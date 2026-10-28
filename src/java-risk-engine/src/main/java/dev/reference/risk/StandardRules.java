package dev.reference.risk;

import java.util.ArrayList;
import java.util.List;

import static dev.reference.risk.Model.*;
import static dev.reference.risk.RiskDecision.*;

/** Mandatory risk controls; ordering is configurable, disabling them is not. */
public enum StandardRules implements RiskRule {
    POSITION_LIMIT {
        @Override public List<Violation> evaluate(Context c) {
            List<Violation> violations = new ArrayList<>();
            if (c.order().quantity().compareTo(c.instrument().maxOrderQuantity()) > 0)
                violations.add(v(id(), Code.ORDER_QUANTITY_LIMIT, "order quantity exceeds configured maximum"));
            if (c.exposure().minimumPosition().abs().compareTo(c.instrument().maxAbsolutePosition()) > 0
                    || c.exposure().maximumPosition().abs().compareTo(c.instrument().maxAbsolutePosition()) > 0)
                violations.add(v(id(), Code.POSITION_LIMIT, "independent pending-fill extremes exceed position limit"));
            if (!c.config().allowShortSelling() && c.exposure().minimumPosition().signum() < 0)
                violations.add(v(id(), Code.SHORT_SELLING_DISABLED, "sell reservations exceed owned quantity"));
            return List.copyOf(violations);
        }
    },
    BUYING_POWER {
        @Override public List<Violation> evaluate(Context c) {
            return c.exposure().requiredBuyingPower().compareTo(c.exposure().availableBuyingPower()) > 0
                ? List.of(v(id(), Code.INSUFFICIENT_BUYING_POWER, "required cash exceeds unreserved buying power"))
                : List.of();
        }
    },
    STOP_POLICY {
        @Override public List<Violation> evaluate(Context c) {
            Order order = c.order();
            if (!order.type().conditional()) return List.of();
            List<Violation> violations = new ArrayList<>();
            if (order.handling() == Handling.NOT_HELD)
                violations.add(v(id(), Code.NOT_HELD_UNSUPPORTED,
                    "Rule 5350(b) excludes not-held orders; this reference does not implement their semantics"));
            if (order.type().standardStop()) {
                if (!c.config().acceptStandardStops())
                    violations.add(v(id(), Code.STOPS_DISABLED, "standard stops are not accepted by firm policy"));
            } else {
                if (!c.config().acceptQuoteTriggeredOrders())
                    violations.add(v(id(), Code.ALTERNATIVE_TRIGGER_DISABLED, "quote-triggered alternatives disabled"));
                Disclosure disclosure = order.disclosure();
                if (disclosure == null) {
                    violations.add(v(id(), Code.DISCLOSURE_REQUIRED, "advance description of trigger required"));
                } else {
                    if (disclosure.orderType() != order.type() || disclosure.trigger() != order.trigger())
                        violations.add(v(id(), Code.DISCLOSURE_MISMATCH, "disclosure does not match the order and trigger"));
                    if (!disclosure.deliveredAt().isBefore(order.placedAt()))
                        violations.add(v(id(), Code.DISCLOSURE_TOO_LATE, "disclosure must precede order placement"));
                    if (c.config().onlineTrading() && !disclosure.postedClearlyOnWebsite())
                        violations.add(v(id(), Code.WEBSITE_DISCLOSURE_REQUIRED, "online disclosure must be conspicuous"));
                }
            }
            // Optional firm policy, NOT a requirement imposed by Rule 5350's definition.
            if (c.config().enforceStopPlacementDirection()) {
                Quote quote = c.market().quotes().get(order.symbol());
                boolean invalid = order.side() == Side.BUY
                    ? order.stopPrice().compareTo(quote.ask()) <= 0
                    : order.stopPrice().compareTo(quote.bid()) >= 0;
                if (invalid) violations.add(v(id(), Code.STOP_PLACEMENT_DIRECTION,
                    "stop placement does not meet optional directional policy"));
            }
            return List.copyOf(violations);
        }
    };

    @Override public String id() { return name(); }
    static Violation v(String rule, Code code, String detail) { return new Violation(rule, code, detail); }
}
