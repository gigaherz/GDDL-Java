package gigaherz.util.gddl;

public class Token implements ContextProvider
{
    public final Tokens Name;
    public final String Text;
    public final ParsingContext Context;

    public Token(Tokens name, ParsingContext context, String text)
    {
        Name = name;
        Text = text;
        Context = context;
    }

    @Override
    public String toString()
    {
        if (Text == null)
            return String.format("(%s @ %d:%d)", Name, Context.Line, Context.Column);

        if (Text.length() > 22)
            return String.format("(%s @ %d:%d: %s...)", Name, Context.Line, Context.Column, Text.substring(0, 20));

        return String.format("(%s @ %d:%d: %s)", Name, Context.Line, Context.Column, Text);
    }

    @Override
    public ParsingContext getParsingContext()
    {
        return Context;
    }
}
