package dev.reference.risk;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

import static dev.reference.risk.Decimals.*;
import static dev.reference.risk.Model.*;

/**
 * Stateless activation of an already-accepted conditional order.
 * Activation is NOT execution and does not guarantee an execution price.
 */
public final class StopTriggerService {
    public sealed interface MarketEvent permits TradeEvent, QuoteEvent {
        String symbol();
        String currency();
        Instant occurredAt();
    }
    public record TradeEvent(String symbol, String currency, BigDecimal price,
                             Instant occurredAt) implements MarketEvent {
        public TradeEvent {
            symbol = text(symbol, "symbol");
            currency = text(currency, "currency");
            price = positive(price, "trade price");
            Objects.requireNonNull(occurredAt);
        }
    }
    public record QuoteEvent(Quote quote) implements MarketEvent {
        public QuoteEvent { Objects.requireNonNull(quote); }
        @Override public String symbol() { return quote.symbol(); }
        @Override public String currency() { return quote.currency(); }
        @Override public Instant occurredAt() { return quote.observedAt(); }
    }
    public enum Status { TRIGGERED, NOT_TRIGGERED, NOT_APPLICABLE, INVALID_EVENT, UNSUPPORTED }
    public record ExecutionOrder(String sourceOrderId, String symbol, String currency,
                                 Side side, BigDecimal quantity, OrderType type, BigDecimal limitPrice) {
        public ExecutionOrder {
            sourceOrderId = text(sourceOrderId, "sourceOrderId");
            symbol = text(symbol, "symbol");
            currency = text(currency, "currency");
            Objects.requireNonNull(side);
            quantity = positive(quantity, "quantity");
            Objects.requireNonNull(type);
            if (type != OrderType.MARKET && type != OrderType.LIMIT)
                throw new IllegalArgumentException("execution type");
            if (type == OrderType.LIMIT) limitPrice = positive(limitPrice, "limitPrice");
            else if (limitPrice != null) throw new IllegalArgumentException("market limitPrice");
        }
    }
    public record Activation(Status status, ExecutionOrder executionOrder, String reason) {
        public Activation {
            Objects.requireNonNull(status);
            reason = text(reason, "reason");
            if ((status == Status.TRIGGERED) != (executionOrder != null))
                throw new IllegalArgumentException("activation payload mismatch");
        }
    }

    public Activation evaluate(Order order, MarketEvent event, Instant asOf, Duration maxEventAge) {
        Objects.requireNonNull(order);
        Objects.requireNonNull(event);
        Objects.requireNonNull(asOf);
        Objects.requireNonNull(maxEventAge);
        if (maxEventAge.isNegative() || maxEventAge.isZero()) throw new IllegalArgumentException("maxEventAge");
        if (!order.type().conditional()) return result(Status.NOT_APPLICABLE, "not a conditional order");
        if (order.handling() == Handling.NOT_HELD) return result(Status.UNSUPPORTED, "not-held semantics excluded");
        Trigger expected = order.type().standardStop() ? Trigger.TRANSACTION : Trigger.QUOTATION;
        if (order.trigger() != expected || order.stopPrice() == null || order.stopPrice().signum() <= 0
                || order.quantity().signum() <= 0 || order.type().limitBased() != (order.limitPrice() != null)
                || (order.limitPrice() != null && order.limitPrice().signum() <= 0))
            return result(Status.UNSUPPORTED, "invalid conditional order; pre-trade acceptance required");
        if (!order.symbol().equals(event.symbol()) || !order.currency().equals(event.currency()))
            return result(Status.INVALID_EVENT, "event instrument or currency mismatch");
        Duration age = Duration.between(event.occurredAt(), asOf);
        if (age.isNegative() || age.compareTo(maxEventAge) > 0 || event.occurredAt().isBefore(order.placedAt()))
            return result(Status.INVALID_EVENT, "future, stale or pre-placement event");
        BigDecimal price;
        if (order.trigger() == Trigger.TRANSACTION && event instanceof TradeEvent trade) {
            price = trade.price();
        } else if (order.trigger() == Trigger.QUOTATION && event instanceof QuoteEvent quote) {
            // A reference firm policy: buy uses ask, sell uses bid. Disclose this convention.
            price = order.side() == Side.BUY ? quote.quote().ask() : quote.quote().bid();
        } else {
            return result(Status.NOT_TRIGGERED, "event kind does not match declared trigger");
        }
        boolean crossed = order.side() == Side.BUY
            ? price.compareTo(order.stopPrice()) >= 0 : price.compareTo(order.stopPrice()) <= 0;
        if (!crossed) return result(Status.NOT_TRIGGERED, "trigger threshold not reached");
        OrderType executionType = order.type().limitBased() ? OrderType.LIMIT : OrderType.MARKET;
        return new Activation(Status.TRIGGERED,
            new ExecutionOrder(order.id(), order.symbol(), order.currency(), order.side(),
                order.quantity(), executionType, order.limitPrice()), "threshold reached");
    }

    private static Activation result(Status status, String reason) { return new Activation(status, null, reason); }
}
