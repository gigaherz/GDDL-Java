package dev.gigaherz.util.gddl2.queries;

import dev.gigaherz.util.gddl2.serialization.Formatter;
import dev.gigaherz.util.gddl2.structure.GddlElement;

import java.util.Objects;
import java.util.stream.Stream;

public final class ParentQueryComponent extends QueryComponent
{
    public static final ParentQueryComponent INSTANCE = new ParentQueryComponent();

    private ParentQueryComponent()
    {
    }

    @Override
    public Stream<GddlElement<?>> filter(Stream<GddlElement<?>> input)
    {
        return input
                .<GddlElement<?>>map(GddlElement::getParent)
                .filter(Objects::nonNull);
    }

    @Override
    public String toString(Formatter formatter)
    {
        return "..";
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
