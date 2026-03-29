package org.akazukin.snowflake.generator;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.akazukin.annotation.marker.NonThreadSafe;
import org.akazukin.snowflake.Constants;
import org.akazukin.snowflake.config.ISnowflakeConfig;
import org.akazukin.snowflake.config.SnowflakeConfigUtils;
import org.jetbrains.annotations.NotNull;

/**
 * Simple, non-thread-safe generator implementation for producing identifiers.
 *
 * <p>Minimal implementation intended for single-threaded use.
 * Validates configuration on construction and composes identifiers according
 * to the configured bit layout.
 */
@NonThreadSafe
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class Snowflake implements ISnowflake {
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
    long timestamp, sequence;

    /**
     * Constructs a new {@code SnowFlake} and validates the provided configuration.
     * Initializes bit shifts and local machine identifier.
     *
     * @param config    configuration specifying machine and sequence bits
     *                  and the start timestamp (must not be null)
     * @param machineId machine identifier for this instance (non-negative,
     *                  must not exceed the maximum allowed by machine ID bits)
     * @throws IllegalStateException    if configuration bit sizes are invalid
     * @throws IllegalArgumentException if {@code machineId} is out of range
     */
    public Snowflake(@NotNull final ISnowflakeConfig config, final long machineId) {
        // Validate the configuration
        SnowflakeConfigUtils.validate(config);

        this.startTimestamp = config.getTimestampStart();
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

        if (this.timestamp < curTime) {
            this.timestamp = curTime;
        } else if (this.sequence < this.maxSequenceNum) {
            this.sequence++;
        } else {
            this.timestamp++;
            this.sequence = 0;
        }

        return (this.timestamp - this.startTimestamp) << this.timestampLeft
                | this.machineId << this.machineLeft
                | this.sequence;
    }
}
