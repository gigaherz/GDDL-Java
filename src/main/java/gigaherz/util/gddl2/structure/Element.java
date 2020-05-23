package gigaherz.util.gddl2.structure;

import gigaherz.util.gddl2.Lexer;
import gigaherz.util.gddl2.config.StringGenerationContext;
import gigaherz.util.gddl2.config.StringGenerationOptions;
import gigaherz.util.gddl2.util.Utility;

import java.util.Arrays;
import java.util.Objects;

@SuppressWarnings("unused")
public abstract class Element
{
    private String comment;
    private String name;

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

    protected abstract void toStringImpl(StringBuilder builder, StringGenerationContext ctx);

    @Override
    public final String toString()
    {
        return toString(new StringGenerationContext(StringGenerationOptions.Compact));
    }

    public final String toString(StringGenerationContext ctx)
    {
        StringBuilder builder = new StringBuilder();
        toStringWithName(builder, ctx);
        return builder.toString();
    }

    /*package-private*/ void toStringWithName(StringBuilder builder, StringGenerationContext ctx)
    {
        if (hasComment() && ctx.options.writeComments)
        {
            for(String s : getComment().split("(?:(?:\n)|(?:\r\n))"))
            {
                ctx.appendIndent(builder);
                builder.append("#");
                builder.append(s);
                builder.append("\n");
            }
        }
        ctx.appendIndent(builder);
        if (hasName())
        {
            String sname = name;
            if (!Lexer.isValidIdentifier(sname))
                sname = Lexer.escapeString(sname);
            builder.append(sname);
            builder.append(" = ");
        }

        toStringImpl(builder, ctx);
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

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Element element = (Element) o;

        return ((Utility.isNullOrEmpty(comment) && Utility.isNullOrEmpty(element.comment)) || Objects.equals(comment, element.comment)) &&
                Objects.equals(name, element.name);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(comment, name);
    }
}