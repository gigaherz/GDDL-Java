package gigaherz.utils.GDDL.exceptions;

import gigaherz.utils.GDDL.FileContext;
import gigaherz.utils.GDDL.Parser;

public class ParserException extends Exception
{
    public ParserException(FileContext context, String message)
    {
        super(String.format("%s: %s", context.getFileContext(), message));
    }
}
