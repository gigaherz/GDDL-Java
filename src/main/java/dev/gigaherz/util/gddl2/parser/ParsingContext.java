package dev.gigaherz.util.gddl2.parser;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ParsingContext implements ContextProvider
{
    public final String filename;
    public final int line;
    public final int column;

    public ParsingContext(String f, int l, int c)
    {
        filename = f;
        line = l;
        column = c;
    }

    public String getFilename()
    {
        return filename;
    }

    public int getLine()
    {
        return line;
    }

    public int getColumn()
    {
        return column;
    }

    @Override
    public boolean equals(Object other)
    {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        return equalsImpl((ParsingContext) other);
    }

    public boolean equals(ParsingContext other)
    {
        if (this == other) return true;
        if (other == null) return false;
        return equalsImpl(other);
    }

    public boolean equalsImpl(@NotNull ParsingContext other)
    {
        return line == other.line &&
                column == other.column &&
                filename.equals(other.filename);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(filename, line, column);
    }

    @Override
    public String toString()
    {
        return String.format("%s(%d,%d)", filename, line, column);
    }

    @Override
    public ParsingContext getParsingContext()
    {
        return this;
    }
}
