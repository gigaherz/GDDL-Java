package dev.gigaherz.util.gddl2.structure;

import dev.gigaherz.util.gddl2.serialization.Formatter;
import dev.gigaherz.util.gddl2.util.MappingResult;
import dev.gigaherz.util.gddl2.util.Utility;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public abstract class Element<T extends Element<T>>
{
    //region API

    /**
     * @return True if this element has whitespace attached
     */
    public boolean hasWhitespace()
    {
        return !Utility.isNullOrEmpty(whitespace);
    }

    /**
     * Gets the current whitespace attached to this element.
     *
     * @return The whitespace if present, or an empty string
     */
    @NotNull
    public String getWhitespace()
    {
        return whitespace;
    }

    /**
     * Sets or removes the comment attached to this element.
     *
     * @param value The new comment, or null to remove the comment.
     */
    public void setWhitespace(@NotNull String value)
    {
        whitespace = Objects.requireNonNull(value);
    }

    /**
     * Sets the name and returns itself. Useful for chaining on initialization.
     *
     * @param whitespace The new whitespace, or null to remove the whitespace.
     * @return The same instance the method was called on.
     */
    public T withWhitespace(String whitespace)
    {
        this.setWhitespace(whitespace);
        //noinspection unchecked
        return (T) this;
    }

    /**
     * @return True if this element has a comment attached
     */
    public boolean hasComment()
    {
        return !Utility.isNullOrEmpty(comment);
    }

    /**
     * Gets the current comment attached to this element.
     *
     * @return The comment if present, or an empty string
     */
    @NotNull
    public String getComment()
    {
        return comment;
    }

    /**
     * Sets or removes the comment attached to this element.
     *
     * @param value The new comment, or null to remove the comment.
     */
    public void setComment(@NotNull String value)
    {
        comment = Objects.requireNonNull(value);
    }

    /**
     * Sets the name and returns itself. Useful for chaining on initialization.
     *
     * @param comment The new comment, or null to remove the comment.
     * @return The same instance the method was called on.
     */
    public T withComment(String comment)
    {
        this.setComment(comment);
        //noinspection unchecked
        return (T) this;
    }

    /**
     * @return True of the name is set and not empty.
     */
    public boolean hasName()
    {
        return !Utility.isNullOrEmpty(name);
    }

    /**
     * Gets the current name of this element.
     * Only meaningful for elements contained in a collection.
     *
     * @return The name
     */
    @Nullable
    public String getName()
    {
        return name;
    }

    /**
     * Sets or removes the name of this element.
     *
     * @param name The new name, or null to remove the name.
     */
    public void setName(@Nullable String name)
    {
        if (parent != null)
            parent.setName(this, name);
        else
            setNameInternal(name);
    }

    /**
     * Sets the name and returns itself. Useful for chaining on initialization.
     *
     * @param name The new name, or null to remove the name.
     * @return The same instance the method was called on.
     */
    public T withName(String name)
    {
        this.setName(name);
        //noinspection unchecked
        return (T) this;
    }

    /**
     * @return True if this element is a Collection
     */
    public boolean isCollection()
    {
        return this instanceof Collection;
    }

    /**
     * Casts the instance to Collection.
     *
     * @return Itself
     * @throws ClassCastException If the object is not a Collection
     */
    public Collection asCollection()
    {
        return (Collection) this;
    }

    /**
     * If this element is a Collection, runs the consumer.
     *
     * @param consumer The function to apply if the current element
     */
    public void ifCollection(Consumer<Collection> consumer)
    {
        if (isCollection())
            consumer.accept(asCollection());
    }

    /**
     * @return True if this element is a Value
     */
    public boolean isValue()
    {
        return this instanceof Value;
    }

    /**
     * Casts the instance to Value.
     *
     * @return Itself
     * @throws ClassCastException If the object is not a Value
     */
    public Value asValue()
    {
        return (Value) this;
    }

    /**
     * If this element is a Value, runs the consumer.
     *
     * @param consumer The function to apply if the current element
     */
    public void ifValue(Consumer<Value> consumer)
    {
        if (isValue())
            consumer.accept(asValue());
    }

    /**
     * @return True if this element is a Value
     */
    public boolean isReference()
    {
        return this instanceof Reference;
    }

    /**
     * Casts the instance to Reference.
     *
     * @return Itself
     * @throws ClassCastException If the object is not a Reference
     */
    public Reference asReference()
    {
        return (Reference) this;
    }

    /**
     * If this element is a Value, runs the consumer.
     *
     * @param consumer The function to apply if the current element
     */
    public void ifReference(Consumer<Value> consumer)
    {
        if (isValue())
            consumer.accept(asValue());
    }

    /**
     * Determines wether the contained value is `null`
     */
    public boolean isNull()
    {
        return false;
    }

    /**
     * Determines wether the contained value is a string
     */
    public boolean isString()
    {
        return false;
    }

    /**
     * Gets the string contained in this value.
     *
     * @throws ClassCastException If the contained value is not actually a string.
     */
    public String asString()
    {
        throw new IllegalStateException("This element is not a value.");
    }

    /**
     * Determines wether the contained value is a boolean
     */
    public boolean isBoolean()
    {
        return false;
    }

    /**
     * Gets the boolean contained in this value.
     *
     * @throws ClassCastException If the contained value is not actually a boolean.
     */
    public boolean asBoolean()
    {
        throw new IllegalStateException("This element is not a value.");
    }

    /**
     * Determines wether the contained value is an integer
     */
    public boolean isInteger()
    {
        return false;
    }

    /**
     * Gets the integer contained in this value.
     *
     * @throws ClassCastException If the contained value is not actually an integer.
     */
    public long asInteger()
    {
        throw new IllegalStateException("This element is not a value.");
    }

    /**
     * Determines wether the contained value is a floating-point number
     */
    public boolean isDouble()
    {
        return false;
    }

    /**
     * Gets the floating-point number contained in this value.
     *
     * @throws ClassCastException If the contained value is not actually a floating-point number.
     */
    public double asDouble()
    {
        throw new IllegalStateException("This element is not a value.");
    }

    /**
     * If this element is a Collection, applies a mapping that returns a new Element.
     * Otherwise, returns itself.
     *
     * @param mapping The function to apply if the current element
     * @return The mapped value, or itself.
     */
    public <TResult> MappingResult<TResult> when()
    {
        return MappingResult.remainder(this);
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

    //endregion

    //region Implementation
    private Collection parent;

    String whitespace = "";
    String comment = "";
    String name;

    Element()
    {
    }

    void setNameInternal(@Nullable String name)
    {
        this.name = name;
    }

    protected abstract T copyInternal();

    protected void copyTo(T other)
    {
        if (hasName())
            other.setName(getName());
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
    //endregion

    //region Equality
    @Override
    public abstract boolean equals(Object o);

    public abstract boolean equals(T other);

    protected boolean equalsImpl(T other)
    {
        return ((Utility.isNullOrEmpty(whitespace) && Utility.isNullOrEmpty(other.whitespace)) || Objects.equals(whitespace, other.whitespace)) &&
                ((Utility.isNullOrEmpty(comment) && Utility.isNullOrEmpty(other.comment)) || Objects.equals(comment, other.comment)) &&
                Objects.equals(name, other.name);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(comment, name);
    }
    //endregion
}