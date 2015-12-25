package gigaherz.utils.GDDL;

public class ParserException extends Exception
{
    protected ParserException()
    {
        super();
    }
    protected ParserException(String message)
    {
        super(message);
    }

    public ParserException(FileContext context, String message)
    {
        super(String.format("%s: %s", context.getFileContext(), message));
    }

    public ParserException(Parser parser, String message)
    {
        this(parser.getLexer(), message);
    }
}
