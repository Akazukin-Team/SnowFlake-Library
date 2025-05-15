/**
 * This package contains interfaces and implements for generating distributed,
 * globally unique, and time-ordered identifiers using the SnowFlake algorithm.
 *
 * <p>
 * The SnowFlake algorithm ensures high performance, low latency, and collision-free
 * generation of 64-bit identifiers. The generated IDs consist of components such as:
 * </p>
 * <ul>
 *     <li>A timestamp component to make IDs time-ordered.</li>
 *     <li>A machine ID component to ensure uniqueness across distributed systems.</li>
 *     <li>A sequence component to handle multiple IDs generated within the same millisecond.</li>
 * </ul>
 *
 * <p>
 * The SnowFlake implementations use configurable components, such as the number
 * of bits allocated for the machine ID and sequence number, to provide flexibility for
 * various distributed system setups.
 * </p>
 */
package org.akazukin.snowflake.generator;
