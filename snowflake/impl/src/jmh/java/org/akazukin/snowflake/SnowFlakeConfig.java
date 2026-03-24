package org.akazukin.snowflake;

import org.akazukin.snowflake.config.ISnowFlakeConfig;

public final class SnowFlakeConfig implements ISnowFlakeConfig {
    @Override
    public long getTimestampStart() {
        return 1735689600000L; // 2025-01-01T00:00:00.000Z
    }

    @Override
    public long getTimestampOffset() {
        return 0;
    }

    @Override
    public byte getMachineIdBits() {
        return 10; // max: 1023
    }

    @Override
    public byte getSequenceBits() {
        return 12; // max: 4095
    }
}
