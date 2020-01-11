package gigaherz.util.gddl2.exceptions;

import gigaherz.util.gddl2.ContextProvider;

public class ReaderException extends LexerException
{
    public ReaderException(ContextProvider context, String message)
    {
        super(context, message);
    }
}
