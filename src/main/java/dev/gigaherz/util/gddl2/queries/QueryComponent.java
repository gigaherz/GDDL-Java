package dev.gigaherz.util.gddl2.queries;

import dev.gigaherz.util.gddl2.formatting.Formatter;
import dev.gigaherz.util.gddl2.structure.GddlElement;

import java.util.stream.Stream;

public sealed abstract class QueryComponent
        permits ListQueryComponent, MapQueryComponent, ParentQueryComponent, SelfQueryComponent
{
    public abstract Stream<GddlElement<?>> filter(Stream<GddlElement<?>> input);

    public abstract String toString(Formatter formatter);

    public abstract QueryComponent copy();

    public abstract boolean equals(Object other);

    public abstract int hashCode();
}
