package gigaherz.util.gddl2.structure;

import gigaherz.util.gddl2.Lexer;
import gigaherz.util.gddl2.config.StringGenerationContext;
import gigaherz.util.gddl2.config.StringGenerationOptions;

import java.util.Arrays;

@SuppressWarnings("unused")
public abstract class Element
{
    private String comment;
    private String name;

    // Factory methods
    public static Collection set(Element... initial)
    {
        return new Collection(Arrays.asList(initial));
    }

    public static Collection set(java.util.Collection initial)
    {
        return new Collection(initial);
    }

    public static Reference backreference(boolean rooted, String... parts)
    {
        return new Reference(rooted, parts);
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
    public String getComment()
    {
        return comment;
    }

    public void setComment(String value)
    {
        comment = value;
    }

    public boolean hasComment() {return comment != null && comment.length() > 0; }

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

    public boolean isCollection()
    {
        return this instanceof Collection;
    }

    public Collection asCollection()
    {
        return (Collection) this;
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

    protected abstract String toStringImpl(StringGenerationContext ctx);

    @Override
    public final String toString()
    {
        return toString(new StringGenerationContext(StringGenerationOptions.Compact));
    }

    public final String toString(StringGenerationContext ctx)
    {
        if (ctx.IndentLevel == 0)
        {
            StringBuilder builder = new StringBuilder();
            if (hasComment() && ctx.Options.writeComments)
            {
                for(String s : getComment().split("((\n)|(\r\n))"))
                {
                    builder.append("#");
                    builder.append(s);
                    builder.append("\n");
                }
            }
            builder.append(toStringWithName(ctx));
            return builder.toString();
        }
        else {
            return toStringWithName(ctx);
        }
    }

    private String toStringWithName(StringGenerationContext ctx)
    {
        if (hasName())
        {
            String sname = name;
            if (!Lexer.isValidIdentifier(sname))
                sname = Lexer.escapeString(sname);
            return String.format("%s = %s", sname, toStringImpl(ctx));
        }

        return toStringImpl(ctx);
    }

    protected abstract Element copy();

    protected void copyTo(Element other)
    {
        if (hasName())
            other.setName(getName());
    }

    public Element withName(String name)
    {
        this.setName(name);
        return this;
    }
}