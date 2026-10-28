# Java Risk Engine Reference

A complete, runnable Java 17 / Maven reference project for exact decimal pre-trade risk checks, immutable account snapshots, and reproducible concurrent validation. The core has no third-party runtime dependencies; JUnit 5 is used for tests. This is an educational equity risk engine, not a certified compliance system or an order-execution platform.

## Quick start

Requirements: JDK 17 or newer and Maven 3.8.6 or newer. The initial Maven build downloads pinned dependencies and plugins.

```bash
mvn -B -ntp clean verify
java -jar target/java-risk-engine-1.0.0.jar
```

Expected demo output:

```text
Submission: RESERVED
Required buying power: 1061.04
Available buying power: 8938.96
Activation: TRIGGERED -> MARKET
Activation is not execution; the stop price is not a guaranteed fill price.
```

The downloadable distribution also includes a prebuilt dependency-free demo JAR:

```bash
java -jar dist/java-risk-engine-1.0.0.jar
```

See `VERIFICATION.md` for the actual build environment, executed checks, and binary checksum.

Run the same suite with JUnit itself scheduling test methods concurrently:

```bash
mvn -B -ntp test \
  -Djunit.jupiter.execution.parallel.enabled=true \
  -Djunit.jupiter.execution.parallel.mode.default=concurrent \
  -Djunit.jupiter.execution.parallel.config.strategy=fixed \
  -Djunit.jupiter.execution.parallel.config.fixed.parallelism=8
```

The concurrency test suite also creates its own executors to check worker counts of 1, 2, 4, 8, and 16 across the relevant scenarios. No sleep, wall-clock pricing, unseeded randomness, or floating-point arithmetic is used.

## Architecture

```text
Order + immutable RiskState + MarketSnapshot(asOf)
                       |
             structural safety checks
      identity / currency / shape / ticks / freshness
                       |
              exact exposure calculation
                       |
            ordered configurable RiskRule list
         STOP_POLICY / POSITION_LIMIT / BUYING_POWER
                       |
           immutable RiskDecision + diagnostics
                       |
         RiskLedger compare-and-set reservation
        retry validation if account state changed

Accepted conditional Order + timestamped MarketEvent
                       |
                StopTriggerService
                       |
          MARKET or LIMIT activation descriptor
        (NOT a routed order and NOT an execution)
```

- **Model.java:** Records for orders, instruments, quotes, snapshots, disclosures, and reservations; maps are defensively copied, sorted, and unmodifiable.
- **Decimals.java:** Canonical BigDecimal inputs, exact tick alignment, exact basis-point factors, and an explicit conservative rounding boundary.
- **RiskConfig.java:** Instrument-specific position/order limits, slippage and fee buffers, market-data freshness, short-selling and stop policies, and evaluation mode.
- **RiskEngine.java:** Pure validation against a snapshot, mandatory structural safeguards, and ordered rule execution.
- **RiskRule.java / StandardRules.java:** A pure extension interface and mandatory position, buying-power, and stop controls.
- **RiskLedger.java:** Linearizable in-process validation and reservation, idempotent retries while a reservation exists, and cancellation/expiry release.
- **StopTriggerService.java:** Transaction-only activation for standard stops and separately labeled, explicitly declared quote-triggered alternatives.
- **DeterministicReplay.java:** Serial replay ordered by placement timestamp and then case-sensitive order ID, independent of input-list order.
- **Demo.java:** Fixed-input end-to-end acceptance, cash reservation, and stop activation.

## Exact arithmetic and risk assumptions

### Decimal representation

Supply financial values as decimal strings, for example `decimal("100.25")`, or as BigDecimal values sourced from exact decimal representations. The API never uses `double`, `float`, or a rounding MathContext. Callers must not construct BigDecimal from a binary floating-point value; already-corrupted inputs cannot be repaired by normalization.

Numbers are normalized with `stripTrailingZeros()` and a nonnegative scale, so `10`, `10.0`, and `1E+1` compare equal as record components. Arithmetic stays exact until the cash-reservation boundary, where positive amounts round upward using `RoundingMode.CEILING` at the configured currency scale. Display formatting belongs outside the risk domain; canonical USD values such as `100` deliberately do not preserve two display decimals.

An instrument's quantity step and price tick are checked with exact remainder, not approximate tolerances. The default examples use a one-share step and a one-cent tick, but fractional quantities and finer ticks are supported.

### Position checks

For each symbol, take the current filled position and independently compute:

```text
maximum possible position = filled position + all pending buys + candidate buy
minimum possible position = filled position - all pending sells - candidate sell
```

Pending buys and sells are not netted. They can fill independently, so offsetting them would understate exposure. Neither pending buys nor anticipated sale proceeds can cover a pending sell or increase buying power.

Both extremes must fit the instrument's absolute position limit, and each new order must fit its quantity limit. In long-only mode the minimum must remain nonnegative. Short selling can be enabled for position arithmetic, but this project does not model short-margin collateral, stock borrow/locate, or Regulation SHO requirements.

