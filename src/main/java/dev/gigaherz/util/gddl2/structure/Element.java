package dev.gigaherz.util.gddl2.structure;

import dev.gigaherz.util.gddl2.serialization.Formatter;
import dev.gigaherz.util.gddl2.util.MappingResult;
import dev.gigaherz.util.gddl2.util.Utility;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

@SuppressWarnings("unused")
public abstract class Element<T extends Element<T>>
{
    //region API

    /**
     * @return True if this element has whitespace attached
     */
    public final boolean hasWhitespace()
    {
        return whitespace.length() > 0;
    }

    /**
     * Gets the current whitespace attached to this element.
     *
     * @return The whitespace if present, or an empty string
     */
    @NotNull
    public final String getWhitespace()
    {
        return whitespace;
    }

    /**
     * Sets or removes the comment attached to this element.
     *
     * @param value The new comment, or null to remove the comment.
     */
    public final void setWhitespace(@NotNull String value)
    {
        whitespace = Objects.requireNonNull(value);
    }

    /**
     * Sets the name and returns itself. Useful for chaining on initialization.
     *
     * @param whitespace The new whitespace, or null to remove the whitespace.
     * @return The same instance the method was called on.
     */
    public final T withWhitespace(String whitespace)
    {
        this.setWhitespace(whitespace);
        //noinspection unchecked
        return (T) this;
    }

    /**
     * @return True if this element has a comment attached
     */
    public final boolean hasComment()
    {
        return comment.length() > 0;
    }

    /**
     * Gets the current comment attached to this element.
     *
     * @return The comment if present, or an empty string
     */
    @NotNull
    public final String getComment()
    {
        return comment;
    }

    /**
     * Sets or removes the comment attached to this element.
     *
     * @param value The new comment, or null to remove the comment.
     */
    public final void setComment(@NotNull String value)
    {
        comment = Objects.requireNonNull(value);
    }

    /**
     * Sets the name and returns itself. Useful for chaining on initialization.
     *
     * @param comment The new comment, or null to remove the comment.
     * @return The same instance the method was called on.
     */
    public final T withComment(String comment)
    {
        this.setComment(comment);
        //noinspection unchecked
        return (T) this;
    }

    /**
     * @return True of the name is set and not empty.
     */
    public final boolean hasName()
    {
        return name.length() > 0;
    }

    /**
     * Gets the current name of this element.
     * Only meaningful for elements contained in a collection.
     *
     * @return The name
     */
    @NotNull
    public final String getName()
    {
        return name;
    }

    /**
     * Sets or removes the name of this element.
     *
     * @param name The new name, or null to remove the name.
     */
    public final void setName(@NotNull String name)
    {
        Objects.requireNonNull(name);
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
    public final T withName(@NotNull String name)
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
        return false;
    }

    /**
     * Casts the instance to Collection.
     *
     * @return Itself
     * @throws ClassCastException If the object is not a Collection
     */
    public Collection asCollection()
    {
        throw new IllegalStateException("This element is not a Collection.");
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
        return false;
    }

    /**
     * Casts the instance to Value.
     *
     * @return Itself
     * @throws IllegalStateException If the object is not a Value
     */
    public Value asValue()
    {
        throw new IllegalStateException("This element is not a Value.");
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
     * @return True if this element is a Reference
     */
    public boolean isReference()
    {
        return false;
    }

    /**
     * Casts the instance to Reference.
     *
     * @return Itself
     * @throws IllegalStateException If the object is not a Reference
     */
    public Reference asReference()
    {
        throw new IllegalStateException("This element is not a Reference.");
    }

    /**
     * If this element is a Reference, runs the consumer.
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
     * If this element is a Null value, runs the runnable.
     *
     * @param runnable The code to run if the current element is a Null Value
     */
    public void ifNull(Runnable runnable)
    {
        if (isNull())
            runnable.run();
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
     * @throws IllegalStateException If the contained value is not actually a string.
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
     * @throws IllegalStateException If the contained value is not actually a boolean.
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
     * @throws IllegalStateException If the contained value is not actually an integer.
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
     * @throws IllegalStateException If the contained value is not actually a floating-point number.
     */
    public double asDouble()
    {
        throw new IllegalStateException("This element is not a value.");
    }

    public <TResult> TResult when(Function<MappingResult<Element<?>>, TResult> mapping)
    {
        return mapping.apply(MappingResult.remainder(this));
    }

    public <TResult> MappingResult<TResult> when()
    {
        return MappingResult.remainder(this);
    }

    public Element<?> simplify()
    {
        return this;
    }

    public void resolve(Element<?> root)
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
        c.resolve(this);
        return c;
    }

    @Nullable
    public Collection getParent()
    {
        return parent;
    }

    //endregion

    //region Implementation
    private Collection parent;

    String whitespace = "";
    String comment = "";
    String name = "";

    Element()
    {
    }

    void setNameInternal(@NotNull String name)
    {
        this.name = name;
    }

    protected abstract T copyInternal();

    protected void copyTo(T other)
    {
        if (hasName())
            other.setName(getName());
    }

    void setParent(@Nullable Collection parent)
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