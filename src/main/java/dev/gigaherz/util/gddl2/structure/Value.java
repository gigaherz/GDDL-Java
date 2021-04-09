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
    //region Factory Methods
    /**
     * Constructs a Value representing `null`
     * @return The value
     */
    public static Value nullValue()
    {
        return new Value();
    }

    /**
     * Constructs a Value representing the given boolean
     * @return The value
     */
    public static Value of(boolean value)
    {
        return new Value(value);
    }

    /**
     * Constructs a Value representing the given long integer
     * @return The value
     */
    public static Value of(long num)
    {
        return new Value(num);
    }

    /**
     * Constructs a Value representing the given floating-point number
     * @return The value
     */
    public static Value of(double num)
    {
        return new Value(num);
    }

    /**
     * Constructs a Value representing the given string
     * @return The value
     */
    public static Value of(String s)
    {
        return new Value(s);
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

    /**
     * Changes the contained value to be `null`
     */
    public void setNull()
    {
        data = null;
    }

    /**
     * Gets the string contained in this value.
     * @throws ClassCastException If the contained value is not actually a string.
     */
    public String getString()
    {
        return (String) Objects.requireNonNull(data);
    }

    /**
     * Changes the contained value to be the given string
     */
    public void setString(String value)
    {
        data = value;
    }

    /**
     * Gets the boolean contained in this value.
     * @throws ClassCastException If the contained value is not actually a boolean.
     */
    public boolean getBoolean()
    {
        return (boolean) Objects.requireNonNull(data);
    }

    /**
     * Changes the contained value to be the given boolean
     */
    public void setBoolean(boolean value)
    {
        data = value;
    }

    /**
     * Gets the integer contained in this value.
     * @throws ClassCastException If the contained value is not actually an integer.
     */
    public long getInteger()
    {
        return (long) Objects.requireNonNull(data);
    }

    /**
     * Changes the contained value to be the given long integer
     */
    public void setInteger(long value)
    {
        data = value;
    }

    /**
     * Gets the floating-point number contained in this value.
     * @throws ClassCastException If the contained value is not actually a floating-point number.
     */
    public double getDouble()
    {
        return (double) Objects.requireNonNull(data);
    }

    /**
     * Changes the contained value to be the given floating-point number
     */
    public void setDouble(double value)
    {
        data = value;
    }

    /**
     * Determines wether the contained value is `null`
     */
    public boolean isNull()
    {
        return data == null;
    }

    /**
     * Determines wether the contained value is a boolean
     */
    public boolean isBoolean()
    {
        return data instanceof Boolean;
    }

    /**
     * Determines wether the contained value is an integer
     */
    public boolean isInteger()
    {
        return data instanceof Long;
    }

    /**
     * Determines wether the contained value is a floating-point number
     */
    public boolean isDouble()
    {
        return data instanceof Double;
    }

    /**
     * Determines wether the contained value is a string
     */
    public boolean isString()
    {
        return data instanceof String;
    }

    @Override
    public Value withName(String name)
    {
        super.withName(name);
        return this;
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
        if (null == other) return false;
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
