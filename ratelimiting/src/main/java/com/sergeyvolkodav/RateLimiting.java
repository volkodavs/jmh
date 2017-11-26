package com.sergeyvolkodav;

import java.time.Duration;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.RateLimiter;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;


@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@BenchmarkMode({Mode.Throughput, Mode.AverageTime})
@Fork(1)
@Threads(4)
public class RateLimiting {

    @Param({"1000", "1000000"})
    public Integer length;
    private RateLimiter rateLimiter;
    private Semaphore semaphore;
    private Bucket bucket4j;


    @Setup(Level.Trial)
    public void setUp() {

        rateLimiter = RateLimiter.create(1_000_000);
        semaphore = new Semaphore(1_000_000);
        bucket4j = Bucket4j.builder()
                .addLimit(Bandwidth.simple(Long.MAX_VALUE / 2, Duration.ofNanos(Long.MAX_VALUE / 2))
                ).build();
    }

    @Benchmark
    @Measurement(iterations = 2, time = 5)
    @Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
    public void tryAcquireGuava(Blackhole bh) {
        boolean token = rateLimiter.tryAcquire();
        bh.consume(token);
    }


    @Benchmark
    @Measurement(iterations = 2, time = 5)
    @Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
    public void initGuavaRateLimit(Blackhole bh) {
        RateLimiter rateLimiter = RateLimiter.create(100_000);
        bh.consume(rateLimiter);
    }

    // Semaphore
    @Benchmark
    @Measurement(iterations = 2, time = 5)
    @Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
    public void tryAcquireSemaphore(Blackhole bh) {
        boolean token = semaphore.tryAcquire();
        bh.consume(token);
    }

    @Benchmark
    @Measurement(iterations = 2, time = 5)
    @Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
    public void initSemaphore(Blackhole bh) {
        Semaphore semaphore = new Semaphore(100_000);
        bh.consume(semaphore);
    }

    //Bucket 4j
    @Benchmark
    @Measurement(iterations = 2, time = 5)
    @Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
    public void tryAcquireBucket4j(Blackhole bh) {
        boolean token = bucket4j.tryConsume(1);
        bh.consume(token);
    }

    @Benchmark
    @Measurement(iterations = 2, time = 5)
    @Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
    public void initBucket4j(Blackhole bh) {
        Bucket bucket = Bucket4j.builder()
                .addLimit(Bandwidth.simple(100_000, Duration.ofSeconds(1))
                ).build();
        bh.consume(bucket);
    }

}