Existing over-limit positions fail closed even for reducing orders, because one of the independent fill extremes still includes the over-limit initial position. A production engine may add a controlled liquidation-only exception.

### Buying power

The account stores a single currency and total remaining buying-power budget before subtracting open-order reservations. It does not convert currencies or compute regulatory margin.

```text
LIMIT / STOP_LIMIT / QUOTE_TRIGGERED_LIMIT:
    valuation price = limit price

MARKET:
    valuation price = quote side × (1 + slippage basis points / 10000)

STOP / QUOTE_TRIGGERED_MARKET buy:
    valuation price = max(ask, stop price) × (1 + slippage basis points / 10000)

exact notional = valuation price × quantity
buy reservation = upward_currency_round(exact notional × (1 + fee basis points / 10000))
sell reservation = 0
available buying power = account budget - existing cash reservations
```

For market-like sells the reported reference notional uses bid plus the same valuation buffer, but it does not affect cash reservation. The project does not model sell-side fees or short collateral; do not treat this informational notional as executable proceeds.

The fee is a configurable proportional estimate, not a full broker fee schedule. Market and stop orders have no hard execution-price bound; the slippage buffer is a risk estimate and cannot guarantee affordability after a price gap. Revalidate or resize reservations against fresh executable conditions before routing and integrate fill/cash accounting in a production system.

Limit-based orders need no quote unless optional stop-placement direction checks are enabled. Market-like orders require a quote with matching symbol/currency and age no greater than the configured threshold; future quotes are rejected. Evaluation time is always supplied by the caller as `MarketSnapshot.asOf`.

## FINRA 5350 mapping

