package dev.gigaherz.util.gddl2.parser;

public enum WhitespaceMode
{
    /**
     * Ignores all whitespace and comments
     */
    DROP_ALL,

    /**
     * Preserves comments, but not
     */
    PRESERVE_COMMENTS,

    /**
     * Preserves comments and whitespace
     */
    PRESERVE_ALL_WHITESPACE
}
