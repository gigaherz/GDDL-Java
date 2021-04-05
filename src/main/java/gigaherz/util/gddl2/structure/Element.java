package gigaherz.util.gddl2.structure;

import gigaherz.util.gddl2.serialization.Formatter;
import gigaherz.util.gddl2.util.Utility;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@SuppressWarnings("unused")
public abstract class Element<T extends Element<T>>
{
    // For name tracking purposes
    private Collection parent;

    String comment;
    String name;

    Element() {}

    // Actual instance methods

    /**
     * Gets the current comment attached to this element.
     * @return The comment if present, or null
     */
    @Nullable
    public String getComment()
    {
        return comment;
    }

    /**
     * Sets or removes the comment attached to this element.
     * @param value The new comment, or null to remove the comment.
     */
    public void setComment(@Nullable String value)
    {
        comment = value;
    }

    /**
     * @return True if this element has a comment attached
     */
    public boolean hasComment()
    {
        return !Utility.isNullOrEmpty(comment);
    }

    /**
     * Gets the current name of this element.
     * Only meaningful for elements contained in a collection.
     * @return The name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets or removes the name of this element.
     * @param name The new name, or null to remove the name.
     */
    public void setName(@Nullable String name)
    {
        if (parent != null)
            parent.setName(this, name);
        else
            setNameInternal(name);
    }

    void setNameInternal(String name)
    {
        this.name = name;
    }

    public boolean hasName()
    {
        return !Utility.isNullOrEmpty(name);
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

    public Element<?> simplify()
    {
        return this;
    }

    public void resolve(Element<?> root, @Nullable Collection parent)
    {
    }

    /**
     * @return True if this element is not a reference, or the target of the reference has been determined
     */
    public boolean isResolved()
    {
        return true;
    }

    /**
     * @return The actual element this element represents, accounting for references
     */
    public Element<?> resolvedValue()
    {
        return this;
    }

    @Override
    public final String toString()
    {
        return Formatter.formatCompact(this);
    }

    public final T copy()
    {
        T c = copyInternal();
        c.resolve(this, null);
        return c;
    }

    protected abstract T copyInternal();

    protected void copyTo(T other)
    {
        if (hasName())
            other.setName(getName());
    }

    public T withName(String name)
    {
        this.setName(name);
        //noinspection unchecked
        return (T)this;
    }

    @Override
    public abstract boolean equals(Object o);

    public boolean equals(T other)
    {
        if (this == other) return true;
        if (null == other) return false;
        return ((Utility.isNullOrEmpty(comment) && Utility.isNullOrEmpty(other.comment)) || Objects.equals(comment, other.comment)) &&
                Objects.equals(name, other.name);
    }

    protected boolean equalsImpl(T other)
    {
        return ((Utility.isNullOrEmpty(comment) && Utility.isNullOrEmpty(other.comment)) || Objects.equals(comment, other.comment)) &&
                Objects.equals(name, other.name);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(comment, name);
    }

    @Nullable
    Collection getParentInternal()
    {
        return parent;
    }

    void setParentInternal(@Nullable Collection parent)
    {
        this.parent = parent;
    }
}