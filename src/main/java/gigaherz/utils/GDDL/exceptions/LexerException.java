package gigaherz.utils.GDDL.exceptions;

import gigaherz.utils.GDDL.ContextProvider;

public class LexerException extends ParserException
{
    public LexerException(ContextProvider context, String message)
    {
        super(context, message);
    }
}
