package org.openjdk.jmh.custom;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author HappyFeet
 * @since Dec 13, 2019
 */

@BenchmarkMode({Mode.AverageTime})
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
@Fork(value = 1, jvmArgs = {"-Xms1G", "-Xmx1G"})
@Warmup(iterations = 3)
@Measurement(iterations = 8)
@Threads(value = 1)
public class ArrayListVsHashSetContainsBenchmark {

    private List<Integer> arrayList;
    private HashSet<Integer> hashSet;

    @Param(value = {"-1", "300", "3000", "9999", "111111"})
    private int value;

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(ArrayListVsHashSetContainsBenchmark.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }

    @Setup
    public void setUp() {
        // 通过修改这个基数来改变测试的数量级
        arrayList = IntStream.rangeClosed(1, 10000)
                .boxed()
                .collect(Collectors.toList());

        // 打乱 arrayList 的顺序
        Collections.shuffle(arrayList);

        hashSet = new HashSet<>(arrayList);
    }

    @Benchmark
    public boolean arrayListContains() {
        return arrayList.contains(value);
    }

    @Benchmark
    public boolean hashSetContains() {
        return hashSet.contains(value);
    }

}
