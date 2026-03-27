package org.akazukin.snowflake.generator;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.akazukin.annotation.marker.ThreadSafe;
import org.akazukin.snowflake.Constants;
import org.akazukin.snowflake.config.ISnowFlakeConfig;
import org.akazukin.snowflake.config.SnowFlakeConfigUtils;
import org.jetbrains.annotations.NotNull;

/**
 * Thread-safe generator implementation that synchronizes access to state.
 * <p>
 * Provides the same identifier composition as the simple generator but
 * serializes updates to internal state to support concurrent use. Validates
 * configuration on construction.
 */
@ThreadSafe
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class AtomicSnowFlake implements ISnowFlake {
    /**
     * Maximum sequence value.
     */
    final long maxSequenceNum;

    /**
     * Bit shift amounts used to pack machine ID and timestamp.
     */
    final long machineLeft;
    final long timestampLeft;

    /**
     * Configured start timestamp.
     */
    final long startTimestamp;
    final long machineId;

    /**
     * Current timestamp and sequence counter.
     */
    long timestamp;
    long sequence;

    /**
     * Constructs a new thread-safe {@code AtomicSnowFlake}
     * and validates the supplied configuration.
     * Initializes bit shifts and the instance machine identifier.
     * The configured timestamp offset is applied to the start timestamp.
     *
     * @param config    configuration specifying machine and sequence bits,
     *                  timestamp start and offset (must not be null)
     * @param machineId machine identifier for this instance (non-negative,
     *                  must not exceed the maximum allowed by machine ID bits)
     * @throws IllegalStateException    if configuration bit sizes are invalid
     * @throws IllegalArgumentException if {@code machineId} is out of range
     */
    public AtomicSnowFlake(@NotNull final ISnowFlakeConfig config, final long machineId) {
        // Validate the configuration
        SnowFlakeConfigUtils.validate(config);

        this.startTimestamp = config.getTimestampStart() + config.getTimestampOffset();
        //  The number of bits each part occupies
        final long machineBits = config.getMachineIdBits();
        final long sequenceBits = config.getSequenceBits();

        final long maxMachineNum = ~(-1L << machineBits);
        this.maxSequenceNum = ~(-1L << sequenceBits);

        this.machineLeft = sequenceBits;
        this.timestampLeft = this.machineLeft + machineBits;


        if (machineId < 0) {
            throw new IllegalArgumentException(Constants.EX_ILLEGAL_MACHINE_NUM_NEGATIVE);
        }
        if (machineId > maxMachineNum) {
            throw new IllegalArgumentException(Constants.EX_ILLEGAL_MACHINE_NUM_BIGGER);
        }

        this.machineId = machineId;
    }

    /**
     * Returns the next identifier.
     *
     * @return next 64-bit identifier
     */
    @Override
    public long nextId() {
        final long curTime = System.currentTimeMillis();

        final long ts, seq;
        synchronized (this) {
            if (this.timestamp < curTime) {
                ts = this.timestamp = curTime;
                seq = this.sequence = 0;
            } else if (this.sequence == this.maxSequenceNum) {
                ts = ++this.timestamp;
                seq = this.sequence = 0;
            } else {
                ts = this.timestamp;
                seq = ++this.sequence;
            }
        }

        return (ts - this.startTimestamp) << this.timestampLeft
                | this.machineId << this.machineLeft
                | seq;
    }
}
