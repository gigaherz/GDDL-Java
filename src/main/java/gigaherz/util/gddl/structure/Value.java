package gigaherz.util.gddl.structure;

import gigaherz.util.gddl.Lexer;
import gigaherz.util.gddl.config.StringGenerationContext;

import java.util.Locale;

@SuppressWarnings("unused")
public class Value extends Element
{
    Object data;

    Value()
    {
        data = null;
    }

    Value(boolean valueData)
    {
        data = valueData;
    }

    Value(String valueData)
    {
        data = valueData;
    }

    Value(long valueData)
    {
        data = valueData;
    }

    Value(double valueData)
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

    public long getInteger() { return (long) data; }

    public void setInteger(long value) { data = value; }

    public double getDouble() { return (double) data; }

    public void setDouble(double value) { data = value; }

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
        if(!(other instanceof Value))
            throw new IllegalArgumentException("copyTo for invalid type");
        Value b = (Value)other;
        b.setData(getData());
    }

    @Override
    protected String toStringInternal(StringGenerationContext ctx)
    {
        if (data == null)
        {
            return "null";
        }
        if (data instanceof Boolean)
        {
            return getBoolean() ? "true" : "false";
        }
        if (data instanceof String)
        {
            return Lexer.escapeString(getString());
        }
        return String.format(Locale.ROOT, "%s", data.toString());
    }

}
