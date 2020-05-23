package gigaherz.util.gddl2;

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

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParsingContext that = (ParsingContext) o;
        return line == that.line &&
                column == that.column &&
                filename.equals(that.filename);
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
