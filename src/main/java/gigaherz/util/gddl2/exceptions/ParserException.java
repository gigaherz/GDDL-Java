package gigaherz.util.gddl2.exceptions;

import gigaherz.util.gddl2.ContextProvider;

import java.text.ParseException;

public class ParserException extends Exception
{
    public ParserException(ContextProvider context, String message)
    {
        super(String.format("%s: %s", context.getParsingContext(), message));
    }
}
