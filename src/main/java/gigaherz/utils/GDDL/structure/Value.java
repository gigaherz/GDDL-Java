package gigaherz.utils.GDDL.structure;

import gigaherz.utils.GDDL.FileContext;
import gigaherz.utils.GDDL.config.StringGenerationContext;
import gigaherz.utils.GDDL.exceptions.ParserException;

import java.util.Locale;

public class Value extends Element
{
    Object data;
    
    public Object getData() {return data;}

    public String getString() {return (String)data; }
    public void setString(String value) {data = value;}

    public boolean getBoolean() {return (boolean)data; }
    public void setBoolean(boolean value) {data = value;}

    public boolean isNull() {return data == null;}

    Value() { data = null; }

    Value(boolean valueData) { data = valueData; }

    Value(String valueData) { data = valueData; }

    Value(long valueData) { data = valueData; }

    Value(double valueData) { data = valueData; }

    static String unescapeString(FileContext context, String p) throws ParserException
    {
        StringBuilder sb = new StringBuilder();

        char q = (char)0;
        boolean b = false;
        boolean u = false;

        int u1 = 0;
        int u2 = 0;

        for (char c : p.toCharArray())
        {
            if (q != 0)
            {
                if (u)
                {
                    if (u2 == 4)
                    {
                        sb.append((char)u1);
                        u = false;
                    }
                    else if (Character.isDigit(c))
                    {
                        u1 = u1 * 16 + (c - '0');
                    }
                    else if ((u2 < 4) && ((c >= 'a') && (c <= 'f')))
                    {
                        u1 = u1 * 16 + 10 + (c - 'a');
                    }
                    else if ((u2 < 4) && ((c >= 'A') && (c <= 'F')))
                    {
                        u1 = u1 * 16 + 10 + (c - 'A');
                    }
                    else
                    {
                        sb.append((char)u1);
                        u = false;
                    }
                    u2++;
                }

                if (b)
                {
                    switch (c)
                    {
                        case '"':
                            sb.append('"');
                            break;
                        case '\'':
                            sb.append('\'');
                            break;
                        case '\\':
                            sb.append('\\');
                            break;
                        case '0':
                            sb.append('\0');
                            break;
                        case 'b':
                            sb.append('\b');
                            break;
                        case 't':
                            sb.append('\t');
                            break;
                        case 'n':
                            sb.append('\n');
                            break;
                        case 'f':
                            sb.append('\f');
                            break;
                        case 'r':
                            sb.append('\r');
                            break;
                        case 'u':
                        case 'x':
                            u = true;
                            b = false;
                            u1 = 0;
                            u2 = 0;
                            break;
                    }
                }
                else
                {
                    if(c == q)
                        return sb.toString();
                    switch (c)
                    {
                        case '\\':
                            b = true;
                            break;
                        default:
                            sb.append(c);
                            break;
                    }
                }
            }
            else
            {
                switch (c)
                {
                    case '"':
                        q = '"';
                        break;
                    case '\'':
                        q = '\'';
                        break;
                    default:
                        sb.append(c);
                        break;
                }
            }
        }

        throw new ParserException(context, "Invalid string literal");
    }

    static String escapeString(String p)
    {
        StringBuilder sb = new StringBuilder();

        sb.append('"');
        for(char c : p.toCharArray())
        {
            if (!Character.isISOControl(c) && c != '"' && c != '\\')
            {
                sb.append(c);
                continue;
            }

            sb.append('\\');
            switch (c)
            {
                case '\b':
                    sb.append('b');
                    break;
                case '\t':
                    sb.append('t');
                    break;
                case '\n':
                    sb.append('n');
                    break;
                case '\f':
                    sb.append('f');
                    break;
                case '\r':
                    sb.append('r');
                    break;
                case '\"':
                    sb.append('\"');
                    break;
                case '\\':
                    sb.append('\\');
                    break;
                default:
                    sb.append(String.format("u%04x", (int)c));
                    break;
            }
        }
        sb.append('"');

        return sb.toString();
    }

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
            return escapeString(getString());
        }
        return String.format(Locale.ROOT, "%s", data.toString());
    }

    protected String toStringInternal(StringGenerationContext ctx)
    {
        return toStringInternal();
    }
}
