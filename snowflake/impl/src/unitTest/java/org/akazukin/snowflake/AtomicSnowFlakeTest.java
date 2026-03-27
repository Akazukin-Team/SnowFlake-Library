package org.akazukin.snowflake;

import org.akazukin.snowflake.config.ISnowFlakeConfig;
import org.akazukin.snowflake.config.SnowFlakeConfigUtils;
import org.akazukin.snowflake.generator.AtomicSnowFlake;
import org.akazukin.snowflake.generator.ISnowFlake;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class AtomicSnowFlakeTest {
    @Test
    void test() throws Throwable {
        final byte threads = 6;
        final int gens = 10_000;


        final ISnowFlakeConfig cfg = new ISnowFlakeConfig() {
            @Override
            public long getTimestampStart() {
                return 0;
            }

            @Override
            public long getTimestampOffset() {
                return 0;
            }

            @Override
            public byte getMachineIdBits() {
                return 0;
            }

            @Override
            public byte getSequenceBits() {
                return (byte) 22;
            }
        };

        final ISnowFlake gen = new AtomicSnowFlake(cfg, 0);
        final Set<Long> ids = ConcurrentHashMap.newKeySet();

        final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(threads);
        final Set<Future<?>> tasks = new HashSet<>();
        for (int i = 0; i < executor.getMaximumPoolSize(); i++) {
            tasks.add(executor.submit(() -> {
                for (int i2 = 0; i2 < gens; i2++) {
                    ids.add(gen.nextId());
                }
            }));
        }
        executor.shutdown();
        if (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
            executor.shutdownNow();
        }

        for (final Future<?> t : tasks) {
            try {
                t.get();
            } catch (final InterruptedException e) {
                throw e;
            } catch (final ExecutionException e) {
                throw e.getCause();
            }
        }

        Assertions.assertEquals(gens * threads, ids.size());
    }

    @Test
    void testTooManyBits() {
        final IllegalStateException ex = Assertions.assertThrows(IllegalStateException.class, () -> {
            new AtomicSnowFlake(new ISnowFlakeConfig() {
                @Override
                public long getTimestampStart() {
                    return 0;
                }

                @Override
                public long getTimestampOffset() {
                    return 0;
                }

                @Override
                public byte getMachineIdBits() {
                    return 10;
                }

                @Override
                public byte getSequenceBits() {
                    return 13;
                }
            }, 0L);
        });

        Assertions.assertEquals(SnowFlakeConfigUtils.EX_ILLEGAL_BITS, ex.getMessage());
    }

    @Test
    void testNegativeMachineIdBits() {
        final IllegalStateException ex = Assertions.assertThrows(IllegalStateException.class, () -> {
            new AtomicSnowFlake(new ISnowFlakeConfig() {
                @Override
                public long getTimestampStart() {
                    return 0;
                }

                @Override
                public long getTimestampOffset() {
                    return 0;
                }

                @Override
                public byte getMachineIdBits() {
                    return -1;
                }

                @Override
                public byte getSequenceBits() {
                    return 0;
                }
            }, 0L);
        });

        Assertions.assertEquals(SnowFlakeConfigUtils.EX_MACHINE_BITS_NEGATIVE, ex.getMessage());
    }

    @Test
    void testNegativeSequenceBits() {
        final IllegalStateException ex = Assertions.assertThrows(IllegalStateException.class, () -> {
            new AtomicSnowFlake(new ISnowFlakeConfig() {
                @Override
                public long getTimestampStart() {
                    return 0;
                }

                @Override
                public long getTimestampOffset() {
                    return 0;
                }

                @Override
                public byte getMachineIdBits() {
                    return 0;
                }

                @Override
                public byte getSequenceBits() {
                    return -1;
                }
            }, 0L);
        });

        Assertions.assertEquals(SnowFlakeConfigUtils.EX_SEQUENCE_BITS_NEGATIVE, ex.getMessage());
    }

    @Test
    void testMachineIdNegative() {
        final IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new AtomicSnowFlake(new ISnowFlakeConfig() {
                @Override
                public long getTimestampStart() {
                    return 0;
                }

                @Override
                public long getTimestampOffset() {
                    return 0;
                }

                @Override
                public byte getMachineIdBits() {
                    return 0;
                }

                @Override
                public byte getSequenceBits() {
                    return 0;
                }
            }, -1L);
        });

        Assertions.assertEquals(Constants.EX_ILLEGAL_MACHINE_NUM_NEGATIVE, ex.getMessage());
    }

    @Test
    void testMachineIdTooBig() {
        final IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new AtomicSnowFlake(new ISnowFlakeConfig() {
                @Override
                public long getTimestampStart() {
                    return 0;
                }

                @Override
                public long getTimestampOffset() {
                    return 0;
                }

                @Override
                public byte getMachineIdBits() {
                    return 0;
                }

                @Override
                public byte getSequenceBits() {
                    return 0;
                }
            }, 2L);
        });

        Assertions.assertEquals(Constants.EX_ILLEGAL_MACHINE_NUM_BIGGER, ex.getMessage());
    }
}
