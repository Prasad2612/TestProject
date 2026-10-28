package dev.reference.risk;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import static dev.reference.risk.Decimals.*;

/** Immutable domain model for a single-currency equity account. */
public final class Model {
    private Model() {}

    public enum Side { BUY, SELL }
    public enum OrderType {
        MARKET, LIMIT, STOP, STOP_LIMIT, QUOTE_TRIGGERED_MARKET, QUOTE_TRIGGERED_LIMIT;
        public boolean conditional() { return this != MARKET && this != LIMIT; }
        public boolean standardStop() { return this == STOP || this == STOP_LIMIT; }
        public boolean limitBased() {
            return this == LIMIT || this == STOP_LIMIT || this == QUOTE_TRIGGERED_LIMIT;
        }
    }
    public enum Trigger { NONE, TRANSACTION, QUOTATION }
    public enum Handling { HELD, NOT_HELD }
    public static String text(String value, String field) {
        Objects.requireNonNull(value, field);
        if (value.isBlank() || !value.equals(value.trim()))
            throw new IllegalArgumentException(field + " must be nonblank without surrounding spaces");
        return value;
    }

    public record Disclosure(OrderType orderType, Trigger trigger, Instant deliveredAt,
                             String description, boolean postedClearlyOnWebsite) {
        public Disclosure {
            Objects.requireNonNull(orderType);
            Objects.requireNonNull(trigger);
            Objects.requireNonNull(deliveredAt);
            description = text(description, "description");
        }
    }

    public record Order(String id, String accountId, String symbol, String currency, Side side,
                        OrderType type, BigDecimal quantity, BigDecimal limitPrice,
                        BigDecimal stopPrice, Trigger trigger, Handling handling,
                        Instant placedAt, Disclosure disclosure) {
        public Order {
            id = text(id, "id");
            accountId = text(accountId, "accountId");
            symbol = text(symbol, "symbol");
            currency = text(currency, "currency");
            Objects.requireNonNull(side);
            Objects.requireNonNull(type);
            quantity = canonical(quantity);
            limitPrice = optional(limitPrice);
            stopPrice = optional(stopPrice);
            Objects.requireNonNull(trigger);
            Objects.requireNonNull(handling);
            Objects.requireNonNull(placedAt);
        }
    }

    public record Instrument(String symbol, String currency, BigDecimal priceTick,
                             BigDecimal quantityStep, BigDecimal maxOrderQuantity,
                             BigDecimal maxAbsolutePosition) {
        public Instrument {
            symbol = text(symbol, "symbol");
            currency = text(currency, "currency");
            priceTick = positive(priceTick, "priceTick");
            quantityStep = positive(quantityStep, "quantityStep");
            maxOrderQuantity = positive(maxOrderQuantity, "maxOrderQuantity");
            maxAbsolutePosition = positive(maxAbsolutePosition, "maxAbsolutePosition");
        }
    }

    public record Quote(String symbol, String currency, BigDecimal bid,
                        BigDecimal ask, Instant observedAt) {
        public Quote {
            symbol = text(symbol, "symbol");
            currency = text(currency, "currency");
            bid = positive(bid, "bid");
            ask = positive(ask, "ask");
            if (bid.compareTo(ask) > 0) throw new IllegalArgumentException("crossed quote");
            Objects.requireNonNull(observedAt);
        }
    }

    public record MarketSnapshot(Instant asOf, Map<String, Quote> quotes) {
        public MarketSnapshot {
            Objects.requireNonNull(asOf);
            quotes = immutableSorted(quotes);
            quotes.forEach((key, value) -> {
                if (!key.equals(value.symbol())) throw new IllegalArgumentException("quote key mismatch");
            });
        }
    }

    public record Reservation(Order order, BigDecimal buyingPower) {
        public Reservation {
            Objects.requireNonNull(order);
            if (order.quantity().signum() <= 0) throw new IllegalArgumentException("reservation quantity");
            buyingPower = nonNegative(buyingPower, "reservation buyingPower");
        }
    }

    /**
     * buyingPower is the total remaining budget BEFORE open-order reservations.
     * Filled positions are separate from reservations, which are not netted.
     */
    public record RiskState(String accountId, String currency, long version,
                            BigDecimal buyingPower, Map<String, BigDecimal> positions,
                            Map<String, Reservation> reservations) {
        public RiskState {
            accountId = text(accountId, "accountId");
            currency = text(currency, "currency");
            if (version < 0) throw new IllegalArgumentException("negative state version");
            buyingPower = nonNegative(buyingPower, "buyingPower");
            TreeMap<String, BigDecimal> copy = new TreeMap<>();
            Objects.requireNonNull(positions).forEach((symbol, qty) ->
                copy.put(text(symbol, "position symbol"), canonical(qty)));
            positions = Collections.unmodifiableMap(copy);
            reservations = immutableSorted(reservations);
            BigDecimal used = ZERO;
            for (var entry : reservations.entrySet()) {
                Reservation reservation = entry.getValue();
                Order order = reservation.order();
                if (!entry.getKey().equals(order.id()) || !accountId.equals(order.accountId())
                        || !currency.equals(order.currency()))
                    throw new IllegalArgumentException("reservation identity mismatch");
                used = used.add(reservation.buyingPower());
            }
            if (used.compareTo(buyingPower) > 0) throw new IllegalArgumentException("over-reserved buying power");
        }

        public BigDecimal availableBuyingPower() {
            BigDecimal used = reservations.values().stream().map(Reservation::buyingPower)
                .reduce(ZERO, BigDecimal::add);
            return canonical(buyingPower.subtract(used));
        }
    }

    static <T> Map<String, T> immutableSorted(Map<String, T> source) {
        TreeMap<String, T> copy = new TreeMap<>();
        Objects.requireNonNull(source).forEach((key, value) ->
            copy.put(text(key, "map key"), Objects.requireNonNull(value)));
        return Collections.unmodifiableMap(copy);
    }
}
