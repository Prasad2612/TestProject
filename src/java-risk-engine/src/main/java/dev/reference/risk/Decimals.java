package dev.reference.risk;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/** Exact decimal operations. No floating-point constructor or implicit MathContext. */
public final class Decimals {
    public static final BigDecimal ZERO = BigDecimal.ZERO;
    private static final BigDecimal BASIS_POINTS = new BigDecimal("10000");

    private Decimals() {}

    public static BigDecimal decimal(String value) {
        return canonical(new BigDecimal(Objects.requireNonNull(value)));
    }

    /** Scale normalization makes numerically equivalent record inputs equal. */
    public static BigDecimal canonical(BigDecimal value) {
        BigDecimal stripped = Objects.requireNonNull(value).stripTrailingZeros();
        return stripped.scale() < 0 ? stripped.setScale(0) : stripped;
    }

    public static BigDecimal positive(BigDecimal value, String field) {
        value = canonical(value);
        if (value.signum() <= 0) throw new IllegalArgumentException(field + " must be positive");
        return value;
    }

    public static BigDecimal nonNegative(BigDecimal value, String field) {
        value = canonical(value);
        if (value.signum() < 0) throw new IllegalArgumentException(field + " must be nonnegative");
        return value;
    }

    public static BigDecimal optional(BigDecimal value) {
        return value == null ? null : canonical(value);
    }

    public static boolean aligned(BigDecimal value, BigDecimal increment) {
        return value.remainder(increment).signum() == 0;
    }

    public static BigDecimal factor(BigDecimal basisPoints) {
        return BigDecimal.ONE.add(basisPoints.divide(BASIS_POINTS));
    }

    /** Rounding is ONLY applied at the cash-reservation boundary, upward for safety. */
    public static BigDecimal reserve(BigDecimal exact, int currencyScale) {
        return canonical(exact.setScale(currencyScale, RoundingMode.CEILING));
    }
}
