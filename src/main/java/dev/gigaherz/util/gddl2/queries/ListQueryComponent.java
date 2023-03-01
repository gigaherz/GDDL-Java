package dev.gigaherz.util.gddl2.queries;

import dev.gigaherz.util.gddl2.formatting.Formatter;
import dev.gigaherz.util.gddl2.structure.GddlElement;
import dev.gigaherz.util.gddl2.util.Range;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

public final class ListQueryComponent extends QueryComponent
{
    private final Range range;

    public ListQueryComponent(Range range)
    {
        this.range = range;
    }

    @Override
    public Stream<GddlElement<?>> filter(Stream<GddlElement<?>> input)
    {
        return input
                .filter(GddlElement::isList).map(GddlElement::asList)
                .flatMap(m ->
                {
                    var start = range.start().value();
                    var end = range.end().value();
                    if (range.start().fromEnd()) start = m.size() - start;
                    if (range.end().fromEnd()) end = m.size() - end;
                    return m.stream().skip(start).limit(end - start);
                });
    }

    @Override
    public String toString(Formatter formatter)
    {
        var sb = new StringBuilder();
        sb.append("[");
        var start = range.start();
        var end = range.end();
        if (start.value() != 0 || start.fromEnd())
        {
            if (start.fromEnd()) sb.append("^");
            sb.append(start.value());
            if (end.fromEnd() == start.fromEnd())
            {
                if ((!start.fromEnd() && (start.value() + 1 == end.value())) || (start.fromEnd() && (start.value() == end.value() + 1)))
                {
                    sb.append("]");
                    return sb.toString();
                }
            }
        }
        sb.append("..");
        if (end.value() != 0 || end.fromEnd())
        {
            if (end.fromEnd()) sb.append("^");
            sb.append(end.value());
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public QueryComponent copy()
    {
        return new ListQueryComponent(range);
    }

    @Override
    public boolean equals(Object other)
    {
        if (other == this) return true;
        if (other == null || getClass() != other.getClass()) return false;
        return equalsImpl((ListQueryComponent) other);
    }

    public boolean equals(ListQueryComponent other)
    {
        if (other == this) return true;
        if (other == null) return false;
        return equalsImpl(other);
    }

    private boolean equalsImpl(@NotNull ListQueryComponent other)
    {
        return range.equals(other.range);
    }

    @Override
    public int hashCode()
    {
        return range.hashCode();
    }
}
