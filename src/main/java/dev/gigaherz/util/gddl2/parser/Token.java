package dev.gigaherz.util.gddl2.parser;

import dev.gigaherz.util.gddl2.util.Utility;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Token implements ContextProvider
{
    public final String whitespace;
    public final String comment;
    public final TokenType type;
    public final String text;
    public final ParsingContext context;

    public Token(TokenType type, String text, ContextProvider contextProvider, String comment, String whitespace)
    {
        this.whitespace = whitespace;
        this.comment = comment;
        this.type = type;
        this.text = text;
        context = contextProvider.getParsingContext();
    }

    @Override
    public String toString()
    {
        if (text == null)
            return String.format("(%s @ %d:%d)", type, context.line, context.column);

        if (text.length() > 22)
            return String.format("(%s @ %d:%d: %s...)", type, context.line, context.column, text.substring(0, 20));

        return String.format("(%s @ %d:%d: %s)", type, context.line, context.column, text);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return equals((Token) o);
    }

    public boolean equals(@NotNull Token other)
    {
        return type == other.type &&
                text.equals(other.text) &&
                context.equals(other.context);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(whitespace, comment, type, text, context);
    }

    @Override
    public ParsingContext getParsingContext()
    {
        return context;
    }
}
