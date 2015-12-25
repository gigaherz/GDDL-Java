package gigaherz.utils.GDDL.exceptions;

import gigaherz.utils.GDDL.FileContext;

public class ReaderException extends LexerException
{
    public ReaderException(FileContext context, String message)
    {
        super(context, message);
    }
}
