package gigaherz.util.gddl2;

import java.util.Objects;

public class Token implements ContextProvider
{
    public final String comment;
    public final TokenType name;
    public final String text;
    public final ParsingContext context;

    public Token(TokenType name, String text, ContextProvider contextProvider, String comment)
    {
        this.comment = comment;
        this.name = name;
        this.text = text;
        context = contextProvider.getParsingContext();
    }

    @Override
    public String toString()
    {
        if (text == null)
            return String.format("(%s @ %d:%d)", name, context.line, context.column);

        if (text.length() > 22)
            return String.format("(%s @ %d:%d: %s...)", name, context.line, context.column, text.substring(0, 20));

        return String.format("(%s @ %d:%d: %s)", name, context.line, context.column, text);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Token token = (Token) o;
        return comment.equals(token.comment) &&
                name == token.name &&
                text.equals(token.text) &&
                context.equals(token.context);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(comment, name, text, context);
    }

    @Override
    public ParsingContext getParsingContext()
    {
        return context;
    }
}
