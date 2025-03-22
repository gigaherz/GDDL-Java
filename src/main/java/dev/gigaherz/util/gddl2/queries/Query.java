package dev.gigaherz.util.gddl2.queries;

import dev.gigaherz.util.gddl2.exceptions.ParserException;
import dev.gigaherz.util.gddl2.parsing.Lexer;
import dev.gigaherz.util.gddl2.parsing.Parser;
import dev.gigaherz.util.gddl2.parsing.Reader;
import dev.gigaherz.util.gddl2.structure.GddlElement;
import dev.gigaherz.util.gddl2.internal.Utility;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class Query
{
    public static Query fromString(String pathExpression)
    {
        try
        {
            var reader = new Reader(new StringReader(pathExpression), "QueryParser.ParsePath(String)");
            var lexer = new Lexer(reader);
            var parser = new Parser(lexer);
            return parser.parseQuery();
        }
        catch (ParserException | IOException e)
        {
            throw new IllegalStateException(e);
        }
    }

    private boolean absolute = false;
    private final List<QueryComponent> pathComponents = new ArrayList<>();

    public boolean isAbsolute()
    {
        return absolute;
    }

    public List<QueryComponent> pathComponents()
    {
        return Collections.unmodifiableList(pathComponents);
    }

    public Query absolute()
    {
        if (!pathComponents.isEmpty())
            throw new IllegalStateException("Cannot set Absolute after path components have been added.");
        absolute = true;
        return this;
    }

    public Query byKey(String name)
    {
        pathComponents.add(new MapQueryComponent(name));
        return this;
    }

    public Query byRange(Range range)
    {
        pathComponents.add(new ListQueryComponent(range));
        return this;
    }

    public Query self()
    {
        pathComponents.add(SelfQueryComponent.INSTANCE);
        return this;
    }

    public Query parent()
    {
        pathComponents.add(ParentQueryComponent.INSTANCE);
        return this;
    }

    public Stream<GddlElement<?>> apply(GddlElement<?> target)
    {
        var result = Stream.<GddlElement<?>>of(target);

        for (var part : pathComponents)
        {
            result = part.filter(result);
        }

        return result;
    }

    public Query copy()
    {
        var path = new Query();
        copyTo(path);
        return path;
    }

    public void copyTo(Query other)
    {
        for (var component : pathComponents)
        {
            other.pathComponents.add(component.copy());
        }
    }

    @Override
    public boolean equals(Object other)
    {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        return equalsImpl((Query) other);
    }

    public boolean equals(Query other)
    {
        if (this == other) return true;
        if (other == null) return false;
        return equalsImpl(other);
    }

    private boolean equalsImpl(Query other)
    {
        return absolute == other.absolute && Utility.listEquals(pathComponents, other.pathComponents);
    }

    public int hashCode()
    {
        return Objects.hash(absolute, pathComponents);
    }
}

