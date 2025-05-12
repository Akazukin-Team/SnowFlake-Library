package org.akazukin.snowflake.config;

/**
 * Interface for configuring the parameters used in the SnowFlake ID generation algorithm.
 * Provides customizable options such as epoch start timestamp, machine ID bits, sequence bits,
 * and offset for generating globally unique, time-ordered identifiers.
 * <p>
 * Implementations of this interface are expected to define the specific configuration
 * values required for SnowFlake ID generation. These configurations directly influence
 * the structure and limits of the generated IDs.
 */
public interface ISnowFlakeConfig {
    /**
     * Retrieves the custom epoch start timestamp for the SnowFlake ID generation.
     * This timestamp represents the starting reference point for the
     * timestamp component in the ID structure.
     *
     * @return the epoch starts timestamp in milliseconds
     */
    long getTimestampStart();

    /**
     * Retrieves the offset applied to the custom epoch start timestamp for SnowFlake ID generation.
     * The offset is added to the base epoch timestamp to produce a modified starting point.
     *
     * @return the timestamp offset in milliseconds
     */
    long getTimestampOffset();

    /**
     * A unique identifier that varies for each machine.
     * <br>
     * I recommend using 10 bits for the machine ID.
     * If you use 10 bits, the maximum number of machines is 1024.
     * Changing the machine ID for each thread significantly improves performance.
     *
     * @return machine ID bits
     */
    byte getMachineIdBits();

    /**
     * The number of bits used for the incrementing value when regeneration occurs at the same timestamp.
     * <br>
     * I recommend using 12 bits for the sequence number.
     * If you use 12 bits, the maximum number of requests per millisecond is 4096.
     *
     * @return sequence bits
     */
    byte getSequenceBits();
}
