package org.openjdk.jmh.custom;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

/**
 * @author HappyFeet
 * @since Dec 13, 2019
 */

@BenchmarkMode({Mode.Throughput})
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(value = 1, jvmArgs = {"-Xms1G", "-Xmx1G"})
@Warmup(iterations = 3)
@State(Scope.Benchmark)
@Measurement(iterations = 5)
public class StringBuilderVsStringBufferBenchmark {

    @Param({"10", "100", "1000", "10000"})
    private int iterations;

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(StringBuilderVsStringBufferBenchmark.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }

    @Benchmark
    public String stringBuilder(MyState state) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < iterations; i++) {
            sb.append(state.preparedStrList.get(i));
        }
        return sb.toString();
    }

    @Benchmark
    public String stringBuffer(MyState state) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < iterations; i++) {
            sb.append(state.preparedStrList.get(i));
        }
        return sb.toString();
    }

    @State(Scope.Benchmark)
    public static class MyState {
        List<String> preparedStrList = IntStream
                .rangeClosed(1, 10000)
                .boxed()
                .map(Object::toString)
                .collect(toList());
    }

}
