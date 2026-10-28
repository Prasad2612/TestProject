package dev.reference.risk;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static dev.reference.risk.Decimals.decimal;
import static dev.reference.risk.Fixtures.*;
import static dev.reference.risk.Model.*;
import static dev.reference.risk.RiskDecision.*;
import static org.junit.jupiter.api.Assertions.*;

class RiskEngineTest {
    private final RiskEngine engine = new RiskEngine(config());

    @Test void buyingPowerAcceptsExactBoundary() {
        var result = engine.validate(buy("o", "10"), state("100", "0"), market());
        assertTrue(result.accepted());
        assertEquals(decimal("100"), result.exposure().requiredBuyingPower());
    }
    @Test void buyingPowerRejectsOneCentBelowBoundary() {
        assertTrue(has(engine.validate(buy("o", "10"), state("99.99", "0"), market()), Code.INSUFFICIENT_BUYING_POWER));
    }
    @ParameterizedTest @CsvSource({"100,0,true", "101,0,false", "1,999,true", "1,1000,false"})
    void orderAndPositionBoundaries(String qty, String position, boolean accepted) {
        assertEquals(accepted, engine.validate(buy("o", qty), state("100000", position), market()).accepted());
    }
    @Test void pendingBuyAndSellOrdersAreNotNettedForWorstCasePosition() {
        Order pendingBuy = buy("b", "100");
        Order pendingSell = order("s", Side.SELL, OrderType.LIMIT, "100", "10", null);
        RiskState state = new RiskState("A1", "USD", 0, decimal("100000"), Map.of("ACME", decimal("900")),
            Map.of("b", new Reservation(pendingBuy, decimal("1000")), "s", new Reservation(pendingSell, decimal("0"))));
        var result = engine.validate(buy("new", "1"), state, market());
        assertTrue(has(result, Code.POSITION_LIMIT));
        assertEquals(decimal("800"), result.exposure().minimumPosition());
        assertEquals(decimal("1001"), result.exposure().maximumPosition());
    }
    @Test void pendingBuysCannotFundOrCoverPendingSells() {
        RiskState state = new RiskState("A1", "USD", 0, decimal("100000"), Map.of(),
            Map.of("b", new Reservation(buy("b", "100"), decimal("1000"))));
        var result = engine.validate(order("s", Side.SELL, OrderType.LIMIT, "1", "10", null), state, market());
        assertTrue(has(result, Code.SHORT_SELLING_DISABLED));
    }
    @Test void sellsDoNotCreditBuyingPower() {
        RiskState state = new RiskState("A1", "USD", 0, decimal("10"), Map.of("ACME", decimal("100")),
            Map.of("s", new Reservation(order("s", Side.SELL, OrderType.LIMIT, "100", "10", null), decimal("0"))));
        assertTrue(has(engine.validate(buy("b", "2"), state, market()), Code.INSUFFICIENT_BUYING_POWER));
    }
    @Test void shortSellingCanBeExplicitlyEnabledButStillHasPositionLimit() {
        RiskEngine shorts = new RiskEngine(config("100", "10", true, false, false, RiskConfig.EvaluationMode.COLLECT_ALL));
        assertTrue(shorts.validate(order("s", Side.SELL, OrderType.LIMIT, "10", "10", null), state("100", "0"), market()).accepted());
        assertTrue(has(shorts.validate(order("s", Side.SELL, OrderType.LIMIT, "11", "10", null), state("100", "0"), market()), Code.POSITION_LIMIT));
    }
    @Test void fractionalQuantityAndSubcentTickAreExact() {
        Instrument instrument = new Instrument("ACME", "USD", decimal("0.0001"), decimal("0.001"),
            decimal("100"), decimal("1000"));
        RiskConfig config = new RiskConfig(Map.of("ACME", instrument), 2, decimal("0"), decimal("0"),
            AGE, false, true, false, true, false, RiskConfig.EvaluationMode.COLLECT_ALL);
        var result = new RiskEngine(config).validate(order("o", Side.BUY, OrderType.LIMIT, "0.003", "1.2345", null),
            state("1", "0"), market());
        assertTrue(result.accepted());
        assertEquals(decimal("0.0037035"), result.exposure().exactNotional());
        assertEquals(decimal("0.01"), result.exposure().requiredBuyingPower());
    }
    @Test void marketAndStopBuysIncludeExactBufferAndFees() {
        RiskConfig config = new RiskConfig(config().instruments(), 2, decimal("100"), decimal("5"),
            AGE, false, true, false, true, false, RiskConfig.EvaluationMode.COLLECT_ALL);
        RiskEngine buffered = new RiskEngine(config);
        var marketBuy = buffered.validate(order("m", Side.BUY, OrderType.MARKET, "10", null, null), state("10000", "0"), market());
        assertEquals(decimal("101"), marketBuy.exposure().exactNotional());
        assertEquals(decimal("101.06"), marketBuy.exposure().requiredBuyingPower());
        var stopBuy = buffered.validate(order("s", Side.BUY, OrderType.STOP, "10", null, "12"), state("10000", "0"), market());
        assertEquals(decimal("12.12"), stopBuy.exposure().valuationPrice());
        assertEquals(decimal("121.27"), stopBuy.exposure().requiredBuyingPower());
    }
    @Test void stopLimitUsesLimitNotStopAsCashCap() {
        var result = engine.validate(order("s", Side.BUY, OrderType.STOP_LIMIT, "10", "13", "12"),
            state("1000", "0"), new MarketSnapshot(NOW, Map.of()));
        assertTrue(result.accepted());
        assertEquals(decimal("130"), result.exposure().requiredBuyingPower());
    }
    @Test void existingReservationsReduceAvailableCash() {
        RiskState state = new RiskState("A1", "USD", 0, decimal("100"), Map.of(),
            Map.of("old", new Reservation(buy("old", "9"), decimal("90"))));
        var result = engine.validate(buy("new", "2"), state, market());
        assertEquals(decimal("10"), result.exposure().availableBuyingPower());
        assertTrue(has(result, Code.INSUFFICIENT_BUYING_POWER));
    }
    @ParameterizedTest @ValueSource(strings = {"0", "-1", "1.5"})
    void badQuantitiesRejectBeforeValuation(String qty) {
        var result = engine.validate(buy("o", qty), state("100", "0"), market());
        assertTrue(has(result, Code.INVALID_QUANTITY));
        assertNull(result.exposure());
    }
    @ParameterizedTest @ValueSource(strings = {"0", "-1", "10.001"})
    void badPricesRejectBeforeValuation(String price) {
        assertTrue(has(engine.validate(order("o", Side.BUY, OrderType.LIMIT, "1", price, null), state("100", "0"), market()), Code.INVALID_PRICE));
    }
    @Test void shapeMismatchAndQuoteTriggerUnderStopLabelFailClosed() {
        var malformed = order("o", Side.BUY, OrderType.MARKET, "1", "10", null);
        assertTrue(has(engine.validate(malformed, state("100", "0"), market()), Code.INVALID_ORDER_SHAPE));
        Order stop = order("s", Side.BUY, OrderType.STOP, "1", null, "11");
        assertTrue(has(engine.validate(replace(stop, Trigger.QUOTATION, Handling.HELD, null), state("100", "0"), market()), Code.INVALID_TRIGGER));
    }
    @Test void accountCurrencyInstrumentAndFuturePlacementAreChecked() {
        Order o = new Order("o", "OTHER", "UNKNOWN", "EUR", Side.BUY, OrderType.LIMIT,
            decimal("1"), decimal("10"), null, Trigger.NONE, Handling.HELD, NOW.plusNanos(1), null);
        var result = engine.validate(o, state("100", "0"), market());
        assertTrue(has(result, Code.ACCOUNT_MISMATCH));
        assertTrue(has(result, Code.CURRENCY_MISMATCH));
        assertTrue(has(result, Code.UNKNOWN_INSTRUMENT));
        assertTrue(has(result, Code.FUTURE_ORDER));
    }
    @Test void missingQuoteIsRequiredForMarketButNotLimit() {
        MarketSnapshot empty = new MarketSnapshot(NOW, Map.of());
        assertTrue(engine.validate(buy("l", "1"), state("100", "0"), empty).accepted());
        assertTrue(has(engine.validate(order("m", Side.BUY, OrderType.MARKET, "1", null, null), state("100", "0"), empty), Code.MISSING_QUOTE));
    }
    @ParameterizedTest @CsvSource({"-5000000000,true", "-5000000001,false", "1,false"})
    void quoteFreshnessBoundaryAndFutureTimestamp(long nanos, boolean accepted) {
        Quote quote = new Quote("ACME", "USD", decimal("9.99"), decimal("10"), NOW.plusNanos(nanos));
        var result = engine.validate(order("m", Side.BUY, OrderType.MARKET, "1", null, null),
            state("100", "0"), new MarketSnapshot(NOW, Map.of("ACME", quote)));
        assertEquals(accepted, result.accepted());
        if (!accepted) assertTrue(has(result, nanos > 0 ? Code.FUTURE_QUOTE : Code.STALE_QUOTE));
    }
    @Test void quoteCurrencyMustMatchAccount() {
        Quote quote = new Quote("ACME", "EUR", decimal("9.99"), decimal("10"), NOW);
        assertTrue(has(engine.validate(order("m", Side.BUY, OrderType.MARKET, "1", null, null),
            state("100", "0"), new MarketSnapshot(NOW, Map.of("ACME", quote))), Code.QUOTE_CURRENCY_MISMATCH));
    }
    @Test void diagnosticOrderAndFailFastAreConfigurable() {
        var orderedRules = List.<RiskRule>of(StandardRules.BUYING_POWER, StandardRules.POSITION_LIMIT, StandardRules.STOP_POLICY);
        RiskEngine collecting = new RiskEngine(config(), orderedRules);
        var collected = collecting.validate(buy("o", "101"), state("0", "1000"), market());
        assertEquals(List.of(Code.INSUFFICIENT_BUYING_POWER, Code.ORDER_QUANTITY_LIMIT, Code.POSITION_LIMIT),
            collected.violations().stream().map(Violation::code).toList());
        RiskEngine fast = new RiskEngine(config("100", "1000", false, false, false, RiskConfig.EvaluationMode.FAIL_FAST), orderedRules);
        var result = fast.validate(buy("o", "101"), state("0", "1000"), market());
        assertEquals(List.of(Code.INSUFFICIENT_BUYING_POWER), result.violations().stream().map(Violation::code).toList());
    }
    @Test void customPureRuleCanExtendPipeline() {
        RiskRule custom = new RiskRule() {
            @Override public String id() { return "RESTRICTED_SYMBOL"; }
            @Override public List<Violation> evaluate(Context context) {
                return context.order().symbol().equals("ACME")
                    ? List.of(new Violation(id(), Code.CUSTOM_RULE, "ACME is on the restricted list")) : List.of();
            }
        };
        RiskEngine extended = new RiskEngine(config(), List.of(custom, StandardRules.STOP_POLICY,
            StandardRules.POSITION_LIMIT, StandardRules.BUYING_POWER));
        assertTrue(has(extended.validate(buy("o", "1"), state("100", "0"), market()), Code.CUSTOM_RULE));
    }
}
