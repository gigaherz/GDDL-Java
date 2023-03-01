package dev.gigaherz.util.gddl2.structure;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Represents a simple value within the GDDL hierarchy.
 * Simple values are: null, boolean false and true, strings, integers (long), and floats (double).
 */
@SuppressWarnings("unused")
public final class GddlValue extends GddlElement<GddlValue>
{
    //region API

    /**
     * Constructs a Value representing `null`
     *
     * @return The value
     */
    public static GddlValue nullValue()
    {
        return new GddlValue();
    }

    /**
     * Constructs a Value representing the given boolean
     *
     * @return The value
     */
    public static GddlValue of(boolean value)
    {
        return new GddlValue(value);
    }

    /**
     * Constructs a Value representing the given long integer
     *
     * @return The value
     */
    public static GddlValue of(long num)
    {
        return new GddlValue(num);
    }

    /**
     * Constructs a Value representing the given floating-point number
     *
     * @return The value
     */
    public static GddlValue of(double num)
    {
        return new GddlValue(num);
    }

    /**
     * Constructs a Value representing the given string
     *
     * @return The value
     */
    public static GddlValue of(String s)
    {
        return new GddlValue(s);
    }

    @Override
    public boolean isValue()
    {
        return true;
    }

    @Override
    public GddlValue asValue()
    {
        return this;
    }

    @Override
    public String stringValue()
    {
        return (String) Objects.requireNonNull(data);
    }

    @Override
    public boolean booleanValue()
    {
        return (boolean) Objects.requireNonNull(data);
    }

    @Override
    public byte byteValue()
    {
        return (byte)longValue();
    }

    @Override
    public short shortValue()
    {
        return (short)longValue();
    }

    @Override
    public int intValue()
    {
        return (int)longValue();
    }

    @Override
    public long longValue()
    {
        return (long) Objects.requireNonNull(data);
    }

    @Override
    public float floatValue()
    {
        return (float)doubleValue();
    }

    @Override
    public double doubleValue()
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

    private GddlValue()
    {
        data = null;
    }

    private GddlValue(boolean valueData)
    {
        data = valueData;
    }

    private GddlValue(String valueData)
    {
        data = valueData;
    }

    private GddlValue(long valueData)
    {
        data = valueData;
    }

    private GddlValue(double valueData)
    {
        data = valueData;
    }
    //endregion

    //region Element
    @Override
    protected GddlValue copyInternal()
    {
        GddlValue value = new GddlValue();
        copyTo(value);
        return value;
    }

    @Override
    protected void copyTo(GddlValue other)
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
        return equalsImpl((GddlValue) other);
    }

    @Override
    public boolean equals(GddlValue other)
    {
        if (this == other) return true;
        if (other == null) return false;
        return equalsImpl(other);
    }

    private boolean equalsImpl(@NotNull GddlValue value)
    {
        return Objects.equals(data, value.data);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), data);
    }
    //endregion
}
