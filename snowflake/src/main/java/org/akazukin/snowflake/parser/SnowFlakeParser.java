package org.akazukin.snowflake.parser;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.akazukin.snowflake.config.ISnowFlakeConfig;
import org.akazukin.snowflake.config.SnowFlakeConfigUtils;

/**
 * This class is responsible for parsing Snowflake IDs into their constituent parts.
 * It uses a configuration-based approach to define the structure of a Snowflake ID.
 * The parsed components typically include the machine ID, sequence number, and timestamp.
 *
 * @see ISnowFlakeConfig
 * @see SnowFlakeConfigUtils#validate(ISnowFlakeConfig)
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SnowFlakeParser {
    /**
     * The number of bits each part to shift
     */
    long machineLeft;
    long timestampLeft;

    /**
     * Constructs a new instance of the SnowFlakeParser with the specified configuration.
     *
     * @param cfg The configuration for the SnowFlake ID generator, specifying machine ID
     *            bits, sequence bits, and the start timestamp.
     * @throws IllegalStateException    If the sum of machine ID bits and sequence bits exceeds 22 bits,
     *                                  or if either machine ID bits or sequence bits are negative.
     * @throws IllegalArgumentException If the provided machine ID is negative or
     *                                  exceeds the maximum allowed value.
     */
    public SnowFlakeParser(final ISnowFlakeConfig cfg) {
        // Validate the configuration
        SnowFlakeConfigUtils.validate(cfg);

        this.machineLeft = cfg.getSequenceBits();
        this.timestampLeft = this.machineLeft + cfg.getMachineIdBits();
    }

    /**
     * Parses the given SnowFlake ID into its constituent parts.
     *
     * @param id The SnowFlake ID to be parsed.
     * @return A Result object containing the machine ID, sequence, and timestamp extracted from the ID.
     */
    public Result parse(final long id) {
        final long sequence = id & ~(-1L << this.machineLeft);
        final long machineId = (id >> this.machineLeft) & ~(-1L << this.timestampLeft);
        final long timestamp = (id >> this.timestampLeft) + this.machineLeft;

        return new Result(machineId, sequence, timestamp);
    }

    /**
     * Represents the result of parsing a Snowflake ID.
     * This class encapsulates the machine ID, sequence, and timestamp components extracted from a Snowflake identifier.
     *
     * @see SnowFlakeParser#parse(long)
     */
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @AllArgsConstructor
    public static class Result {
        long machineId;
        long sequence;
        long timestamp;
    }
}
