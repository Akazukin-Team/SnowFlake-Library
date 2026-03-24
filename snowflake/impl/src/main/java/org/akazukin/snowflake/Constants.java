package org.akazukin.snowflake;

/**
 * This class defines a collection of constant strings used for exception messages
 * in the SnowFlake ID generation process. These constants serve as standardized
 * error messages shared across multiple classes and scenarios.
 * <p>
 * These messages pertain to configuration and validation issues encountered
 * during the initialization or usage of SnowFlake instances.
 */
public class Constants {
    /**
     * Exception messages
     */
    public static final String EX_ILLEGAL_BITS = "The sum of machineId bits and sequence greater than 22 bits";
    public static final String EX_MACHINE_BITS_NEGATIVE = "The machineId bits must be positive";
    public static final String EX_SEQUENCE_BITS_NEGATIVE = "The sequence bits must be positive";
    public static final String EX_ILLEGAL_MACHINE_NUM_BIGGER = "machineId can't be greater than max machine id";
    public static final String EX_ILLEGAL_MACHINE_NUM_NEGATIVE = "machineId must not be negative";
}
