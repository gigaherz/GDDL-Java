package gigaherz.utils.GDDL.structure;

import gigaherz.utils.GDDL.ContextProvider;
import gigaherz.utils.GDDL.config.StringGenerationContext;
import gigaherz.utils.GDDL.exceptions.ParserException;

import java.util.Arrays;
import java.util.Collection;

@SuppressWarnings("unused")
public abstract class Element
{
    private String name;

    // Factory methods
    public static Set set(Element... initial)
    {
        return new Set(Arrays.asList(initial));
    }

    public static Set set(Collection<Element> initial)
    {
        return new Set(initial);
    }

    public static Backreference backreference(boolean rooted, String name)
    {
        return new Backreference(rooted, name);
    }

    public static Value Null()
    {
        return new Value();
    }

    public static Value booleanValue(boolean value)
    {
        return new Value(value);
    }

    public static Value intValue(long num)
    {
        return new Value(num);
    }

    public static Value intValue(String text)
    {
        return new Value(Long.parseLong(text));
    }

    public static Value intValue(String text, int _base)
    {
        return new Value(Long.parseLong(text.substring(2), 16));
    }

    public static Value floatValue(double num)
    {
        return new Value(num);
    }

    public static Value floatValue(String text)
    {
        return new Value(Double.parseDouble(text));
    }

    public static Value stringValue(ContextProvider ctx, String text) throws ParserException
    {
        if (text.charAt(0) == '"' || text.charAt(0) == '\'')
            return new Value(Value.unescapeString(ctx, text));

        return new Value(text);
    }

    public String getName()
    {
        return name;
    }

    public void setName(String value)
    {
        name = value;
    }

    public boolean hasName()
    {
        return name != null;
    }

    public boolean isSet()
    {
        return this instanceof Set;
    }

    public Set asSet()
    {
        return (Set) this;
    }

    public boolean isValue()
    {
        return this instanceof Value;
    }

    public Value asValue()
    {
        return (Value) this;
    }

    // Removes single-element non-named sets and backreferences
    public Element simplify()
    {
        return this;
    }

    public void resolve(Element root)
    {
    }

    public boolean isResolved()
    {
        return true;
    }

    public Element resolvedValue()
    {
        return this;
    }

    protected abstract String toStringInternal();

    protected abstract String toStringInternal(StringGenerationContext ctx);

    public String toString()
    {
        if (hasName())
        {
            return String.format("%s = %s", name, toStringInternal());
        }

        return toStringInternal();
    }

    public String toString(StringGenerationContext ctx)
    {
        if (hasName())
        {
            return String.format("%s = %s", name, toStringInternal(ctx));
        }

        return toStringInternal(ctx);
    }
}