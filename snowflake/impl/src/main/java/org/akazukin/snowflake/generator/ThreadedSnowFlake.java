package org.akazukin.snowflake.generator;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.akazukin.snowflake.config.ISnowFlakeConfig;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ThreadedSnowFlake implements ISnowFlake {
    SnowFlakeContainer[] pool;
    int poolSize;

    ThreadLocal<Integer> threadIdx;

    public ThreadedSnowFlake(final ISnowFlakeConfig config, final long machineId, final int poolSize) {
        this.poolSize = poolSize;
        this.pool = new SnowFlakeContainer[poolSize];
        for (int i = 0; i < poolSize; i++) {
            this.pool[i] = new SnowFlakeContainer(new SnowFlake(config, machineId + i));
        }
        this.threadIdx = ThreadLocal.withInitial(() -> ThreadLocalRandom.current().nextInt(poolSize));
    }

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

    private static class SnowFlakeContainer {
        final SnowFlake instance;
        final AtomicBoolean lock = new AtomicBoolean(false);

        SnowFlakeContainer(final SnowFlake instance) {
            this.instance = instance;
        }
    }
}
