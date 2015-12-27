package gigaherz.utils.GDDL.structure;

import gigaherz.utils.GDDL.ContextProvider;
import gigaherz.utils.GDDL.Lexer;
import gigaherz.utils.GDDL.config.StringGenerationContext;
import gigaherz.utils.GDDL.exceptions.ParserException;

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

    public boolean isNull()
    {
        return data == null;
    }

    @Override
    protected String toStringInternal()
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

    @Override
    protected String toStringInternal(StringGenerationContext ctx)
    {
        return toStringInternal();
    }

}
