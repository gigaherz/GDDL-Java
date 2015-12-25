package gigaherz.utils.GDDL;

public class ParseContext
{
    public String Filename;
    public int Line;
    public int Column;

    public ParseContext(String f, int l, int c)
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
