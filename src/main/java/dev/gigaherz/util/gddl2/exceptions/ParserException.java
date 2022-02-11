package dev.gigaherz.util.gddl2.exceptions;

import dev.gigaherz.util.gddl2.parsing.ContextProvider;

public class ParserException extends Exception
{
    public ParserException(ContextProvider context, String message)
    {
        super(String.format("%s: %s", context.getParsingContext(), message));
    }

    public ParserException(ContextProvider context, String message, Throwable throwable)
    {
        super(String.format("%s: %s", context.getParsingContext(), message), throwable);
    }
}
