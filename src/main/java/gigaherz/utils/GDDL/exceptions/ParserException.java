package gigaherz.utils.GDDL.exceptions;

import gigaherz.utils.GDDL.ContextProvider;

public class ParserException extends Exception
{
    public ParserException(ContextProvider context, String message)
    {
        super(String.format("%s: %s", context.getParsingContext(), message));
    }
}
