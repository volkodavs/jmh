package com.sergeyvolkodav.boxing;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

@State(Scope.Benchmark)
public class ListBoxing {


    @Param({"2000", "2000000"})
    public Integer length;

    private List<String> stringArrayList = new ArrayList<>();
    private List<Object> objects = new ArrayList<>();

    @Setup(Level.Trial)
    public void setUp() {

        for (int i = 0; i < length; i++) {
            stringArrayList.add(String.valueOf(i));
            objects.add(String.valueOf(i));
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.All)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    @Measurement(iterations = 2, time = 5)
    @Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
    @Fork(1)
    public void testObject(Blackhole bh) {

        for (int i = 0; i < objects.size(); i++) {
            bh.consume(objects.get(i));
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.All)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    @Measurement(iterations = 2, time = 5)
    @Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
    @Fork(1)
    public void testStringArray(Blackhole bh) {
        for (int i = 0; i < stringArrayList.size(); i++) {
            bh.consume(stringArrayList.get(i));
        }
    }


}
