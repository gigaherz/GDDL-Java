package gigaherz.utils.GDDL;

import java.io.IOException;

public class ReaderException extends LexerException
{
    public ReaderException(FileContext context, String message)
    {
        super(context, message);
    }
}
