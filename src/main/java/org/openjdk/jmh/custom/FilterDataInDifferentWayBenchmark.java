package org.openjdk.jmh.custom;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Fork(value = 1, jvmArgs = {"-Xms1G", "-Xmx1G"})
@Warmup(iterations = 3)
@Measurement(iterations = 5)
public class FilterDataInDifferentWayBenchmark {

    private List<Integer> values;

    public static void main(String[] args) throws RunnerException {

        Options opt = new OptionsBuilder()
                .include(FilterDataInDifferentWayBenchmark.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }

    /*
    There is the output:
        # JMH version: 1.22
        # VM version: JDK 1.8.0_101, Java HotSpot(TM) 64-Bit Server VM, 25.101-b13
        # VM invoker: /Library/Java/JavaVirtualMachines/jdk1.8.0_101.jdk/Contents/Home/jre/bin/java
        # VM options: -Xms1G -Xmx1G
        # Warmup: 3 iterations, 10 s each
        # Measurement: 5 iterations, 10 s each
        # Timeout: 10 min per iteration
        # Threads: 1 thread, will synchronize iterations
        # Benchmark mode: Average time, time/op

        Benchmark                                                        Mode  Cnt  Score   Error  Units
        FilterDataInDifferentWayBenchmark.eachFilter                     avgt    5  6.971 ± 0.957  ms/op
        FilterDataInDifferentWayBenchmark.filterChain                    avgt    5  3.838 ± 0.162  ms/op
        FilterDataInDifferentWayBenchmark.filterChainWithParallelStream  avgt    5  1.970 ± 0.054  ms/op
        FilterDataInDifferentWayBenchmark.forLoop                        avgt    5  1.398 ± 0.013  ms/op
        FilterDataInDifferentWayBenchmark.forLoopWithExpectedCapacity    avgt    5  1.333 ± 0.038  ms/op
     */

    @Setup
    public void setUp() {
        values = IntStream.rangeClosed(0, 100000).boxed().collect(Collectors.toList());
    }

    @Benchmark
    public List<Integer> filterChain() {

        return values.stream()
                .filter(value -> value % 17 != 0)
                .filter(value -> value % 16 != 0)
                .filter(value -> value % 15 != 0)
                .filter(value -> value % 14 != 0)
                .filter(value -> value % 13 != 0)
                .collect(Collectors.toList());
    }

    @Benchmark
    public List<Integer> filterChainWithParallelStream() {

        return values.parallelStream()
                .filter(value -> value % 17 != 0)
                .filter(value -> value % 16 != 0)
                .filter(value -> value % 15 != 0)
                .filter(value -> value % 14 != 0)
                .filter(value -> value % 13 != 0)
                .collect(Collectors.toList());
    }

    @Benchmark
    public List<Integer> eachFilter() {

        List<Integer> filter1 = values.stream()
                .filter(value -> value % 17 != 0)
                .collect(Collectors.toList());

        List<Integer> filter2 = filter1.stream()
                .filter(value -> value % 16 != 0)
                .collect(Collectors.toList());

        List<Integer> filter3 = filter2.stream()
                .filter(value -> value % 15 != 0)
                .collect(Collectors.toList());

        List<Integer> filter4 = filter3.stream()
                .filter(value -> value % 14 != 0)
                .collect(Collectors.toList());

        return filter4.stream()
                .filter(value -> value % 13 != 0)
                .collect(Collectors.toList());
    }

    @Benchmark
    public List<Integer> forLoop() {

        List<Integer> results = new ArrayList<>();

        for (Integer value : values) {
            if (value % 17 != 0
                    && value % 16 != 0
                    && value % 15 != 0
                    && value % 14 != 0
                    && value % 13 != 0) {
                results.add(value);
            }
        }

        return results;
    }

    @Benchmark
    public List<Integer> forLoopWithExpectedCapacity() {

        List<Integer> results = new ArrayList<>(values.size());

        for (Integer value : values) {
            if (value % 17 != 0
                    && value % 16 != 0
                    && value % 15 != 0
                    && value % 14 != 0
                    && value % 13 != 0) {
                results.add(value);
            }
        }

        return results;
    }
}
