package dev.reference.risk;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import static dev.reference.risk.Decimals.*;
import static dev.reference.risk.Model.*;
import static dev.reference.risk.RiskDecision.*;
import static dev.reference.risk.StandardRules.v;

/**
 * Pure snapshot validation: identical immutable inputs produce identical decisions,
 * including rule order and canonical decimals, independent of execution thread.
 */
public final class RiskEngine {
    private final RiskConfig config;
    private final List<RiskRule> rules;

    public RiskEngine(RiskConfig config) {
        this(config, List.of(StandardRules.STOP_POLICY, StandardRules.POSITION_LIMIT, StandardRules.BUYING_POWER));
    }

    public RiskEngine(RiskConfig config, List<RiskRule> rules) {
        this.config = Objects.requireNonNull(config);
        this.rules = List.copyOf(rules);
        HashSet<String> ids = new HashSet<>();
        for (RiskRule rule : this.rules) {
            if (!ids.add(Model.text(rule.id(), "rule id"))) throw new IllegalArgumentException("duplicate rule id");
        }
        for (StandardRules required : StandardRules.values()) {
            if (!this.rules.contains(required))
                throw new IllegalArgumentException("missing mandatory control: " + required);
        }
    }

    public RiskConfig config() { return config; }
    public List<RiskRule> rules() { return rules; }

    public RiskDecision validate(Order order, RiskState state, MarketSnapshot market) {
        Objects.requireNonNull(order);
        Objects.requireNonNull(state);
        Objects.requireNonNull(market);
        List<Violation> violations = structural(order, state, market);
        if (!violations.isEmpty()) return new RiskDecision(order.id(), state.version(), violations, null);
        Exposure exposure = exposure(order, state, market);
        RiskRule.Context context = new RiskRule.Context(order, state, market, config,
            config.instruments().get(order.symbol()), exposure);
        for (RiskRule rule : rules) {
            List<Violation> found = List.copyOf(rule.evaluate(context));
            violations.addAll(found);
            if (!found.isEmpty() && config.mode() == RiskConfig.EvaluationMode.FAIL_FAST) break;
        }
        return new RiskDecision(order.id(), state.version(), violations, exposure);
    }

    private List<Violation> structural(Order o, RiskState state, MarketSnapshot market) {
        List<Violation> errors = new ArrayList<>();
        if (!o.accountId().equals(state.accountId()))
            errors.add(v("INPUT", Code.ACCOUNT_MISMATCH, "order does not belong to this account"));
        Instrument instrument = config.instruments().get(o.symbol());
        if (instrument == null)
            errors.add(v("INPUT", Code.UNKNOWN_INSTRUMENT, "symbol is not configured"));
        if (!o.currency().equals(state.currency())
                || (instrument != null && !o.currency().equals(instrument.currency())))
            errors.add(v("INPUT", Code.CURRENCY_MISMATCH, "order, instrument and account currencies must match"));
        if (o.placedAt().isAfter(market.asOf()))
            errors.add(v("INPUT", Code.FUTURE_ORDER, "placement time is after evaluation time"));
        if (state.reservations().containsKey(o.id()))
            errors.add(v("INPUT", Code.DUPLICATE_ORDER, "order id already reserved; use ledger for idempotent retries"));
        if (o.quantity().signum() <= 0
                || (instrument != null && !aligned(o.quantity(), instrument.quantityStep())))
            errors.add(v("INPUT", Code.INVALID_QUANTITY, "quantity must be positive and aligned to quantity step"));
        boolean hasLimit = o.limitPrice() != null;
        boolean hasStop = o.stopPrice() != null;
        if (hasLimit != o.type().limitBased() || hasStop != o.type().conditional())
            errors.add(v("INPUT", Code.INVALID_ORDER_SHAPE, "limit and stop fields do not match order type"));
        for (BigDecimal price : new BigDecimal[]{o.limitPrice(), o.stopPrice()}) {
            if (price != null && (price.signum() <= 0 || (instrument != null && !aligned(price, instrument.priceTick()))))
                errors.add(v("INPUT", Code.INVALID_PRICE, "price must be positive and aligned to instrument tick"));
        }
        Trigger expected = !o.type().conditional() ? Trigger.NONE
            : o.type().standardStop() ? Trigger.TRANSACTION : Trigger.QUOTATION;
        if (o.trigger() != expected)
            errors.add(v("INPUT", Code.INVALID_TRIGGER, "order label requires trigger " + expected));
        // Invalid shape must never reach valuation or custom rules.
        if (!errors.isEmpty()) return errors;
        boolean needQuote = !o.type().limitBased()
            || (o.type().conditional() && config.enforceStopPlacementDirection());
        if (needQuote) {
            Quote quote = market.quotes().get(o.symbol());
            if (quote == null) {
                errors.add(v("MARKET_DATA", Code.MISSING_QUOTE, "valuation requires a quote"));
            } else {
                if (!o.currency().equals(quote.currency()))
                    errors.add(v("MARKET_DATA", Code.QUOTE_CURRENCY_MISMATCH, "quote currency mismatch"));
                Duration age = Duration.between(quote.observedAt(), market.asOf());
                if (age.isNegative())
                    errors.add(v("MARKET_DATA", Code.FUTURE_QUOTE, "quote is from the future"));
                else if (age.compareTo(config.maxQuoteAge()) > 0)
                    errors.add(v("MARKET_DATA", Code.STALE_QUOTE, "quote age exceeds policy"));
            }
        }
        return errors;
    }

    private Exposure exposure(Order o, RiskState state, MarketSnapshot market) {
        BigDecimal minimum = state.positions().getOrDefault(o.symbol(), ZERO);
        BigDecimal maximum = minimum;
        for (Reservation reservation : state.reservations().values()) {
            Order pending = reservation.order();
            if (!pending.symbol().equals(o.symbol())) continue;
            if (pending.side() == Side.BUY) maximum = maximum.add(pending.quantity());
            else minimum = minimum.subtract(pending.quantity());
        }
        if (o.side() == Side.BUY) maximum = maximum.add(o.quantity());
        else minimum = minimum.subtract(o.quantity());

        BigDecimal price;
        if (o.type().limitBased()) {
            price = o.limitPrice();
        } else {
            Quote quote = market.quotes().get(o.symbol());
            price = o.side() == Side.BUY ? quote.ask() : quote.bid();
            if (o.type().conditional() && o.side() == Side.BUY) price = price.max(o.stopPrice());
            price = price.multiply(factor(config.slippageBasisPoints()));
        }
        BigDecimal exactNotional = price.multiply(o.quantity());
        BigDecimal required = o.side() == Side.BUY
            ? reserve(exactNotional.multiply(factor(config.feeBasisPoints())), config.currencyScale()) : ZERO;
        return new Exposure(minimum, maximum, price, exactNotional, required, state.availableBuyingPower());
    }
}
