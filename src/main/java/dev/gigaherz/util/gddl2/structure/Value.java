package dev.gigaherz.util.gddl2.structure;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Represents a simple value within the GDDL hierarchy.
 * Simple values are: null, boolean false and true, strings, integers (long), and floats (double).
 */
@SuppressWarnings("unused")
public final class Value extends Element<Value>
{
    //region API

    /**
     * Constructs a Value representing `null`
     *
     * @return The value
     */
    public static Value nullValue()
    {
        return new Value();
    }

    /**
     * Constructs a Value representing the given boolean
     *
     * @return The value
     */
    public static Value of(boolean value)
    {
        return new Value(value);
    }

    /**
     * Constructs a Value representing the given long integer
     *
     * @return The value
     */
    public static Value of(long num)
    {
        return new Value(num);
    }

    /**
     * Constructs a Value representing the given floating-point number
     *
     * @return The value
     */
    public static Value of(double num)
    {
        return new Value(num);
    }

    /**
     * Constructs a Value representing the given string
     *
     * @return The value
     */
    public static Value of(String s)
    {
        return new Value(s);
    }

    @Override
    public String asString()
    {
        return (String) Objects.requireNonNull(data);
    }

    @Override
    public boolean asBoolean()
    {
        return (boolean) Objects.requireNonNull(data);
    }

    @Override
    public long asInteger()
    {
        return (long) Objects.requireNonNull(data);
    }

    @Override
    public double asDouble()
    {
        return (double) Objects.requireNonNull(data);
    }

    @Override
    public boolean isNull()
    {
        return data == null;
    }

    @Override
    public boolean isBoolean()
    {
        return data instanceof Boolean;
    }

    @Override
    public boolean isInteger()
    {
        return data instanceof Long;
    }

    @Override
    public boolean isDouble()
    {
        return data instanceof Double;
    }

    @Override
    public boolean isString()
    {
        return data instanceof String;
    }
    //endregion

    //region Implementation
    private Object data;

    private Value()
    {
        data = null;
    }

    private Value(boolean valueData)
    {
        data = valueData;
    }

    private Value(String valueData)
    {
        data = valueData;
    }

    private Value(long valueData)
    {
        data = valueData;
    }

    private Value(double valueData)
    {
        data = valueData;
    }
    //endregion

    //region Element
    @Override
    protected Value copyInternal()
    {
        Value value = new Value();
        copyTo(value);
        return value;
    }

    @Override
    protected void copyTo(Value other)
    {
        super.copyTo(other);
        other.data = data;
    }
    //endregion

    //region Equality
    @Override
    public boolean equals(Object other)
    {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        return equalsImpl((Value) other);
    }

    @Override
    public boolean equals(Value other)
    {
        if (this == other) return true;
        if (other == null) return false;
        return equalsImpl(other);
    }

    @Override
    protected boolean equalsImpl(@NotNull Value value)
    {
        return super.equalsImpl(value) && Objects.equals(data, value.data);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), data);
    }
    //endregion
}
