package dev.reference.risk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

import static dev.reference.risk.Decimals.decimal;
import static dev.reference.risk.Fixtures.*;
import static dev.reference.risk.Model.*;
import static org.junit.jupiter.api.Assertions.*;

class ImmutabilityTest {
    @Test void positionsAreDefensivelyCopiedAndUnmodifiable() {
        var input = new HashMap<String, java.math.BigDecimal>();
        input.put("ACME", decimal("5.00"));
        RiskState state = new RiskState("A1", "USD", 0, decimal("1000"), input, Map.of());
        input.put("ACME", decimal("999"));
        assertEquals(decimal("5"), state.positions().get("ACME"));
        assertThrows(UnsupportedOperationException.class, () -> state.positions().put("OTHER", decimal("1")));
    }
    @Test void reservationsAreDefensivelyCopiedAndUnmodifiable() {
        var input = new HashMap<String, Reservation>();
        input.put("o", new Reservation(buy("o", "1"), decimal("10")));
        RiskState state = new RiskState("A1", "USD", 0, decimal("1000"), Map.of(), input);
        input.clear();
        assertEquals(1, state.reservations().size());
        assertThrows(UnsupportedOperationException.class, () -> state.reservations().clear());
    }
    @Test void marketAndConfigurationDefensivelyCopyMaps() {
        var quotes = new HashMap<>(market().quotes());
        MarketSnapshot market = new MarketSnapshot(NOW, quotes);
        quotes.clear();
        assertEquals(1, market.quotes().size());
        assertThrows(UnsupportedOperationException.class, () -> market.quotes().clear());
        var instruments = new HashMap<>(config().instruments());
        RiskConfig c = new RiskConfig(instruments, 2, decimal("0"), decimal("0"), AGE,
            false, true, false, true, false, RiskConfig.EvaluationMode.COLLECT_ALL);
        instruments.clear();
        assertEquals(1, c.instruments().size());
        assertThrows(UnsupportedOperationException.class, () -> c.instruments().clear());
    }
    @Test void pipelineIsCopiedAndCannotLoseMandatoryControls() {
        var rules = new ArrayList<RiskRule>(List.of(StandardRules.values()));
        RiskEngine engine = new RiskEngine(config(), rules);
        rules.clear();
        assertEquals(3, engine.rules().size());
        assertThrows(UnsupportedOperationException.class, () -> engine.rules().clear());
        assertThrows(IllegalArgumentException.class, () -> new RiskEngine(config(), List.of(StandardRules.BUYING_POWER)));
        assertThrows(IllegalArgumentException.class, () -> new RiskEngine(config(),
            List.of(StandardRules.STOP_POLICY, StandardRules.POSITION_LIMIT, StandardRules.BUYING_POWER, StandardRules.BUYING_POWER)));
    }
    @Test void equivalentDecimalOrdersHaveEqualRecordsAndHashes() {
        assertEquals(buy("o", "1.0"), buy("o", "1.000"));
        assertEquals(buy("o", "1.0").hashCode(), buy("o", "1.000").hashCode());
    }
    @Test void mapIterationIsCanonicalIndependentOfInsertionOrder() {
        var positions = new HashMap<String, java.math.BigDecimal>();
        positions.put("ZZZ", decimal("1"));
        positions.put("AAA", decimal("2"));
        RiskState state = new RiskState("A1", "USD", 0, decimal("1000"), positions, Map.of());
        assertEquals(List.of("AAA", "ZZZ"), new ArrayList<>(state.positions().keySet()));
    }
    @Test void stateRejectsNegativeCashAndOverReservation() {
        assertThrows(IllegalArgumentException.class, () -> state("-1", "0"));
        assertThrows(IllegalArgumentException.class, () ->
            new RiskState("A1", "USD", 0, decimal("9"), Map.of(),
                Map.of("o", new Reservation(buy("o", "1"), decimal("10")))));
    }
    @Test void stateRejectsMismatchedReservationIdentity() {
        assertThrows(IllegalArgumentException.class, () ->
            new RiskState("A1", "USD", 0, decimal("1000"), Map.of(),
                Map.of("different-id", new Reservation(buy("o", "1"), decimal("10")))));
    }
    @Test void crossedQuotesAndMismatchedQuoteKeysAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Quote("ACME", "USD", decimal("11"), decimal("10"), NOW));
        assertThrows(IllegalArgumentException.class, () -> new MarketSnapshot(NOW, Map.of("BAD", market().quotes().get("ACME"))));
    }
}
