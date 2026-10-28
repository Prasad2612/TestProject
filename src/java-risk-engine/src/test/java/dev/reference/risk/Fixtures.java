package dev.reference.risk;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

import static dev.reference.risk.Decimals.decimal;
import static dev.reference.risk.Model.*;

final class Fixtures {
    static final Instant NOW = Instant.parse("2026-10-09T14:30:00Z");
    static final Duration AGE = Duration.ofSeconds(5);
    private Fixtures() {}

    static Instrument instrument(String orderLimit, String positionLimit) {
        return new Instrument("ACME", "USD", decimal("0.01"), decimal("1"),
            decimal(orderLimit), decimal(positionLimit));
    }
    static RiskConfig config() { return config("100", "1000", false, false, false, RiskConfig.EvaluationMode.COLLECT_ALL); }
    static RiskConfig config(String orderLimit, String positionLimit, boolean shorts,
                             boolean alternatives, boolean direction, RiskConfig.EvaluationMode mode) {
        return new RiskConfig(Map.of("ACME", instrument(orderLimit, positionLimit)), 2,
            decimal("0"), decimal("0"), AGE, shorts, true, alternatives, true, direction, mode);
    }
    static RiskState state(String buyingPower, String position) {
        return new RiskState("A1", "USD", 0, decimal(buyingPower), Map.of("ACME", decimal(position)), Map.of());
    }
    static MarketSnapshot market() {
        return new MarketSnapshot(NOW, Map.of("ACME",
            new Quote("ACME", "USD", decimal("9.99"), decimal("10"), NOW)));
    }
    static Order order(String id, Side side, OrderType type, String quantity, String limit, String stop) {
        Trigger trigger = !type.conditional() ? Trigger.NONE
            : type.standardStop() ? Trigger.TRANSACTION : Trigger.QUOTATION;
        Disclosure disclosure = type.conditional() && !type.standardStop()
            ? new Disclosure(type, Trigger.QUOTATION, NOW.minusSeconds(60),
                "Buy activates on ask at or above threshold; sell on bid at or below threshold.", true) : null;
        return new Order(id, "A1", "ACME", "USD", side, type, decimal(quantity),
            limit == null ? null : decimal(limit), stop == null ? null : decimal(stop),
            trigger, Handling.HELD, NOW, disclosure);
    }
    static Order buy(String id, String quantity) {
        return order(id, Side.BUY, OrderType.LIMIT, quantity, "10", null);
    }
    static Order replace(Order o, Trigger trigger, Handling handling, Disclosure disclosure) {
        return new Order(o.id(), o.accountId(), o.symbol(), o.currency(), o.side(), o.type(),
            o.quantity(), o.limitPrice(), o.stopPrice(), trigger, handling, o.placedAt(), disclosure);
    }
    static boolean has(RiskDecision decision, RiskDecision.Code code) {
        return decision.violations().stream().anyMatch(v -> v.code() == code);
    }
}
