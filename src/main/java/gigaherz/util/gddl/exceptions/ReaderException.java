package gigaherz.util.gddl.exceptions;

import gigaherz.util.gddl.ContextProvider;

public class ReaderException extends LexerException
{
    public ReaderException(ContextProvider context, String message)
    {
        super(context, message);
    }
}
