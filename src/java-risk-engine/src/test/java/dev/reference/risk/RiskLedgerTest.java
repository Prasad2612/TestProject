package dev.reference.risk;

import org.junit.jupiter.api.Test;

import static dev.reference.risk.Decimals.decimal;
import static dev.reference.risk.Fixtures.*;
import static dev.reference.risk.RiskDecision.Code;
import static dev.reference.risk.RiskLedger.Status;
import static org.junit.jupiter.api.Assertions.*;

class RiskLedgerTest {
    @Test void reservationUpdatesVersionButNotPriorSnapshot() {
        var initial = state("100", "0");
        var ledger = new RiskLedger(new RiskEngine(config()), initial);
        var result = ledger.submit(buy("b", "3"), market());
        assertEquals(Status.RESERVED, result.status());
        assertEquals(1, ledger.snapshot().version());
        assertEquals(decimal("70"), ledger.availableBuyingPower());
        assertTrue(initial.reservations().isEmpty());
        assertEquals(decimal("100"), initial.availableBuyingPower());
        assertEquals(initial, state("100", "0"));
    }
    @Test void idempotentSubmissionDoesNotReserveTwice() {
        var ledger = new RiskLedger(new RiskEngine(config()), state("100", "0"));
        ledger.submit(buy("b", "3.00"), market());
        var retry = ledger.submit(buy("b", "3"), market());
        assertEquals(Status.ALREADY_RESERVED, retry.status());
        assertNull(retry.decision());
        assertEquals(1, ledger.snapshot().version());
        assertEquals(decimal("70"), ledger.availableBuyingPower());
    }
    @Test void conflictingReuseOfOrderIdIsRejected() {
        var ledger = new RiskLedger(new RiskEngine(config()), state("100", "0"));
        ledger.submit(buy("b", "3"), market());
        var conflict = ledger.submit(buy("b", "4"), market());
        assertEquals(Status.REJECTED, conflict.status());
        assertTrue(has(conflict.decision(), Code.DUPLICATE_ORDER));
        assertEquals(decimal("70"), ledger.availableBuyingPower());
    }
    @Test void rejectionDoesNotMutateState() {
        var initial = state("0", "0");
        var ledger = new RiskLedger(new RiskEngine(config()), initial);
        assertEquals(Status.REJECTED, ledger.submit(buy("b", "1"), market()).status());
        assertSame(initial, ledger.snapshot());
    }
    @Test void cancellationReleasesExactlyOnceAndRestoresCapacity() {
        var ledger = new RiskLedger(new RiskEngine(config()), state("100", "0"));
        ledger.submit(buy("b", "10"), market());
        assertTrue(ledger.release("b"));
        assertFalse(ledger.release("b"));
        assertEquals(2, ledger.snapshot().version());
        assertEquals(decimal("100"), ledger.availableBuyingPower());
        assertEquals(Status.RESERVED, ledger.submit(buy("new-id", "10"), market()).status());
    }
}
