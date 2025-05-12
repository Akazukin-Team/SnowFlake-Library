package org.akazukin.snowflake.generator;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.akazukin.annotation.marker.NonThreadSafe;
import org.akazukin.snowflake.Constants;
import org.akazukin.snowflake.config.ISnowFlakeConfig;
import org.jetbrains.annotations.NotNull;

/**
 * The SnowFlake ID is a distributed unique ID generation algorithm that produces 64-bit integers with the following structure:
 * <pre>
 * +---------------------+---------------------+---------------------+
 * |  Timestamp (41 bits)  | Machine ID (VAR bits) |  Sequence (VAR bits)  |
 * +---------------------+---------------------+---------------------+
 * </pre>
 *
 * <h2>Components:</h2>
 * <ul>
 *   <li><b>Timestamp (41 bits):</b> Milliseconds since a custom epoch. Provides ~69 years of unique timestamps.</li>
 *   <li><b>Machine ID (VAR bits):</b> Identifies the ID generator instance.</li>
 *   <li><b>Sequence (VAR bits):</b> Auto-incrementing sequence number for IDs generated in the same millisecond.</li>
 * </ul>
 *
 * <h2>Key Features:</h2>
 * <ul>
 *   <li>Guaranteed uniqueness across distributed systems when properly configured</li>
 *   <li>Time-sortable: IDs are ordered by generation time due to a timestamp component</li>
 *   <li>High performance: Can generate multiple unique IDs within same millisecond</li>
 *   <li>No central coordination required</li>
 * </ul>
 *
 * <h2>Usage Example:</h2>
 * <pre>
 * ISnowFlakeConfig config = new SnowFlakeConfig();
 * SnowFlake snowflake = new SnowFlake(config, 1L);
 * long id = snowflake.nextId();
 * </pre>
 *
 * <h2>Considerations:</h2>
 * <ul>
 *   <li>Clock synchronization between nodes is important for maintaining proper ordering</li>
 *   <li>Machine IDs must be unique across all nodes to prevent ID collisions</li>
 *   <li>The custom epoch should be chosen carefully based on your application's timeline needs</li>
 *   <li>The maximum timestamp value is limited by 41 bits (~69 years from custom epoch)</li>
 * </ul>
 *
 * @author Currypan1229
 * @see org.akazukin.snowflake.config.ISnowFlakeConfig for configuration options
 * @since 1.0.0
 */
@NonThreadSafe
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class SnowFlake implements ISnowFlake {
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
    long timestamp, sequence;

    public SnowFlake(@NotNull final ISnowFlakeConfig config, final long machineId) {
        if (config.getMachineIdBits() + config.getSequenceBits() > 22) {
            throw new IllegalStateException(Constants.EX_ILLEGAL_BITS);
        }
        if (config.getMachineIdBits() < 0) {
            throw new IllegalStateException(Constants.EX_MACHINE_BITS_NEGATIVE);
        }
        if (config.getSequenceBits() < 0) {
            throw new IllegalStateException(Constants.EX_SEQUENCE_BITS_NEGATIVE);
        }

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
