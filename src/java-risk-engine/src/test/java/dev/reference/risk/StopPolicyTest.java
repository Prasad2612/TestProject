package dev.reference.risk;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static dev.reference.risk.Decimals.decimal;
import static dev.reference.risk.Fixtures.*;
import static dev.reference.risk.Model.*;
import static dev.reference.risk.RiskDecision.Code;
import static org.junit.jupiter.api.Assertions.*;

class StopPolicyTest {
    private final RiskEngine alternatives = new RiskEngine(config("100", "1000", false, true, false, RiskConfig.EvaluationMode.COLLECT_ALL));
    private Order quoteOrder() { return order("q", Side.BUY, OrderType.QUOTE_TRIGGERED_MARKET, "1", null, "11"); }
    private RiskDecision validate(Order order) { return alternatives.validate(order, state("1000", "10"), market()); }

    @Test void restingStopNeedNotBeTriggeredToPassPreTrade() {
        var result = new RiskEngine(config()).validate(order("s", Side.BUY, OrderType.STOP, "1", null, "20"), state("1000", "0"), market());
        assertTrue(result.accepted());
    }
    @ParameterizedTest @EnumSource(value = OrderType.class, names = {"STOP", "STOP_LIMIT"})
    void notHeldStopsAreExplicitlyUnsupportedNotMisclassifiedAsRuleViolations(OrderType type) {
        Order order = order("s", Side.BUY, type, "1", type.limitBased() ? "11" : null, "11");
        var result = validate(replace(order, Trigger.TRANSACTION, Handling.NOT_HELD, null));
        assertTrue(has(result, Code.NOT_HELD_UNSUPPORTED));
    }
    @Test void firmMayDeclineStandardStops() {
        RiskConfig config = new RiskConfig(config().instruments(), 2, decimal("0"), decimal("0"), AGE,
            false, false, false, true, false, RiskConfig.EvaluationMode.COLLECT_ALL);
        assertTrue(has(new RiskEngine(config).validate(order("s", Side.BUY, OrderType.STOP, "1", null, "11"),
            state("1000", "0"), market()), Code.STOPS_DISABLED));
    }
    @Test void alternativeQuoteOrdersNeedExplicitOptIn() {
        assertTrue(has(new RiskEngine(config()).validate(quoteOrder(), state("1000", "0"), market()), Code.ALTERNATIVE_TRIGGER_DISABLED));
        assertTrue(validate(quoteOrder()).accepted());
    }
    @Test void alternativeQuoteOrderRequiresDisclosure() {
        var order = quoteOrder();
        assertTrue(has(validate(replace(order, order.trigger(), order.handling(), null)), Code.DISCLOSURE_REQUIRED));
    }
    @Test void disclosureMustPrecedePlacementStrictly() {
        Order order = quoteOrder();
        for (var time : new java.time.Instant[]{NOW, NOW.plusNanos(1)}) {
            var disclosure = new Disclosure(order.type(), order.trigger(), time, "Ask-based threshold.", true);
            assertTrue(has(validate(replace(order, order.trigger(), order.handling(), disclosure)), Code.DISCLOSURE_TOO_LATE));
        }
        var early = new Disclosure(order.type(), order.trigger(), NOW.minusNanos(1), "Ask-based threshold.", true);
        assertTrue(validate(replace(order, order.trigger(), order.handling(), early)).accepted());
    }
    @Test void disclosureTypeAndTriggerMustMatch() {
        Order order = quoteOrder();
        var mismatch = new Disclosure(OrderType.QUOTE_TRIGGERED_LIMIT, Trigger.TRANSACTION, NOW.minusSeconds(1),
            "Wrong description.", true);
        assertTrue(has(validate(replace(order, order.trigger(), order.handling(), mismatch)), Code.DISCLOSURE_MISMATCH));
    }
    @Test void onlineTradingRequiresWebsiteDisclosure() {
        Order order = quoteOrder();
        var disclosure = new Disclosure(order.type(), order.trigger(), NOW.minusSeconds(1), "Ask-based threshold.", false);
        assertTrue(has(validate(replace(order, order.trigger(), order.handling(), disclosure)), Code.WEBSITE_DISCLOSURE_REQUIRED));
    }
    @Test void offlineTradingDoesNotRequireWebsiteButStillNeedsAdvanceDisclosure() {
        RiskConfig config = new RiskConfig(config().instruments(), 2, decimal("0"), decimal("0"), AGE,
            false, true, true, false, false, RiskConfig.EvaluationMode.COLLECT_ALL);
        Order order = quoteOrder();
        var disclosure = new Disclosure(order.type(), order.trigger(), NOW.minusSeconds(1), "Ask-based threshold.", false);
        assertTrue(new RiskEngine(config).validate(replace(order, order.trigger(), order.handling(), disclosure),
            state("1000", "0"), market()).accepted());
    }
    @Test void optionalStopPlacementDirectionIsSeparateFromFinraDefinition() {
        Order order = order("s", Side.BUY, OrderType.STOP, "1", null, "10");
        assertTrue(validate(order).accepted());
        RiskEngine strict = new RiskEngine(config("100", "1000", false, true, true, RiskConfig.EvaluationMode.COLLECT_ALL));
        assertTrue(has(strict.validate(order, state("1000", "0"), market()), Code.STOP_PLACEMENT_DIRECTION));
        assertTrue(has(strict.validate(order, state("1000", "0"), new MarketSnapshot(NOW, Map.of())), Code.MISSING_QUOTE));
    }
}
