package dev.reference.risk;

import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import static dev.reference.risk.Decimals.decimal;
import static dev.reference.risk.Fixtures.*;
import static dev.reference.risk.Model.*;
import static dev.reference.risk.StopTriggerService.*;
import static org.junit.jupiter.api.Assertions.*;

class StopTriggerServiceTest {
    private final StopTriggerService service = new StopTriggerService();
    private Activation trigger(Order order, MarketEvent event) { return service.evaluate(order, event, NOW, AGE); }
    private TradeEvent trade(String price) { return new TradeEvent("ACME", "USD", decimal(price), NOW); }

    @ParameterizedTest @CsvSource({
        "BUY,9.99,NOT_TRIGGERED", "BUY,10,TRIGGERED", "BUY,10.01,TRIGGERED",
        "SELL,10.01,NOT_TRIGGERED", "SELL,10,TRIGGERED", "SELL,9.99,TRIGGERED"
    })
    void standardStopTradeThresholdsAreInclusive(Side side, String price, Status expected) {
        Order order = order("s", side, OrderType.STOP, "1", null, "10");
        var result = trigger(order, trade(price));
        assertEquals(expected, result.status());
        if (expected == Status.TRIGGERED) {
            assertEquals(OrderType.MARKET, result.executionOrder().type());
            assertNull(result.executionOrder().limitPrice());
        }
    }
    @ParameterizedTest @EnumSource(value = OrderType.class, names = {"STOP", "STOP_LIMIT"})
    void quotationCannotTriggerStandardStop(OrderType type) {
        Order order = order("s", Side.BUY, type, "1", type.limitBased() ? "11" : null, "10");
        assertEquals(Status.NOT_TRIGGERED, trigger(order, new QuoteEvent(
            new Quote("ACME", "USD", decimal("10"), decimal("11"), NOW))).status());
    }
    @Test void stopLimitBecomesLimitRetainingLimitPriceEvenWhenTradeGapsAboveLimit() {
        Order order = order("s", Side.BUY, OrderType.STOP_LIMIT, "2", "11", "10");
        var result = trigger(order, trade("50"));
        assertEquals(Status.TRIGGERED, result.status());
        assertEquals(OrderType.LIMIT, result.executionOrder().type());
        assertEquals(decimal("11"), result.executionOrder().limitPrice());
        assertEquals(decimal("2"), result.executionOrder().quantity());
    }
    @Test void quoteAlternativeUsesAskForBuyAndBidForSell() {
        var quote = new QuoteEvent(new Quote("ACME", "USD", decimal("9"), decimal("11"), NOW));
        Order buy = order("b", Side.BUY, OrderType.QUOTE_TRIGGERED_MARKET, "1", null, "10");
        Order sell = order("s", Side.SELL, OrderType.QUOTE_TRIGGERED_LIMIT, "1", "9", "10");
        assertEquals(Status.TRIGGERED, trigger(buy, quote).status());
        var activatedSell = trigger(sell, quote);
        assertEquals(Status.TRIGGERED, activatedSell.status());
        assertEquals(OrderType.LIMIT, activatedSell.executionOrder().type());
        assertEquals(decimal("9"), activatedSell.executionOrder().limitPrice());
        assertEquals(Status.NOT_TRIGGERED, trigger(buy, trade("11")).status());
    }
    @Test void nonStopOrdersAreNotApplicable() {
        assertEquals(Status.NOT_APPLICABLE, trigger(buy("b", "1"), trade("10")).status());
    }
    @Test void invalidDeclaredTriggerAndNotHeldAreUnsupported() {
        Order stop = order("s", Side.BUY, OrderType.STOP, "1", null, "10");
        assertEquals(Status.UNSUPPORTED, trigger(replace(stop, Trigger.QUOTATION, Handling.HELD, null), trade("10")).status());
        assertEquals(Status.UNSUPPORTED, trigger(replace(stop, Trigger.TRANSACTION, Handling.NOT_HELD, null), trade("10")).status());
    }
    @Test void wrongSymbolOrCurrencyEventsAreInvalid() {
        Order stop = order("s", Side.BUY, OrderType.STOP, "1", null, "10");
        assertEquals(Status.INVALID_EVENT, trigger(stop, new TradeEvent("OTHER", "USD", decimal("10"), NOW)).status());
        assertEquals(Status.INVALID_EVENT, trigger(stop, new TradeEvent("ACME", "EUR", decimal("10"), NOW)).status());
    }
    @Test void prePlacementAndFutureEventsAreInvalid() {
        Order stop = order("s", Side.BUY, OrderType.STOP, "1", null, "10");
        for (Instant time : new Instant[]{NOW.minusNanos(1), NOW.plusNanos(1)})
            assertEquals(Status.INVALID_EVENT, trigger(stop, new TradeEvent("ACME", "USD", decimal("10"), time)).status());
    }
    @Test void eventAgeBoundaryIsInclusiveAndOneNanosecondOlderRejects() {
        Order original = order("s", Side.BUY, OrderType.STOP, "1", null, "10");
        Order old = new Order(original.id(), original.accountId(), original.symbol(), original.currency(),
            original.side(), original.type(), original.quantity(), null, original.stopPrice(),
            original.trigger(), original.handling(), NOW.minusSeconds(10), null);
        assertEquals(Status.TRIGGERED, trigger(old, new TradeEvent("ACME", "USD", decimal("10"), NOW.minusSeconds(5))).status());
        assertEquals(Status.INVALID_EVENT, trigger(old, new TradeEvent("ACME", "USD", decimal("10"), NOW.minusSeconds(5).minusNanos(1))).status());
    }
    @Test void activationIsPureAndRepeatableNotAnExactlyOnceDispatchMechanism() {
        Order stop = order("s", Side.BUY, OrderType.STOP, "1", null, "10");
        assertEquals(trigger(stop, trade("10")), trigger(stop, trade("10")));
    }
}
