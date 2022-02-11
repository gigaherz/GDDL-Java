package dev.gigaherz.util.gddl2.exceptions;

import dev.gigaherz.util.gddl2.parsing.ContextProvider;

public class LexerException extends ParserException
{
    public LexerException(ContextProvider context, String message)
    {
        super(context, message);
    }
}
