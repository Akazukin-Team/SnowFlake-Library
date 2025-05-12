package org.akazukin.snowflake;

import org.akazukin.snowflake.config.ISnowFlakeConfig;
import org.akazukin.snowflake.generator.AtomicSnowFlake;
import org.akazukin.snowflake.generator.ISnowFlake;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SnowFlakeTest {
    @Test
    void test() throws Throwable {
        final byte threads = 3;
        final int gens = 100_000;


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

        final ISnowFlake ISnowFlake = new AtomicSnowFlake(cfg, 0);
        final Set<Long> ids = new HashSet<>();

        final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(threads);
        final Set<Future<?>> tasks = new HashSet<>();
        for (int i = 0; i < executor.getMaximumPoolSize(); i++) {
            tasks.add(executor.submit(() -> {
                for (int i2 = 0; i2 < gens; i2++) {
                    final long id = ISnowFlake.nextId();
                    synchronized (ids) {
                        if (ids.contains(id)) {
                            throw new IllegalStateException("Duplicate id: " + id);
                        } else {
                            ids.add(id);
                        }
                    }
                }
            }));
        }
        executor.shutdown();
        if (!executor.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS)) {
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

        Assertions.assertEquals(Constants.EX_ILLEGAL_BITS, ex.getMessage());
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

        Assertions.assertEquals(Constants.EX_MACHINE_BITS_NEGATIVE, ex.getMessage());
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

        Assertions.assertEquals(Constants.EX_SEQUENCE_BITS_NEGATIVE, ex.getMessage());
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
