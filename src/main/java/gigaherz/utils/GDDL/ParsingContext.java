package gigaherz.utils.GDDL;

public class ParsingContext
{
    public final String Filename;
    public final int Line;
    public final int Column;

    public ParsingContext(String f, int l, int c)
    {
        Filename = f;
        Line = l;
        Column = c;
    }

    @Override
    public String toString()
    {
        return String.format("%s(%d,%d)", Filename, Line, Column);
    }
}
