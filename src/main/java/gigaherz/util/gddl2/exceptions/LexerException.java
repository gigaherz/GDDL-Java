package gigaherz.util.gddl2.exceptions;

import gigaherz.util.gddl2.ContextProvider;

public class LexerException extends ParserException
{
    public LexerException(ContextProvider context, String message)
    {
        super(context, message);
    }
}
