package dev.reference.risk;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

import static dev.reference.risk.Decimals.decimal;
import static dev.reference.risk.Model.*;

public final class Demo {
    private Demo() {}
    public static void main(String[] args) {
        Instant now = Instant.parse("2026-10-09T14:30:00Z");
        Instrument instrument = new Instrument("ACME", "USD", decimal("0.01"), decimal("1"),
            decimal("100"), decimal("1000"));
        RiskEngine engine = new RiskEngine(RiskConfig.conservativeEquities(Map.of("ACME", instrument)));
        RiskState state = new RiskState("demo", "USD", 0, decimal("10000"),
            Map.of("ACME", decimal("50")), Map.of());
        MarketSnapshot market = new MarketSnapshot(now,
            Map.of("ACME", new Quote("ACME", "USD", decimal("99.90"), decimal("100"), now)));
        Order order = new Order("stop-001", "demo", "ACME", "USD", Side.BUY, OrderType.STOP,
            decimal("10"), null, decimal("105"), Trigger.TRANSACTION, Handling.HELD, now, null);
        RiskLedger ledger = new RiskLedger(engine, state);
        RiskLedger.Submission submission = ledger.submit(order, market);
        System.out.println("Submission: " + submission.status());
        System.out.println("Required buying power: " + submission.decision().exposure().requiredBuyingPower().toPlainString());
        System.out.println("Available buying power: " + ledger.availableBuyingPower().toPlainString());
        StopTriggerService triggers = new StopTriggerService();
        var event = new StopTriggerService.TradeEvent("ACME", "USD", decimal("105"), now.plusSeconds(1));
        var activation = triggers.evaluate(order, event, now.plusSeconds(1), Duration.ofSeconds(5));
        System.out.println("Activation: " + activation.status() + " -> " + activation.executionOrder().type());
        System.out.println("Activation is not execution; the stop price is not a guaranteed fill price.");
    }
}
