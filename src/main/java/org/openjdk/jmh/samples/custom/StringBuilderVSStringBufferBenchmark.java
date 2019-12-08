package org.openjdk.jmh.samples.custom;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Fork(value = 1, jvmArgs = {"-Xms4G", "-Xmx4G"})
@Warmup(iterations = 5)
@Measurement(iterations = 10)
public class StringBuilderVSStringBufferBenchmark {


    //    @Param(value = {"10", "1000", "10000"})
    @Param(value = {"3000"})
    private int size;

    @Benchmark
    public void stringBuffer(Blackhole bh) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < size; i++) {
            sb.append(i);
        }
        bh.consume(sb);
    }

    @Benchmark
    public void stringBuilder(Blackhole bh) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            sb.append(i);
        }
        bh.consume(sb);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(StringBuilderVSStringBufferBenchmark.class.getSimpleName())
//                .forks(1)
                .build();

        new Runner(opt).run();
    }

}
