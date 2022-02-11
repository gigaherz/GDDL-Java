package dev.gigaherz.util.gddl2.parsing;

@FunctionalInterface
public interface ContextProvider
{
    /**
     * @return An object containing the location being parsed, to be used in error messages and debugging.
     */
    ParsingContext getParsingContext();
}
