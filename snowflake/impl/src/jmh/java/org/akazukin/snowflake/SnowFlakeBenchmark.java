package org.akazukin.snowflake;

import org.akazukin.snowflake.generator.ISnowFlake;
import org.akazukin.snowflake.generator.SnowFlake;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.CompilerControl;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@CompilerControl(CompilerControl.Mode.DONT_INLINE)
public class SnowFlakeBenchmark {
    private static final int SIZE = 1 << 30;

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void test() {
        final ISnowFlake gen = new SnowFlake(new SnowFlakeConfig(), 0b111 << 7 | 0b1);
        for (int i2 = 0; i2 < SIZE; i2++) {
            gen.nextId();
        }
    }
}
