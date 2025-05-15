package org.akazukin.snowflake.config;

import lombok.experimental.UtilityClass;
import org.akazukin.snowflake.Constants;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class SnowFlakeConfigUtils {
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
            throw new IllegalStateException(Constants.EX_ILLEGAL_BITS);
        }
        if (cfg.getMachineIdBits() < 0) {
            throw new IllegalStateException(Constants.EX_MACHINE_BITS_NEGATIVE);
        }
        if (cfg.getSequenceBits() < 0) {
            throw new IllegalStateException(Constants.EX_SEQUENCE_BITS_NEGATIVE);
        }
    }
}
