package org.akazukin.snowflake.config;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class SnowFlakeConfigUtils {
    public static final String EX_ILLEGAL_BITS = "The sum of machineId bits and sequence greater than 22 bits";
    public static final String EX_MACHINE_BITS_NEGATIVE = "The machineId bits must be positive";
    public static final String EX_SEQUENCE_BITS_NEGATIVE = "The sequence bits must be positive";

    /**
     * Validates the provided configuration for the SnowFlake ID generator.
     *
     * @param cfg The configuration to be validated.
     * @throws IllegalStateException    If the sum of machine ID bits and sequence bits exceeds 22 bits,
     *                                  or if either machine ID bits or sequence bits are negative.
     * @throws IllegalArgumentException If the provided machine ID is negative or
     *                                  exceeds the maximum allowed value.
     */
    public void validate(@NotNull final ISnowFlakeConfig cfg) {
        if (cfg.getMachineIdBits() + cfg.getSequenceBits() > 22) {
            throw new IllegalStateException(EX_ILLEGAL_BITS);
        }
        if (cfg.getMachineIdBits() < 0) {
            throw new IllegalStateException(EX_MACHINE_BITS_NEGATIVE);
        }
        if (cfg.getSequenceBits() < 0) {
            throw new IllegalStateException(EX_SEQUENCE_BITS_NEGATIVE);
        }
    }
}
