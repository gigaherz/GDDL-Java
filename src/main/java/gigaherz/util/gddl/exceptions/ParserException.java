package gigaherz.util.gddl.exceptions;

import gigaherz.util.gddl.ContextProvider;

public class ParserException extends Exception
{
    public ParserException(ContextProvider context, String message)
    {
        super(String.format("%s: %s", context.getParsingContext(), message));
    }
}
