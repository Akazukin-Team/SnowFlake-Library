package org.akazukin.snowflake;

import org.akazukin.snowflake.config.ISnowflakeConfig;
import org.akazukin.snowflake.config.SnowflakeConfigUtils;
import org.akazukin.snowflake.generator.ISnowflake;
import org.akazukin.snowflake.generator.ThreadedSnowflake;
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

public class ThreadedSnowflakeTest {
    @Test
    void test() throws Throwable {
        final byte threads = 6;
        final byte genThrads = 4;
        final int gens = 10_000;


        final ISnowflakeConfig cfg = new ISnowflakeConfig() {
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
                return 2;
            }

            @Override
            public byte getSequenceBits() {
                return (byte) 20;
            }
        };

        final ISnowflake gen = new ThreadedSnowflake(cfg, 0, genThrads);
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
            new ThreadedSnowflake(new ISnowflakeConfig() {
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
            }, 0L, 1);
        });

        Assertions.assertEquals(SnowflakeConfigUtils.EX_ILLEGAL_BITS, ex.getMessage());
    }

    @Test
    void testNegativeMachineIdBits() {
        final IllegalStateException ex = Assertions.assertThrows(IllegalStateException.class, () -> {
            new ThreadedSnowflake(new ISnowflakeConfig() {
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
            }, 0L, 1);
        });

        Assertions.assertEquals(SnowflakeConfigUtils.EX_MACHINE_BITS_NEGATIVE, ex.getMessage());
    }

    @Test
    void testNegativeSequenceBits() {
        final IllegalStateException ex = Assertions.assertThrows(IllegalStateException.class, () -> {
            new ThreadedSnowflake(new ISnowflakeConfig() {
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
            }, 0L, 1);
        });

        Assertions.assertEquals(SnowflakeConfigUtils.EX_SEQUENCE_BITS_NEGATIVE, ex.getMessage());
    }

    @Test
    void testMachineIdNegative() {
        final IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new ThreadedSnowflake(new ISnowflakeConfig() {
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
            }, -1L, 1);
        });

        Assertions.assertEquals(Constants.EX_ILLEGAL_MACHINE_NUM_NEGATIVE, ex.getMessage());
    }

    @Test
    void testMachineIdTooBig() {
        final IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new ThreadedSnowflake(new ISnowflakeConfig() {
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
            }, 2L, 1);
        });

        Assertions.assertEquals(Constants.EX_ILLEGAL_MACHINE_NUM_BIGGER, ex.getMessage());
    }
}
