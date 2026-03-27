package org.akazukin.snowflake.generator;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.akazukin.annotation.marker.ThreadSafe;
import org.akazukin.snowflake.Constants;
import org.akazukin.snowflake.config.ISnowFlakeConfig;
import org.akazukin.snowflake.config.SnowFlakeConfigUtils;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a SnowFlake ID generator, which creates distributed, globally unique,
 * and time-ordered 64-bit identifiers by partitioning components such as timestamp,
 * machine ID, and sequence number.
 * This implementation adheres to the {@link ISnowFlake} interface and enforces configuration prerequisites.
 * <p>
 * The SnowFlake algorithm ensures high performance, low latency, and collision-free
 * generation of identifiers in distributed systems.
 * <p>
 * The implementation is thread-safe and does not require external synchronization.
 */
@ThreadSafe
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class AtomicSnowFlake implements ISnowFlake {
    /**
     * The number of max sequences
     */
    final long maxSequenceNum;

    /**
     * The number of bits each part to shift
     */
    final long machineLeft;
    final long timestampLeft;

    /**
     * Timestamp start time
     */
    final long startTimestamp;
    final long machineId;

    /**
     * Current timestamp
     */
    long timestamp;
    long sequence;

    /**
     * Constructs a new instance of the SnowFlake ID generator, with the specified configuration
     * and machine ID. The SnowFlake algorithm generates unique, time-ordered IDs by partitioning
     * a 64-bit number into components that include a timestamp, machine ID, and sequence number.
     *
     * @param config    The configuration for the SnowFlake ID generator, specifying machine ID
     *                  bits, sequence bits, and the start timestamp.
     * @param machineId The unique identifier for the machine in a distributed system.
     *                  Must be non-negative and not exceed the maximum value determined by the configured
     *                  machine ID bits.
     * @throws IllegalStateException    If the sum of machine ID bits and sequence bits exceeds 22 bits,
     *                                  or if either machine ID bits or sequence bits are negative.
     * @throws IllegalArgumentException If the provided machine ID is negative or
     *                                  exceeds the maximum allowed value.
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
