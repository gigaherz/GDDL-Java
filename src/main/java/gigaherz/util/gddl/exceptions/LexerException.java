package gigaherz.util.gddl.exceptions;

import gigaherz.util.gddl.ContextProvider;

public class LexerException extends ParserException
{
    public LexerException(ContextProvider context, String message)
    {
        super(context, message);
    }
}
