package dev.gigaherz.util.gddl2.queries;

public record Index(int value, boolean fromEnd)
{
    public static Index fromStart(int value)
    {
        return new Index(value, false);
    }

    public static Index fromEnd(int value)
    {
        return new Index(value, true);
    }

    public int offset(int size)
    {
        return fromEnd ? size - value : value;
    }
}
