package dev.gigaherz.util.gddl2.serialization;

/**
 * Defines how to handle formatting of floating-point numbers
 */
public enum DoubleFormattingStyle
{
    /**
     * Always format numbers in decimal notation
     */
    DECIMAL,
    /**
     * Always format numbers in scientific notation
     */
    SCIENTIFIC,
    /**
     * Decide automatically
     */
    AUTO
}
