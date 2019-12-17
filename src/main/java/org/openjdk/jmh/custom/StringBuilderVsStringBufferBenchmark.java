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

@BenchmarkMode({Mode.AverageTime})
@OutputTimeUnit(TimeUnit.NANOSECONDS)
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
    public String plusInLoop(MyState state) {
        String str = "";
        for (int i = 0; i < iterations; i++) {
            str = str + state.preparedStrList.get(i);
        }
        return str;
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

    @Benchmark
    public String stringBuilderWithCapacity(MyState state) {
        StringBuilder sb = new StringBuilder(iterations * 5);
        for (int i = 0; i < iterations; i++) {
            sb.append(state.preparedStrList.get(i));
        }
        return sb.toString();
    }

    @Benchmark
    public String stringBuilderWithoutCapacity(MyState state) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < iterations; i++) {
            sb.append(state.preparedStrList.get(i));
        }
        return sb.toString();
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.EXCLUDE)
    public String byPlusAppendWithoutCompile(MyState state) {
        StringBuilder sb = new StringBuilder();
        sb.append(state.mike + state.daniel + state.john + state.martin);
        return sb.toString();
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.EXCLUDE)
    public String byChainAppendWithoutCompile(MyState state) {
        StringBuilder sb = new StringBuilder();
        sb.append(state.mike).append(state.daniel).append(state.john).append(state.martin);
        return sb.toString();
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.EXCLUDE)
    public String byEachRowAppendWithoutCompile(MyState state) {
        StringBuilder sb = new StringBuilder();
        sb.append(state.mike);
        sb.append(state.daniel);
        sb.append(state.john);
        sb.append(state.martin);
        return sb.toString();
    }

    @Benchmark
    public String byPlusAppendWithCompile(MyState state) {
        StringBuilder sb = new StringBuilder();
        sb.append(state.mike + state.daniel + state.john + state.martin);
        return sb.toString();
    }

    @Benchmark
    public String byChainAppendWithCompile(MyState state) {
        StringBuilder sb = new StringBuilder();
        sb.append(state.mike).append(state.daniel).append(state.john).append(state.martin);
        return sb.toString();
    }

    @Benchmark
    public String byEachRowAppendWithCompile(MyState state) {
        StringBuilder sb = new StringBuilder();
        sb.append(state.mike);
        sb.append(state.daniel);
        sb.append(state.john);
        sb.append(state.martin);
        return sb.toString();
    }

    @State(Scope.Benchmark)
    public static class MyState {
        String mike = "mike";
        String daniel = "daniel";
        String john = "john";
        String martin = "martin";

        List<String> preparedStrList = IntStream
                .rangeClosed(10001, 20000)
                .boxed()
                .map(Object::toString)
                .collect(toList());
    }

}
