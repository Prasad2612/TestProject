package dev.reference.risk;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static dev.reference.risk.Decimals.*;
import static org.junit.jupiter.api.Assertions.*;

class DecimalsTest {
    @Test void exactAdditionAvoidsBinaryFloatingPointError() {
        assertEquals(decimal("0.3"), canonical(decimal("0.1").add(decimal("0.2"))));
    }
    @Test void exactMultiplicationHasNoPrecisionContextTruncation() {
        assertEquals(decimal("121932631137021494965704638000.15217849"),
            canonical(decimal("12345678901234567890.123").multiply(decimal("9876543210.98763"))));
    }
    @ParameterizedTest @ValueSource(strings = {"10", "10.0", "10.00", "1E+1"})
    void canonicalValuesHaveIdenticalScaleAndEquality(String input) {
        assertEquals(new BigDecimal("10"), decimal(input));
    }
    @Test void zeroNormalizesIncludingNegativeZero() {
        assertEquals(BigDecimal.ZERO, decimal("-0.000"));
    }
    @ParameterizedTest @CsvSource({"1.00001,1.01", "1.01,1.01", "0.0000001,0.01", "0,0"})
    void reserveRoundsUpOnlyAtBoundary(String exact, String expected) {
        assertEquals(decimal(expected), reserve(decimal(exact), 2));
    }
    @Test void basisPointDivisionTerminatesExactly() {
        assertEquals(decimal("1.000125"), factor(decimal("1.25")));
    }
    @Test void stepAlignmentIsExact() {
        assertTrue(aligned(decimal("100.125"), decimal("0.005")));
        assertFalse(aligned(decimal("100.126"), decimal("0.005")));
    }
}
