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
public abstract class GddlElement<T extends GddlElement<T>>
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
     * @return True if this element is a Collection
     */
    public boolean isMap()
    {
        return false;
    }

    /**
     * Casts the instance to Collection.
     *
     * @return Itself
     * @throws IllegalStateException If the object is not a Collection
     */
    public GddlMap asMap()
    {
        throw new IllegalStateException("This element is not a Collection.");
    }

    /**
     * If this element is a Collection, runs the consumer.
     *
     * @param consumer The function to apply if the current element
     */
    public void ifMap(Consumer<GddlMap> consumer)
    {
        if (isMap())
            consumer.accept(asMap());
    }

    /**
     * @return True if this element is a Collection
     */
    public boolean isList()
    {
        return false;
    }

    /**
     * Casts the instance to Collection.
     *
     * @return Itself
     * @throws IllegalStateException If the object is not a Collection
     */
    public GddlList asList()
    {
        throw new IllegalStateException("This element is not a Collection.");
    }

    /**
     * If this element is a Collection, runs the consumer.
     *
     * @param consumer The function to apply if the current element
     */
    public void ifList(Consumer<GddlList> consumer)
    {
        if (isList())
            consumer.accept(asList());
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
    public GddlValue asValue()
    {
        throw new IllegalStateException("This element is not a Value.");
    }

    /**
     * If this element is a Value, runs the consumer.
     *
     * @param consumer The function to apply if the current element
     */
    public void ifValue(Consumer<GddlValue> consumer)
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
    public GddlReference asReference()
    {
        throw new IllegalStateException("This element is not a Reference.");
    }

    /**
     * If this element is a Reference, runs the consumer.
     *
     * @param consumer The function to apply if the current element
     */
    public void ifReference(Consumer<GddlValue> consumer)
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

    public <TResult> TResult when(Function<MappingResult<GddlElement<?>>, TResult> mapping)
    {
        return mapping.apply(MappingResult.remainder(this));
    }

    public <TResult> MappingResult<TResult> when()
    {
        return MappingResult.remainder(this);
    }

    public GddlElement<?> simplify()
    {
        return this;
    }

    public void resolve(GddlElement<?> root)
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
    public GddlElement<?> resolvedValue()
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
    public GddlElement<?> getParent()
    {
        return parent;
    }

    //endregion

    //region Implementation
    private GddlElement<?> parent;

    String whitespace = "";
    String comment = "";

    GddlElement()
    {
    }

    protected abstract T copyInternal();

    protected void copyTo(T other)
    {
        if (hasWhitespace())
            other.setWhitespace(getWhitespace());
        if (hasComment())
            other.setComment(getComment());
    }

    void setParent(@Nullable GddlElement<?> parent)
    {
        this.parent = parent;
    }
    //endregion

    //region Equality
    @Override
    public abstract boolean equals(Object o);

    public abstract boolean equals(T other);

    @Override
    public int hashCode()
    {
        return Objects.hash(whitespace, comment);
    }

    public int getFormattingComplexity()
    {
        return 1;
    }
    //endregion
}