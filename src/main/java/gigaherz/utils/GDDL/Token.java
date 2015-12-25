package gigaherz.utils.GDDL;

public class Token
{
    public Tokens Name;
    public String Text;
    public ParseContext Context;

    public Token(Tokens name, ParseContext context, String text)
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
}
