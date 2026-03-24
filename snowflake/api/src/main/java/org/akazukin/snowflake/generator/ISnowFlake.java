package org.akazukin.snowflake.generator;

/**
 * ISnowFlake provides an interface for generating unique 64-bit identifiers
 * using a SnowFlake algorithm. The generated IDs are time-ordered and consist
 * of components like timestamp, machine ID, and sequence number.
 * <p>
 * Core Features:
 * - Ensures uniqueness across distributed systems when configured correctly.
 * - Produces time-ordered identifiers for chronological sorting.
 * - Supports high performance, allowing multiple IDs to be generated within the same millisecond.
 * <p>
 * Implementations of this interface must adhere to SnowFlake's design principles,
 * ensuring proper bit allocation and collision-free identifier generation.
 */
public interface ISnowFlake {
    /**
     * Generates the next unique identifier using the SnowFlake algorithm.
     * The identifier is a 64-bit number comprising timestamp, machine ID, and sequence components.
     *
     * @return a globally unique 64-bit ID that is time-ordered and collision-free
     * within a distributed system, assuming proper configuration.
     */
    long nextId();
}
