package gigaherz.util.gddl2.structure;

import java.util.Objects;

@SuppressWarnings("unused")
public class Value extends Element
{
    // Factory Methods
    public static Value nullValue()
    {
        return new Value();
    }

    public static Value of(boolean value)
    {
        return new Value(value);
    }

    public static Value of(long num)
    {
        return new Value(num);
    }

    public static Value of(double num)
    {
        return new Value(num);
    }

    public static Value of(String s)
    {
        return new Value(s);
    }

    // Implementation
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

    public void setNull()
    {
        data = null;
    }

    public String getString()
    {
        return (String) Objects.requireNonNull(data);
    }

    public void setString(String value)
    {
        data = value;
    }

    public boolean getBoolean()
    {
        return (boolean) Objects.requireNonNull(data);
    }

    public void setBoolean(boolean value)
    {
        data = value;
    }

    public long getInteger()
    {
        return (long) Objects.requireNonNull(data);
    }

    public void setInteger(long value)
    {
        data = value;
    }

    public double getDouble()
    {
        return (double) Objects.requireNonNull(data);
    }

    public void setDouble(double value)
    {
        data = value;
    }

    public boolean isNull()
    {
        return data == null;
    }

    public boolean isBoolean()
    {
        return data instanceof Boolean;
    }

    public boolean isInteger()
    {
        return data instanceof Long;
    }

    public boolean isDouble()
    {
        return data instanceof Double;
    }

    public boolean isString()
    {
        return data instanceof String;
    }

    public Value withName(String name)
    {
        super.withName(name);
        return this;
    }

    @Override
    protected Value copy()
    {
        Value b = new Value();
        copyTo(b);
        return b;
    }

    @Override
    protected void copyTo(Element other)
    {
        super.copyTo(other);
        if (!(other instanceof Value))
            throw new IllegalArgumentException("copyTo for invalid type");
        Value b = (Value) other;
        b.data = data;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Value value = (Value) o;
        return Objects.equals(data, value.data);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), data);
    }
}