The reference uses the actual rule text rather than treating all conditional orders as interchangeable. FINRA permits firms to decline stop and stop-limit orders, and defines standard stop activation by a transaction at the trigger threshold, with conversion to market or limit respectively ([FINRA Rule 5350(a)](https://www.finra.org/rules-guidance/rulebooks/finra-rules/5350)).

- **Standard STOP and STOP_LIMIT:** Require the declared `TRANSACTION` trigger; quote events cannot activate them, and a buy activates at or above the threshold while a sell activates at or below it ([FINRA Rule 5350](https://www.finra.org/rules-guidance/rulebooks/finra-rules/5350), [Regulatory Notice 16-19](https://www.finra.org/rules-guidance/notices/16-19)).
- **Alternative quote triggers:** Use the distinct `QUOTE_TRIGGERED_MARKET` and `QUOTE_TRIGGERED_LIMIT` domain types; supplementary material .01 permits alternative events only with a distinguishable label and pre-order disclosure of the order type and trigger ([FINRA Rule 5350.01](https://www.finra.org/rules-guidance/rulebooks/finra-rules/5350)).
- **Disclosure evidence:** The validator requires a matching type and trigger, nonblank description, and delivery timestamp strictly before order placement; online mode additionally requires website-posting evidence because the rule calls for clear and conspicuous website disclosures ([FINRA Rule 5350.01](https://www.finra.org/rules-guidance/rulebooks/finra-rules/5350)).
- **Not-held orders:** Rule 5350(b) excludes not-held stops; this engine explicitly rejects them as unsupported rather than asserting that the rule's held-order semantics apply ([FINRA Rule 5350(b)](https://www.finra.org/rules-guidance/rulebooks/finra-rules/5350)).
- **Routing responsibilities:** Supplementary material .02 requires reasonable steps for downstream handling; this reference has no router and cannot satisfy that operational duty by itself ([FINRA Rule 5350.02](https://www.finra.org/rules-guidance/rulebooks/finra-rules/5350)).
- **Execution risk:** A stop price is not a guaranteed execution price, and a stop-limit order can fail to execute; the trigger service intentionally emits only an activation descriptor ([FINRA Regulatory Notice 16-19](https://www.finra.org/rules-guidance/notices/16-19)).

The quote policy uses ask for buys and bid for sells. That is a reference firm convention, not a quotation-side requirement specified by Rule 5350; the sample disclosure describes it explicitly. Optional direction enforcement requires buy stops above current ask and sell stops below current bid, and is disabled by default because it is distinct from the rule's trigger definition.

The evidence record is not a compliance attestation. Production systems must validate the actual disclosure content, rendered label, website visibility, delivery proof, and customer/consent identity against durable records. A trusted compliance layer must supply those facts; a client-provided boolean is insufficient.

## Configuration and extensions

Create a configuration in Java so the compiler checks enum values and record shape. There is deliberately no permissive stringly typed JSON loader.

```java
import java.time.Duration;
import java.util.List;
import java.util.Map;
import dev.reference.risk.*;
import static dev.reference.risk.Decimals.decimal;
import static dev.reference.risk.Model.*;

Instrument acme = new Instrument(
    "ACME", "USD", decimal("0.01"), decimal("1"),
    decimal("250"), decimal("2000"));

RiskConfig config = new RiskConfig(
    Map.of("ACME", acme),
    2,                              // currency rounding scale
    decimal("100"),                 // 1% market/stop buffer
    decimal("5"),                   // 0.05% proportional fee estimate
    Duration.ofSeconds(5),
    false,                          // short selling
    true,                           // accept standard stops
    false,                          // quote alternatives require explicit opt-in
    true,                           // online trading
    false,                          // optional placement-direction policy
    RiskConfig.EvaluationMode.COLLECT_ALL);

RiskEngine engine = new RiskEngine(config, List.of(
    StandardRules.STOP_POLICY,
    StandardRules.POSITION_LIMIT,
    StandardRules.BUYING_POWER));
```

Reorder the rules to choose diagnostic priority. `FAIL_FAST` stops after the first failing pipeline rule; that rule may return multiple diagnostics. `COLLECT_ALL` collects all pipeline diagnostics in configured order. Structural and market-data gates always collect their own errors and terminate before arithmetic or extension execution when invalid inputs would make later stages unsafe.

All three standard controls must be present exactly once. Adding a custom rule cannot silently disable a mandatory control or spoof one through the same ID. Implement `RiskRule.id()` and `RiskRule.evaluate(Context)` to add restricted-symbol, account-status, or business-specific controls. Custom rules must return immutable-compatible diagnostics and be pure, thread-safe, stable in ordering, and free of I/O, random values, wall-clock access, and mutable captured state.

Null Java inputs, invalid configuration, or extension exceptions are programming failures and propagate; invalid but well-shaped business orders return a rejected `RiskDecision`. Rules that require live external data should receive it as another immutable snapshot in a deliberate domain extension rather than doing network calls in `evaluate`.

## Concurrency and reproducibility

- **Snapshot validation:** The same order, state, configuration, market snapshot, and pure extension rules produce equal `RiskDecision` records across execution threads. Canonical decimals, explicit timestamps, sorted maps, and stable diagnostic order make equality meaningful.
- **Atomic reservation:** Each successful submission uses compare-and-set to publish a new immutable state; a lost CAS retries validation against the latest state, preventing independent approvals from oversubscribing shared capacity.
- **Idempotency:** An exact order retry while its reservation exists returns `ALREADY_RESERVED`, with `decision == null`; this means already recorded, not a new risk approval. Reuse of its ID with a different payload returns a duplicate-ID rejection.
- **Cancellation:** `release` removes an unfilled reservation atomically and at most once; it is for cancellation/expiry, not trade fills.
- **Race outcomes:** Contending live orders can win in different orders and therefore have different decisions/state versions. Safety is guaranteed, not scheduler-independent identities of winners.
- **Ordered replay:** `DeterministicReplay` sorts by `placedAt`, then ID and applies submissions sequentially; independent replays across threads reproduce winners, decisions, versions, and final state.
- **Activation:** `StopTriggerService` is pure and can return the same activation on repeated events; exactly-once dispatch, replay protection, persistent trigger state, and revalidation before routing belong in a separate lifecycle/router layer.

`RiskLedger` is scoped to one account and one process. Multiple ledger instances initialized from the same account snapshot do not coordinate; use a shared transactional or partitioned state store for distributed deployment. Once a reservation is released, its ID is no longer remembered, so permanent idempotency requires an external durable order journal.

## Test coverage

The suite verifies exact decimal arithmetic; canonical equality; defensive copies; input identity, currency, order shape, increments, and freshness; buying-power and position boundaries; pending-order non-netting; optional short policy; buffers and fees; stop disclosures and not-held exclusions; inclusive transaction thresholds; quote-versus-trade behavior; stop-limit conversion; cancellation and duplicate handling; pure-rule extensions; and ordered replay.

The dedicated thread tests compare full record equality against sequential results, contend on cash and position capacity, race identical retries/cancellations, and replay fixed seeded input permutations. They assert economic safety and stable replay without asserting whichever order happens to win an unordered race.

## Production boundary

This project intentionally excludes persistence, networking, downstream routing, order amendments, fill/partial-fill accounting, exchange calendars and session eligibility, consolidated-trade filtering, market-data sequence arbitration, FX, margin/Reg T, short-borrow checks, portfolio diversification, corporate actions, authenticated disclosure evidence, and a durable audit trail. Market events are assumed to be eligible trusted events in the relevant security/session; timestamps alone cannot distinguish same-timestamp event ordering.

Initial state is trusted input: external positions, budgets, and reservations must already be reconciled, configuration-compatible, and accurate. The constructors reject basic identity and over-reservation inconsistencies, but do not certify externally supplied reservation pricing.

The project has no `applyFill` API; integrating executions requires one atomic transition that changes filled positions, cash/buying power, and the remaining reservation together. Do not call `release` on a filled order as a substitute for that accounting.

The included GitHub Actions workflow is configured for Java 17 and 21. Run the suite in your own environment before adoption, and use licensed legal/compliance review for regulatory obligations.
