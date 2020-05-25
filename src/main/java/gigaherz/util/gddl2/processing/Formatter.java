package gigaherz.util.gddl2.processing;

import gigaherz.util.gddl2.Lexer;
import gigaherz.util.gddl2.config.StringGenerationOptions;
import gigaherz.util.gddl2.structure.Collection;
import gigaherz.util.gddl2.structure.Element;
import gigaherz.util.gddl2.structure.Reference;
import gigaherz.util.gddl2.structure.Value;
import gigaherz.util.gddl2.util.BasicIntStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Formatter
{
    public static String formatCompact(Element e)
    {
        return format(e, StringGenerationOptions.COMPACT);
    }

    public static String formatNice(Element e)
    {
        return format(e, StringGenerationOptions.NICE);
    }

    public static String format(Element e, StringGenerationOptions options)
    {
        StringBuilder b = new StringBuilder();
        Formatter f = new Formatter(b, options);
        f.formatStandalone(e);
        return b.toString();
    }

    private final BasicIntStack indentLevels = new BasicIntStack();
    private final StringGenerationOptions options;
    private final StringBuilder builder;

    public int indentLevel = 0;

    public Formatter(StringBuilder builder, StringGenerationOptions options)
    {
        this.builder = builder;
        this.options = options;
    }

    private void pushIndent()
    {
        indentLevels.push(indentLevel);
    }

    private void popIndent()
    {
        indentLevel = indentLevels.pop();
    }

    private void setIndent(int newIndent)
    {
        indentLevel = newIndent;
    }

    private void incIndent()
    {
        indentLevel++;
    }

    private void appendMultiple(String s, int n)
    {
        for (int i = 0; i < n; i++)
        {
            builder.append(s);
        }
    }

    private void appendIndent()
    {
        int tabsToGen = indentLevel;
        for (int i = 0; i < tabsToGen; i++)
        {
            if (options.indentUsingTabs)
            {
                builder.append("\t");
            }
            else
            {
                for (int j = 0; j < options.spacesPerIndent; j++)
                {
                    builder.append(" ");
                }
            }
        }
    }

    public void formatStandalone(Element e)
    {
        formatComment(e);
        formatName(e);
        formatElement(e, false);
    }

    public void formatComment(Element e)
    {
        if (e.hasComment() && options.writeComments)
        {
            for(String s : e.getComment().split("(?:(?:\n)|(?:\r\n))"))
            {
                appendIndent();
                builder.append("#");
                builder.append(s);
                builder.append("\n");
            }
        }
    }

    protected void formatName(Element e)
    {
        appendIndent();
        if (e.hasName())
        {
            String sname = e.getName();
            if (!Lexer.isValidIdentifier(sname))
                sname = Lexer.escapeString(sname);
            builder.append(sname);
            builder.append(" = ");
        }
    }

    protected void formatElement(Element e, boolean hasNext)
    {
        if (e instanceof Value)
        {
            formatValue((Value)e);
        }
        else if (e instanceof Reference)
        {
            formatReference((Reference)e);
        }
        else if (e instanceof Collection)
        {
            formatCollection((Collection)e, hasNext);
        }
        else
        {
            throw new IllegalStateException("A new Element type has been added without updating Formatter#formatElement.");
        }
    }

    protected void formatValue(Value v)
    {
        if (v.isNull())
        {
            builder.append("null");
        }
        else if (v.isBoolean())
        {
            builder.append(v.getBoolean() ? "true" : "false");
        }
        else if (v.isInteger())
        {
            formatInteger(v.getInteger());
        }
        else if(v.isDouble())
        {
            formatDoubleCustom(v.getDouble());
        }
        else if (v.isString())
        {
            builder.append(Lexer.escapeString(v.getString()));
        }
        else
        {
            throw new IllegalStateException("A new Value type has been added without updating Formatter#formatValue.");
        }
    }

    private void formatInteger(long exp2)
    {
        builder.append(String.format(Locale.ROOT, "%d", exp2));
    }

    private void formatDoubleCustom(double value)
    {
        switch (options.floatFormattingStyle)
        {
            case DECIMAL:
                formatDoubleDecimal(value);
                break;
            case SCIENTIFIC:
                formatDoubleScientific(value);
                break;
            default:
                formatDoubleShortest(value);
                break;
        }
    }

    private void formatDoubleShortest(double value)
    {
        if (formatSpecial(value))
            return;

        int exp = (int) Math.floor(Math.log10(value));
        if (exp >= 5 || exp < -2)
        {
            formatDoubleScientific(value);
        }
        else
        {
            formatDoubleDecimal(value);
        }
    }

    private void formatDoubleScientific(double value)
    {
        if (formatSpecial(value))
            return;

        int exp = (int) Math.floor(Math.log10(value));
        double adjusted = value / Math.pow(10, exp);
        formatDoubleDecimal(adjusted);
        builder.append("e");
        formatSign(exp);
        formatInteger(Math.abs(exp));
    }

    private void formatDoubleDecimal(double value)
    {
        formatNegative(value);
        value = Math.abs(value);
        double integral = Math.floor(value);
        double fractional = value - integral;
        List<Integer> temp = new ArrayList<>();
        while(integral > 0)
        {
            double digit = integral % 10;
            integral = Math.floor(integral / 10);
            temp.add((int) digit);
        }
        for(int i=temp.size()-1;i>=0;i--)
        {
            builder.append((char)('0'+temp.get(i)));
        }
        if (temp.size() == 0) builder.append('0');
        builder.append(".");
        int digits = 0;
        value = fractional;
        temp.clear();
        while(value > 0 && digits <= 13)
        {
            value *= 10;
            int digit = (int)Math.floor(value);
            value -= digit;
            temp.add(digit);
            digits++;
        }
        if (digits == 0)
        {
            builder.append("0");
        }
        else
        {
            int l = temp.size()-1;
            int r = value >= 0.5 ? 1 : 0;
            while (r > 0 && l >= 0) // round up
            {
                int v = temp.get(l);
                v++;
                if (v >= 10)
                {
                    r = 1;
                    v -= 10;
                }
                else
                {
                    r = 0;
                }
                temp.set(l, v);
                l--;
            }
        }
        int firstTrailingZero=temp.size();
        for(int i=temp.size()-1;i>=0;i--)
        {
            if (temp.get(i) != 0)
            {
                firstTrailingZero = i + 1;
                break;
            }
        }
        for(int i=0;i<firstTrailingZero;i++)
        {
            builder.append((char)('0'+temp.get(i)));
        }
    }

    private boolean formatSpecial(double value)
    {
        if (Double.isNaN(value))
        {
            builder.append(".NaN");
            return true;
        }
        else if (Double.isInfinite(value))
        {
            formatNegative(value);
            builder.append(".Inf");
            return true;
        }
        else
        {
            return false;
        }
    }

    private void formatNegative(double value)
    {
        long l = Double.doubleToRawLongBits(value);
        if (l < 0) builder.append("-");
    }

    private void formatSign(double value)
    {
        long l = Double.doubleToRawLongBits(value);
        builder.append(l < 0 ? "-" : "+");
    }

    protected void formatReference(Reference r)
    {
        int count = 0;
        for (String it : r.getNameParts())
        {
            if (count++ > 0)
                builder.append(':');
            builder.append(it);
        }

        if (r.isResolved())
        {
            builder.append('=');
            if (r.resolvedValue() == null)
                builder.append("NULL");
            else
                builder.append(r.resolvedValue());
        }
    }

    protected void formatCollection(Collection c, boolean hasNext0)
    {
        pushIndent();

        boolean oneElementPerLine = !c.isSimple() || c.size() > options.oneElementPerLineThreshold;

        if (c.hasTypeName())
        {
            builder.append(c.getTypeName());
            if (options.lineBreaksBeforeOpeningBrace == 0)
                builder.append(" ");
        }
        boolean addBraces = indentLevel > 0 || c.hasTypeName();
        if (addBraces)
        {
            if (oneElementPerLine && options.lineBreaksBeforeOpeningBrace > 0)
            {
                appendMultiple("\n", options.lineBreaksBeforeOpeningBrace);
                appendIndent();
            }
            else if (options.spacesBeforeOpeningBrace > 0)
            {
                appendMultiple(" ", options.spacesBeforeOpeningBrace);
            }
            builder.append("{");
            if (oneElementPerLine && options.lineBreaksAfterOpeningBrace > 0)
            {
                appendMultiple("\n", options.lineBreaksAfterOpeningBrace);
            }
            else if (options.spacesAfterOpeningBrace > 0)
            {
                appendMultiple(" ", options.spacesAfterOpeningBrace);
            }
            pushIndent();
            incIndent();
        }

        boolean first = true;
        for (int i = 0; i < c.size(); i++)
        {
            Element e = c.get(i);
            pushIndent();

            if (first && (!oneElementPerLine || options.lineBreaksAfterOpeningBrace == 0))
            {
                setIndent(0);
            }
            else if (!first)
            {
                if (oneElementPerLine)
                {
                    builder.append("\n");
                }
                else if (options.spacesBetweenElements > 0)
                {
                    appendMultiple(" ", options.spacesBetweenElements);
                }

                if (!oneElementPerLine)
                    setIndent(0);
            }

            boolean hasNext1 = (i+1) < c.size();
            formatComment(e);
            formatName(e);
            formatElement(e, hasNext1);
            if (hasNext1 && (!e.isCollection() || !options.omitCommaAfterClosingBrace)) builder.append(",");

            first = false;
            popIndent();
        }

        if (addBraces)
        {
            popIndent();
            if (oneElementPerLine && options.lineBreaksBeforeClosingBrace > 0)
            {
                appendMultiple("\n", options.lineBreaksBeforeClosingBrace);
                appendIndent();
            }
            else if (options.spacesBeforeClosingBrace > 0)
            {
                appendMultiple(" ", options.spacesBeforeClosingBrace);
            }
            builder.append("}");
            if (!hasNext0 || options.omitCommaAfterClosingBrace)
            {
                if (oneElementPerLine && options.lineBreaksAfterClosingBrace > 0)
                {
                    appendMultiple("\n", options.lineBreaksAfterClosingBrace);
                }
                else if (options.spacesAfterClosingBrace > 0)
                {
                    appendMultiple(" ", options.spacesAfterClosingBrace);
                }
            }
        }

        popIndent();
    }
}
