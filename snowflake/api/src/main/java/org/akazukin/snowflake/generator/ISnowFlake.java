package org.akazukin.snowflake.generator;

/**
 * API for producing 64-bit identifiers.
 * <p>
 * Represents a SnowFlake ID generator, which creates distributed, globally unique,
 * and time-ordered 64-bit identifiers by partitioning components such as timestamp,
 * machine ID, and sequence number.
 * This implementation adheres to the {@link ISnowFlake} interface and enforces configuration prerequisites.
 * <p>
 * The SnowFlake algorithm ensures high performance, low latency, and collision-free
 * generation of identifiers in distributed systems.
 */
public interface ISnowFlake {
    /**
     * Returns the next 64-bit identifier.
     *
     * @return next 64-bit identifier
     */
    long nextId();
}
