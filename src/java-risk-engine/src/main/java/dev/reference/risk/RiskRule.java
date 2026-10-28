package dev.reference.risk;

import java.util.List;

import static dev.reference.risk.Model.*;
import static dev.reference.risk.RiskDecision.*;

/**
 * Extensions MUST be pure and thread-safe. The engine passes only immutable values.
 * No I/O, randomness, current time, or mutable captured state is permitted.
 */
public interface RiskRule {
    String id();
    List<Violation> evaluate(Context context);

    record Context(Order order, RiskState state, MarketSnapshot market,
                   RiskConfig config, Instrument instrument, Exposure exposure) {}
}
