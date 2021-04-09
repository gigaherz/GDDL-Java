package dev.gigaherz.util.gddl2.exceptions;

import dev.gigaherz.util.gddl2.parser.ContextProvider;

public class ReaderException extends LexerException
{
    public ReaderException(ContextProvider context, String message)
    {
        super(context, message);
    }
}
