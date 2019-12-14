package org.openjdk.jmh.custom;

import org.agrona.concurrent.UnsafeBuffer;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.TimeUnit;

/**
 * 这是摘自大佬写的测试 ByteBuffer 的性能
 */
public class ByteBufferBenchmark {

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(".*" + ByteBufferBenchmark.class.getSimpleName() + ".*")
                .warmupIterations(10)
                .measurementIterations(10)
                .threads(1)
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    @Benchmark
    @BenchmarkMode({Mode.AverageTime})
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public int measureHeapGetInt(HeapByteBufferState state) {
        return state.buffer.getInt(0);
    }

    @Benchmark
    @BenchmarkMode({Mode.AverageTime})
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public int measureDirectGetInt(DirectByteBufferState state) {
        return state.buffer.getInt(0);
    }

    @Benchmark
    @BenchmarkMode({Mode.AverageTime})
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public int measureUnsafeDirectBufferGetInt(UnsafeBufferState state) {
        return state.buffer.getInt(0);
    }

    @Benchmark
    @BenchmarkMode({Mode.AverageTime})
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public int measureMappedBufferGetInt(MappedByteBufferState state) {
        return state.buffer.getInt(0);
    }

    @Benchmark
    @BenchmarkMode({Mode.AverageTime})
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public int measureMappedArrayGetInt(ArrayState state) {
        return state.buffer[0];
    }

    @Benchmark
    @BenchmarkMode({Mode.AverageTime})
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public int measureUnsafeHeapBufferGetInt(HeapUnsafeByteBufferState state) {
        return state.buffer.getInt(0);
    }

    @State(Scope.Thread)
    public static class ArrayState {

        public int[] buffer;

        @Setup
        public void setup() {
            buffer = new int[]{1};
        }
    }

    @State(Scope.Thread)
    public static class HeapByteBufferState {

        public ByteBuffer buffer;

        @Setup
        public void setup() {
            buffer = ByteBuffer.allocate(4);
            buffer.putInt(1);
            buffer.rewind();
        }
    }

    @State(Scope.Thread)
    public static class MappedByteBufferState {

        public ByteBuffer buffer;
        private File file;

        {
            try {
                file = File.createTempFile("mappedbytebuffer", "test");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Setup
        public void setup() {
            ByteBuffer writeBuffer = ByteBuffer.allocate(4);
            writeBuffer.putInt(1);
            writeBuffer.rewind();


            try (RandomAccessFile fileAccessFile = new RandomAccessFile(file, "rw")) {
                fileAccessFile.write(writeBuffer.array());
                writeBuffer.clear();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try (RandomAccessFile readFile = new RandomAccessFile(file, "rw")) {
                buffer = readFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, 4);
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

        @TearDown
        public void teardown() {
            file.deleteOnExit();
        }
    }

    @State(Scope.Thread)
    public static class DirectByteBufferState {

        public ByteBuffer buffer;

        @Setup
        public void setup() {
            buffer = ByteBuffer.allocateDirect(4);
            buffer.putInt(1);
            buffer.rewind();
        }
    }

    @State(Scope.Thread)
    public static class UnsafeBufferState {

        public UnsafeBuffer buffer;

        @Setup
        public void setup() {
            ByteBuffer rawBuffer = ByteBuffer.allocateDirect(4);
            rawBuffer.putInt(1);
            rawBuffer.rewind();

            buffer = new UnsafeBuffer(rawBuffer);
        }
    }

    @State(Scope.Thread)
    public static class HeapUnsafeByteBufferState {

        public UnsafeBuffer buffer;

        @Setup
        public void setup() {

            ByteBuffer rawBuffer = ByteBuffer.allocate(4);
            rawBuffer.putInt(1);
            rawBuffer.rewind();

            buffer = new UnsafeBuffer(rawBuffer);
        }
    }


}
