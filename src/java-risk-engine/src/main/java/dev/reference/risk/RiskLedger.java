package dev.reference.risk;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicReference;

import static dev.reference.risk.Model.*;
import static dev.reference.risk.RiskDecision.*;

/** In-process linearizable validate-and-reserve store. Not a distributed ledger. */
public final class RiskLedger {
    public enum Status { RESERVED, ALREADY_RESERVED, REJECTED }
    public record Submission(Status status, RiskDecision decision, RiskState state) {}
    private final RiskEngine engine;
    private final AtomicReference<RiskState> state;

    public RiskLedger(RiskEngine engine, RiskState initialState) {
        this.engine = Objects.requireNonNull(engine);
        this.state = new AtomicReference<>(Objects.requireNonNull(initialState));
    }

    public RiskState snapshot() { return state.get(); }

    public Submission submit(Order order, MarketSnapshot market) {
        Objects.requireNonNull(order);
        Objects.requireNonNull(market);
        while (true) {
            RiskState before = state.get();
            Reservation existing = before.reservations().get(order.id());
            if (existing != null) {
                if (existing.order().equals(order))
                    return new Submission(Status.ALREADY_RESERVED, null, before);
                RiskDecision rejected = new RiskDecision(order.id(), before.version(),
                    List.of(new Violation("LEDGER", Code.DUPLICATE_ORDER, "id reused with different payload")), null);
                return new Submission(Status.REJECTED, rejected, before);
            }
            RiskDecision decision = engine.validate(order, before, market);
            if (!decision.accepted()) return new Submission(Status.REJECTED, decision, before);
            TreeMap<String, Reservation> reservations = new TreeMap<>(before.reservations());
            reservations.put(order.id(), new Reservation(order, decision.exposure().requiredBuyingPower()));
            RiskState after = new RiskState(before.accountId(), before.currency(), Math.incrementExact(before.version()),
                before.buyingPower(), before.positions(), reservations);
            if (state.compareAndSet(before, after)) return new Submission(Status.RESERVED, decision, after);
            // Another order won. Revalidate ALL controls against the new state.
        }
    }

    /** Cancellation/expiry only. A fill must not be processed by simply releasing its reserve. */
    public boolean release(String orderId) {
        Model.text(orderId, "orderId");
        while (true) {
            RiskState before = state.get();
            if (!before.reservations().containsKey(orderId)) return false;
            TreeMap<String, Reservation> reservations = new TreeMap<>(before.reservations());
            reservations.remove(orderId);
            RiskState after = new RiskState(before.accountId(), before.currency(), Math.incrementExact(before.version()),
                before.buyingPower(), before.positions(), reservations);
            if (state.compareAndSet(before, after)) return true;
        }
    }

    public BigDecimal availableBuyingPower() { return snapshot().availableBuyingPower(); }
}
