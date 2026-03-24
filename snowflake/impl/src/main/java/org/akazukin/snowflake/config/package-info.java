/**
 * Provides configuration-related classes and interfaces for the SnowFlake ID generation algorithm.
 * <p>
 * This package includes utilities to define customizable parameters for the generation
 * of globally unique, time-ordered identifiers using the SnowFlake algorithm. Users
 * are able to configure aspects such as:
 * <ul>
 *   <li>The custom epoch (starting timestamp) for ID generation</li>
 *   <li>Machine-specific identifiers</li>
 *   <li>A bit of allocation for machine IDs and sequence numbers</li>
 *   <li>Other offsets or limits influencing the ID structure</li>
 * </ul>
 * <p>
 * Key interfaces:
 * <ul>
 *   <li>{@link org.akazukin.snowflake.config.ISnowFlakeConfig} - Defines the configurable parameters for SnowFlake ID generation.</li>
 * </ul>
 * <p>
 * This package is designed to provide flexibility and scalability for systems requiring
 * distributed, high-performance ID generation while ensuring uniqueness and time-order.
 */
package org.akazukin.snowflake.config;