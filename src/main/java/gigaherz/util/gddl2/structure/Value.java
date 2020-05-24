package gigaherz.util.gddl2.structure;

import gigaherz.util.gddl2.Lexer;
import gigaherz.util.gddl2.config.StringGenerationContext;

import java.util.Locale;
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
    Object data;

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

    public Object getData()
    {
        return data;
    }

    void setData(Object value)
    {
        data = value;
    }

    public String getString()
    {
        return (String) data;
    }

    public void setString(String value)
    {
        data = value;
    }

    public boolean getBoolean()
    {
        return (boolean) data;
    }

    public void setBoolean(boolean value)
    {
        data = value;
    }

    public long getInteger()
    {
        return (long) data;
    }

    public void setInteger(long value)
    {
        data = value;
    }

    public double getDouble()
    {
        return (double) data;
    }

    public void setDouble(double value)
    {
        data = value;
    }

    public boolean isNull()
    {
        return data == null;
    }

    @Override
    protected Element copy()
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
        b.setData(getData());
    }

    @Override
    protected void toStringImpl(StringBuilder builder, StringGenerationContext ctx)
    {
        if (data == null)
        {
            builder.append("null");
        }
        else if (data instanceof Boolean)
        {
            builder.append(getBoolean() ? "true" : "false");
        }
        else if (data instanceof String)
        {
            builder.append(Lexer.escapeString(getString()));
        }
        else
        {
            builder.append(String.format(Locale.ROOT, "%s", data.toString()));
        }
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
