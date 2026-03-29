package org.akazukin.snowflake.generator;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.akazukin.annotation.marker.ThreadSafe;
import org.akazukin.snowflake.config.ISnowflakeConfig;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Pool-based implementation that delegates generation to a pool of
 * internal generator instances.
 *
 * <p>Uses multiple internal generator instances (one per pool entry) to
 * produce identifiers and reduce contention under concurrent use.
 */
@ThreadSafe
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ThreadedSnowflake implements ISnowflake {
    /**
     * Pool of internal generator instances used to produce identifiers.
     */
    SnowFlakeContainer[] pool;

    /**
     * Number of entries in the pool.
     */
    int poolSize;

    /**
     * Per-thread starting index into the pool.
     */
    ThreadLocal<Integer> threadIdx;

    /**
     * Constructs a new {@code ThreadedSnowFlake} with the provided configuration.
     *
     * @param config    configuration for each pooled instance (must not be null)
     * @param machineId base machine identifier for the first pooled instance
     * @param poolSize  number of pooled instances (positive)
     */
    public ThreadedSnowflake(final ISnowflakeConfig config, final long machineId, final int poolSize) {
        this.poolSize = poolSize;
        this.pool = new SnowFlakeContainer[poolSize];
        for (int i = 0; i < poolSize; i++) {
            this.pool[i] = new SnowFlakeContainer(new Snowflake(config, machineId + i));
        }
        this.threadIdx = ThreadLocal.withInitial(() -> ThreadLocalRandom.current().nextInt(poolSize));
    }

    /**
     * Returns the next 64-bit identifier by delegating to a pooled generator.
     *
     * @return next 64-bit identifier as produced by a pooled generator
     */
    @Override
    public long nextId() {
        int i = this.threadIdx.get();
        while (true) {
            final SnowFlakeContainer container = this.pool[i];

            if (container.lock.compareAndSet(false, true)) {
                try {
                    return container.instance.nextId();
                } finally {
                    container.lock.set(false);
                }
            }

            i = (i + 1) % this.poolSize;
        }
    }

    /**
     * Pool entry that holds a pooled {@code SnowFlake} and a lock.
     */
    private static class SnowFlakeContainer {
        final Snowflake instance;
        final AtomicBoolean lock = new AtomicBoolean(false);

        SnowFlakeContainer(final Snowflake instance) {
            this.instance = instance;
        }
    }
}
