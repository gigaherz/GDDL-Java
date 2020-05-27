package gigaherz.util.gddl2.structure;

import gigaherz.util.gddl2.serialization.Formatter;
import gigaherz.util.gddl2.util.Utility;

import java.util.Objects;

@SuppressWarnings("unused")
public abstract class Element
{
    private String comment;
    private String name;

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

    @Override
    public final String toString()
    {
        return Formatter.formatCompact(this);
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
        Element other = (Element) o;

        return ((Utility.isNullOrEmpty(comment) && Utility.isNullOrEmpty(other.comment)) || Objects.equals(comment, other.comment)) &&
                Objects.equals(name, other.name);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(comment, name);
    }
}