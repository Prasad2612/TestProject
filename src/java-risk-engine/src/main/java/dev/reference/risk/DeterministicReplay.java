package dev.reference.risk;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import static dev.reference.risk.Model.*;

/** Explicit ordering gives reproducible winners when replaying competing orders. */
public final class DeterministicReplay {
    private DeterministicReplay() {}
    public record ReplayResult(List<RiskLedger.Submission> submissions, RiskState finalState) {
        public ReplayResult {
            submissions = List.copyOf(submissions);
            Objects.requireNonNull(finalState);
        }
    }
    public static ReplayResult run(RiskEngine engine, RiskState initialState,
                                   MarketSnapshot market, List<Order> orders) {
        List<Order> sorted = new ArrayList<>(List.copyOf(orders));
        HashSet<String> ids = new HashSet<>();
        for (Order order : sorted)
            if (!ids.add(order.id())) throw new IllegalArgumentException("duplicate replay order id");
        sorted.sort(Comparator.comparing(Order::placedAt).thenComparing(Order::id));
        RiskLedger ledger = new RiskLedger(engine, initialState);
        List<RiskLedger.Submission> results = new ArrayList<>();
        for (Order order : sorted) results.add(ledger.submit(order, market));
        return new ReplayResult(results, ledger.snapshot());
    }
}
