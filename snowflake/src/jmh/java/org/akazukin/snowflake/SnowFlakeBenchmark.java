package org.akazukin.snowflake;

import org.akazukin.snowflake.generator.AtomicSnowFlake;
import org.akazukin.snowflake.generator.ISnowFlake;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.CompilerControl;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@CompilerControl(CompilerControl.Mode.DONT_INLINE)
public class SnowFlakeBenchmark {
    public static final int POOL_SIZE = 75;
    private static final int SIZE = 1 << 21;
    private List<Callable<Void>> tasks;

    private ThreadPoolExecutor executor;

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void testMultiThreaded() throws InterruptedException {
        this.executor.invokeAll(this.tasks);
    }

    @Setup
    public void initTasks() {
        this.executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(POOL_SIZE);
        this.tasks = new ArrayList<>();

        final ISnowFlake ISnowFlake = new AtomicSnowFlake(new SnowFlakeConfig(), 0b111 << 7 | 0b1);
        final Callable<Void> task = () -> {
            for (int i2 = 0; i2 < SIZE; i2++) {
                ISnowFlake.nextId();
            }
            return null;
        };
        for (int i = 0; i < this.executor.getMaximumPoolSize(); i++) {
            this.tasks.add(task);
        }
    }

    @TearDown
    public void tearDown() {
        this.executor.shutdownNow();
        this.executor = null;
        this.tasks = null;
    }
}
