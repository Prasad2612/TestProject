package dev.reference.risk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static dev.reference.risk.Decimals.decimal;
import static dev.reference.risk.Fixtures.*;
import static dev.reference.risk.Model.*;
import static dev.reference.risk.RiskLedger.Status;
import static org.junit.jupiter.api.Assertions.*;

@Timeout(value = 30, unit = TimeUnit.SECONDS)
class ConcurrencyDeterminismTest {
    @ParameterizedTest @ValueSource(ints = {1, 2, 8, 16})
    void sameSnapshotDecisionsMatchSequentialAcrossWorkerCounts(int threads) throws Exception {
        RiskEngine engine = new RiskEngine(config());
        RiskState state = state("1500", "900");
        MarketSnapshot market = market();
        List<Order> orders = new ArrayList<>();
        for (int i = 0; i < 256; i++) {
            OrderType type = OrderType.values()[i % OrderType.values().length];
            orders.add(order("o-" + i, i % 2 == 0 ? Side.BUY : Side.SELL, type,
                Integer.toString(1 + i % 110), type.limitBased() ? "10.25" : null,
                type.conditional() ? "10.50" : null));
        }
        List<RiskDecision> expected = orders.stream().map(o -> engine.validate(o, state, market)).toList();
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        try {
            for (int repetition = 0; repetition < 4; repetition++) {
                List<Callable<RiskDecision>> calls = orders.stream()
                    .<Callable<RiskDecision>>map(o -> () -> engine.validate(o, state, market)).toList();
                List<Future<RiskDecision>> futures = pool.invokeAll(calls);
                for (int i = 0; i < futures.size(); i++) assertEquals(expected.get(i), futures.get(i).get());
            }
        } finally { shutdown(pool); }
        assertEquals(state("1500", "900"), state);
    }
    @ParameterizedTest @ValueSource(ints = {2, 8, 16})
    void concurrentCashReservationsNeverOversubscribe(int threads) throws Exception {
        for (int round = 0; round < 5; round++) {
            var ledger = new RiskLedger(new RiskEngine(config()), state("1000", "0"));
            var results = burst(threads, 48, i -> () -> ledger.submit(buy("o-" + i, "10"), market()));
            assertEquals(10, results.stream().filter(r -> r.status() == Status.RESERVED).count());
            assertEquals(38, results.stream().filter(r -> r.status() == Status.REJECTED).count());
            assertEquals(decimal("0"), ledger.availableBuyingPower());
            assertEquals(10, ledger.snapshot().version());
            assertEquals(10, ledger.snapshot().reservations().size());
            // Identity of winning orders deliberately not asserted: arrival order is nondeterministic.
        }
    }
    @ParameterizedTest @ValueSource(ints = {2, 8, 16})
    void concurrentPositionReservationsNeverOversubscribe(int threads) throws Exception {
        var config = config("100", "10", false, false, false, RiskConfig.EvaluationMode.COLLECT_ALL);
        var ledger = new RiskLedger(new RiskEngine(config), state("100000", "0"));
        var results = burst(threads, 32, i -> () -> ledger.submit(buy("o-" + i, "1"), market()));
        assertEquals(10, results.stream().filter(r -> r.status() == Status.RESERVED).count());
        var reserved = ledger.snapshot().reservations().values().stream().map(r -> r.order().quantity())
            .reduce(decimal("0"), java.math.BigDecimal::add);
        assertEquals(decimal("10"), reserved);
    }
    @Test void concurrentIdenticalRetriesReserveExactlyOnce() throws Exception {
        var ledger = new RiskLedger(new RiskEngine(config()), state("1000", "0"));
        var results = burst(16, 64, i -> () -> ledger.submit(buy("same", "10.00"), market()));
        assertEquals(1, results.stream().filter(r -> r.status() == Status.RESERVED).count());
        assertEquals(63, results.stream().filter(r -> r.status() == Status.ALREADY_RESERVED).count());
        assertEquals(1, ledger.snapshot().version());
        assertEquals(decimal("900"), ledger.availableBuyingPower());
    }
    @Test void concurrentCancellationReleasesExactlyOnce() throws Exception {
        var ledger = new RiskLedger(new RiskEngine(config()), state("1000", "0"));
        ledger.submit(buy("same", "10"), market());
        var results = burst(16, 64, i -> () -> ledger.release("same"));
        assertEquals(1, results.stream().filter(Boolean::booleanValue).count());
        assertEquals(2, ledger.snapshot().version());
        assertEquals(decimal("1000"), ledger.availableBuyingPower());
    }
    @Test void stopActivationMatchesAcrossThreads() throws Exception {
        var service = new StopTriggerService();
        var stop = order("s", Side.BUY, OrderType.STOP_LIMIT, "10", "12", "11");
        var event = new StopTriggerService.TradeEvent("ACME", "USD", decimal("11"), NOW);
        var expected = service.evaluate(stop, event, NOW, AGE);
        var results = burst(16, 512, i -> () -> service.evaluate(stop, event, NOW, AGE));
        assertTrue(results.stream().allMatch(expected::equals));
    }
    @ParameterizedTest @ValueSource(ints = {1, 4, 16})
    void sortedReplayGivesIdenticalWinnersAndFinalStateAcrossThreadsAndPermutations(int threads) throws Exception {
        var engine = new RiskEngine(config());
        var initial = state("100", "0");
        List<Order> orders = new ArrayList<>();
        for (int i = 0; i < 16; i++) orders.add(buy(String.format(java.util.Locale.ROOT, "o-%02d", i), "1"));
        var expected = DeterministicReplay.run(engine, initial, market(), orders);
        var results = burst(threads, 32, i -> () -> {
            var shuffled = new ArrayList<>(orders);
            Collections.shuffle(shuffled, new Random(i));
            return DeterministicReplay.run(engine, initial, market(), shuffled);
        });
        for (var result : results) assertEquals(expected, result);
        assertEquals(List.of("o-00", "o-01", "o-02", "o-03", "o-04", "o-05", "o-06", "o-07", "o-08", "o-09"),
            new ArrayList<>(expected.finalState().reservations().keySet()));
    }
    @Test void replayRejectsDuplicateIdsBeforeAnySubmission() {
        assertThrows(IllegalArgumentException.class, () -> DeterministicReplay.run(new RiskEngine(config()),
            state("100", "0"), market(), List.of(buy("same", "1"), buy("same", "2"))));
    }

    private static <T> List<T> burst(int threads, int count,
                                    java.util.function.IntFunction<Callable<T>> taskFactory) throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);
        try {
            List<Future<T>> futures = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                Callable<T> task = taskFactory.apply(i);
                futures.add(pool.submit(() -> { start.await(); return task.call(); }));
            }
            start.countDown();
            List<T> results = new ArrayList<>();
            for (Future<T> future : futures) results.add(future.get(20, TimeUnit.SECONDS));
            return results;
        } finally { start.countDown(); shutdown(pool); }
    }
    private static void shutdown(ExecutorService pool) throws InterruptedException {
        pool.shutdownNow();
        assertTrue(pool.awaitTermination(5, TimeUnit.SECONDS), "executor must terminate");
    }
}
