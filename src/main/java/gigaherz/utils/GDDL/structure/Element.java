package gigaherz.utils.GDDL.structure;

import gigaherz.utils.GDDL.Lexer;
import gigaherz.utils.GDDL.config.StringGenerationContext;
import gigaherz.utils.GDDL.config.StringGenerationOptions;

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

    public static Backreference backreference(boolean rooted, String... parts)
    {
        return new Backreference(rooted, parts);
    }

    public static Value nullValue()
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

    public static Value floatValue(double num)
    {
        return new Value(num);
    }

    public static Value stringValue(String s)
    {
        return new Value(s);
    }

    // Actual instance methods
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

    public Element simplify()
    {
        return this;
    }

    public void resolve(Element root, Element parent)
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

    protected abstract String toStringInternal(StringGenerationContext ctx);

    @Override
    public final String toString()
    {
        return toString(new StringGenerationContext(StringGenerationOptions.Compact));
    }

    public final String toString(StringGenerationContext ctx)
    {
        if (hasName())
        {
            String sname = name;
            if(!Lexer.isValidIdentifier(sname))
                sname = Lexer.escapeString(sname);
            return String.format("%s = %s", sname, toStringInternal(ctx));
        }

        return toStringInternal(ctx);
    }

    protected abstract Element copy();
    protected void copyTo(Element other)
    {
        if(hasName())
            other.setName(getName());
    }
}