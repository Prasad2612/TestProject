package dev.reference.risk;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import static dev.reference.risk.Decimals.*;

public record RiskDecision(String orderId, long stateVersion, List<Violation> violations,
                           Exposure exposure) {
    public enum Code {
        ACCOUNT_MISMATCH, CURRENCY_MISMATCH, UNKNOWN_INSTRUMENT, FUTURE_ORDER,
        INVALID_QUANTITY, INVALID_PRICE, INVALID_ORDER_SHAPE, INVALID_TRIGGER,
        DUPLICATE_ORDER, MISSING_QUOTE, STALE_QUOTE, FUTURE_QUOTE,
        QUOTE_CURRENCY_MISMATCH, ORDER_QUANTITY_LIMIT, POSITION_LIMIT, SHORT_SELLING_DISABLED,
        INSUFFICIENT_BUYING_POWER, STOPS_DISABLED, ALTERNATIVE_TRIGGER_DISABLED,
        NOT_HELD_UNSUPPORTED, DISCLOSURE_REQUIRED, DISCLOSURE_MISMATCH,
        DISCLOSURE_TOO_LATE, WEBSITE_DISCLOSURE_REQUIRED, STOP_PLACEMENT_DIRECTION,
        CUSTOM_RULE
    }
    public record Violation(String rule, Code code, String detail) {
        public Violation {
            rule = Model.text(rule, "rule");
            Objects.requireNonNull(code);
            detail = Model.text(detail, "detail");
        }
    }
    public record Exposure(BigDecimal minimumPosition, BigDecimal maximumPosition,
                           BigDecimal valuationPrice, BigDecimal exactNotional,
                           BigDecimal requiredBuyingPower, BigDecimal availableBuyingPower) {
        public Exposure {
            minimumPosition = canonical(minimumPosition);
            maximumPosition = canonical(maximumPosition);
            valuationPrice = canonical(valuationPrice);
            exactNotional = canonical(exactNotional);
            requiredBuyingPower = canonical(requiredBuyingPower);
            availableBuyingPower = canonical(availableBuyingPower);
        }
    }

    public RiskDecision {
        orderId = Model.text(orderId, "orderId");
        violations = List.copyOf(violations);
        if (stateVersion < 0) throw new IllegalArgumentException("stateVersion");
        if (violations.isEmpty()) Objects.requireNonNull(exposure, "accepted exposure");
    }
    public boolean accepted() { return violations.isEmpty(); }
}
