package gigaherz.utils.GDDL.exceptions;

import gigaherz.utils.GDDL.ContextProvider;

public class ReaderException extends LexerException
{
    public ReaderException(ContextProvider context, String message)
    {
        super(context, message);
    }
}
