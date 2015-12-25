package gigaherz.utils.GDDL.exceptions;

import gigaherz.utils.GDDL.FileContext;
import gigaherz.utils.GDDL.exceptions.ParserException;

public class LexerException extends ParserException
{
    public LexerException(FileContext context, String message)
    {
        super(context, message);
    }
}
