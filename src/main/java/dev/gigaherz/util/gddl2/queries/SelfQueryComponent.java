package dev.gigaherz.util.gddl2.queries;

import dev.gigaherz.util.gddl2.serialization.Formatter;
import dev.gigaherz.util.gddl2.structure.GddlElement;

import java.util.stream.Stream;

public final class SelfQueryComponent extends QueryComponent
{
    public static final SelfQueryComponent INSTANCE = new SelfQueryComponent();

    private SelfQueryComponent()
    {
    }

    @Override
    public Stream<GddlElement<?>> filter(Stream<GddlElement<?>> input)
    {
        return input;
    }

    @Override
    public String toString(Formatter formatter)
    {
        return ".";
    }

    @Override
    public QueryComponent copy()
    {
        return this;
    }

    @Override
    public boolean equals(Object other)
    {
        return other == this;
    }

    @Override
    public int hashCode()
    {
        return 0;
    }
}
