package dev.gigaherz.util.gddl2.parsing;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Token implements ContextProvider
{
    public final String whitespace;
    public final String comment;
    public final TokenType type;
    public final String text;
    public final ParsingContext context;
    public Token parent;

    public Token(TokenType type, String text, ContextProvider contextProvider, String comment, String whitespace)
    {
        this.whitespace = whitespace;
        this.comment = comment;
        this.type = type;
        this.text = text;
        this.context = contextProvider.getParsingContext();
    }

    public Token withParent(Token parent)
    {
        this.parent = parent;
        return this;
    }

    public Token specialize(TokenType child)
    {
        return new Token(child, text, this, comment, whitespace).withParent(this);
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
    public boolean equals(Object other)
    {
        if (other == this) return true;
        if (other == null || getClass() != other.getClass()) return false;
        return equalsImpl((Token) other);
    }

    public boolean equals(Token other)
    {
        if (other == this) return true;
        if (other == null) return false;
        return equalsImpl(other);
    }

    private boolean equalsImpl(@NotNull Token other)
    {
        return type == other.type && text.equals(other.text) && context.equals(other.context);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(whitespace, comment, type, text, context);
    }

    public boolean is(TokenType tokenType)
    {
        return type == tokenType || (parent != null && parent.is(tokenType));
    }

    @Override
    public ParsingContext getParsingContext()
    {
        return context;
    }
}
