# Java Risk Engine Verification

The reference project was compiled and exercised in the task workspace on October 9, 2026 Pacific time (October 10 UTC). The results below describe checks actually executed, rather than the additional Java 21 CI job configured for future runs.

## Environment

- **Runtime/compiler:** Ubuntu OpenJDK 17.0.20.1, 64-bit.
- **Build tool:** Apache Maven 3.9.12.
- **Language target:** Java 17.
- **Test framework:** JUnit Jupiter 5.11.4.
- **Maven test plugin:** Surefire 3.5.2.

## Executed checks

- **Clean build:** `mvn -B -ntp clean verify` passed, compiling 11 production and 8 test/support Java files.
- **Unit/integration scenarios:** 100 parameter-expanded test executions passed, with zero failures, errors, or skipped tests.
- **JUnit parallel scheduling:** The complete suite passed again with parallel execution enabled and a fixed parallelism of 8; total remained 100 passed.
- **Clean rebuild:** A subsequent full clean verification passed again.
- **Runnable demo:** The executable JAR accepted and reserved a 10-share buy stop; required buying power was exactly `1061.04`, remaining buying power was `8938.96`, and a threshold trade activated a market-order descriptor.
- **Build reproducibility:** Two clean builds in the same toolchain yielded the identical SHA-256 below.
- **Source audit:** Production sources contain no floating-point financial calculations, current-time reads, sleeps, or unfinished TODO/FIXME placeholders.

## Test breakdown

| Suite | Parameter-expanded executions |
| --- | ---: |
| RiskEngineTest | 29 |
| RiskLedgerTest | 5 |
| ConcurrencyDeterminismTest | 17 |
| StopTriggerServiceTest | 16 |
| DecimalsTest | 13 |
| StopPolicyTest | 11 |
| ImmutabilityTest | 9 |
| Total | 100 |

Counts above are from the ordinary clean build. Parallel Surefire console output may interleave suite reporting; the overall result was still 100 successful executions.

## Thread scenarios

- **Pure risk evaluation:** 256 fixed orders, four repetitions at each of 1, 2, 8, and 16 workers; all 4,096 returned decisions matched their sequential baseline as complete records.
- **Buying-power contention:** Five rounds of 48 submissions at each of 2, 8, and 16 workers; each round reserved exactly 10 affordable orders and rejected 38 without oversubscribing cash.
- **Position contention:** 32 one-share submissions at each of 2, 8, and 16 workers against a 10-share limit; each run reserved exactly 10 shares.
- **Duplicate retry contention:** 64 identical submissions at 16 workers reserved once and returned 63 already-reserved responses.
- **Cancellation contention:** 64 releases at 16 workers removed the reservation exactly once.
- **Stop activation:** 512 evaluations at 16 workers returned equal activation records.
- **Ordered replay:** 32 seeded permutations at each of 1, 4, and 16 workers returned identical winners, decisions, versions, and final state.

Live race winner identities are intentionally not asserted. Reproducible winners require a specified ordering policy, as supplied by `DeterministicReplay`.

## Included binary

File: `dist/java-risk-engine-1.0.0.jar`

```text
SHA-256:
3e0b107c3f8060555f38d6950829dd13a2d56365db42f80da32258bba1eee419
```

The JAR has no external runtime dependencies and can be run with `java -jar`. Matching hashes were verified with the same JDK and Maven; byte-identical output across different compiler versions is not claimed.

## Limits of verification

Java 21 has not been executed in this workspace; the included CI matrix is configured for Java 17 and 21. The tests demonstrate the stated decimal, immutable-state, triggering, replay, and in-process reservation behavior; they do not certify regulatory compliance, external market-data eligibility, distributed coordination, or execution/fill accounting.
