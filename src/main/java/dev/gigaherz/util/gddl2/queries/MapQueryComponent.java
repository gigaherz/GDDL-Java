package dev.gigaherz.util.gddl2.queries;

import dev.gigaherz.util.gddl2.formatting.Formatter;
import dev.gigaherz.util.gddl2.internal.Utility;
import dev.gigaherz.util.gddl2.structure.GddlElement;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.stream.Stream;

public final class MapQueryComponent extends QueryComponent
{
    private final String name;

    public MapQueryComponent(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    @Override
    public Stream<GddlElement<?>> filter(Stream<GddlElement<?>> input)
    {
        return input
                .filter(GddlElement::isMap)
                .map(GddlElement::asMap)
                .filter(m -> m.containsKey(name))
                .map(m -> m.get(name));
    }

    @Override
    public String toString(Formatter formatter)
    {
        var name = getName();
        if (formatter.getOptions().alwaysUseStringLiterals || !Utility.isValidIdentifier(name))
            name = Utility.escapeString(name);
        return name;
    }

    @Override
    public QueryComponent copy()
    {
        return new MapQueryComponent(getName());
    }

    @Override
    public boolean equals(Object other)
    {
        if (other == this) return true;
        if (other == null || getClass() != other.getClass()) return false;
        return equalsImpl((MapQueryComponent) other);
    }

    public boolean equals(MapQueryComponent other)
    {
        if (other == this) return true;
        if (other == null) return false;
        return equalsImpl(other);
    }

    private boolean equalsImpl(@NotNull MapQueryComponent other)
    {
        return Objects.equals(name, other.name);
    }

    @Override
    public int hashCode()
    {
        return (name != null ? name.hashCode() : 0);
    }
}
