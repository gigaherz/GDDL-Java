package dev.gigaherz.util.gddl2.exceptions;

import dev.gigaherz.util.gddl2.parser.ContextProvider;

public class LexerException extends ParserException
{
    public LexerException(ContextProvider context, String message)
    {
        super(context, message);
    }
}
