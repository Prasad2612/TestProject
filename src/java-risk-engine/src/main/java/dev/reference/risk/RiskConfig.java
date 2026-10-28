package dev.reference.risk;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;

import static dev.reference.risk.Decimals.*;
import static dev.reference.risk.Model.*;

public record RiskConfig(Map<String, Instrument> instruments, int currencyScale,
                         BigDecimal slippageBasisPoints, BigDecimal feeBasisPoints,
                         Duration maxQuoteAge, boolean allowShortSelling,
                         boolean acceptStandardStops, boolean acceptQuoteTriggeredOrders,
                         boolean onlineTrading, boolean enforceStopPlacementDirection,
                         EvaluationMode mode) {
    public enum EvaluationMode { COLLECT_ALL, FAIL_FAST }

    public RiskConfig {
        instruments = Model.immutableSorted(instruments);
        if (instruments.isEmpty()) throw new IllegalArgumentException("no instruments");
        instruments.forEach((key, instrument) -> {
            if (!key.equals(instrument.symbol())) throw new IllegalArgumentException("instrument key mismatch");
        });
        if (currencyScale < 0 || currencyScale > 12) throw new IllegalArgumentException("currencyScale");
        slippageBasisPoints = nonNegative(slippageBasisPoints, "slippageBasisPoints");
        feeBasisPoints = nonNegative(feeBasisPoints, "feeBasisPoints");
        Objects.requireNonNull(maxQuoteAge);
        if (maxQuoteAge.isNegative() || maxQuoteAge.isZero()) throw new IllegalArgumentException("maxQuoteAge");
        Objects.requireNonNull(mode);
    }

    public static RiskConfig conservativeEquities(Map<String, Instrument> instruments) {
        return new RiskConfig(instruments, 2, decimal("100"), decimal("5"),
            Duration.ofSeconds(5), false, true, false, true, false, EvaluationMode.COLLECT_ALL);
    }
}
