package dev.gigaherz.util.gddl2.util;

public record Range(Index start, Index end)
{
    public Range(Index start, int i)
    {
        this(start, Index.fromStart(i));
    }

    public int offset(int size)
    {
        return start.offset(size);
    }

    public int length(int size)
    {
        return end.offset(size) - start.offset(size);
    }
}
