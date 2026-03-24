package org.akazukin.snowflake.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class SnowFlakeConfig implements ISnowFlakeConfig {
    long timestampStart;
    long timestampOffset;
    byte machineIdBits;
    byte sequenceBits;

    public SnowFlakeConfig(final long timestampStart, final long timestampOffset, final byte machineIdBits, final byte sequenceBits) {
        this.timestampStart = timestampStart;
        this.timestampOffset = timestampOffset;
        this.machineIdBits = machineIdBits;
        this.sequenceBits = sequenceBits;
    }
}
