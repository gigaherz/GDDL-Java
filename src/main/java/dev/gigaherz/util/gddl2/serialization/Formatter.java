package dev.gigaherz.util.gddl2.serialization;

import dev.gigaherz.util.gddl2.structure.*;
import dev.gigaherz.util.gddl2.util.BasicIntStack;
import dev.gigaherz.util.gddl2.util.Utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class Formatter
{
    //region API
    public static String formatCompact(GddlDocument doc)
    {
        return format(doc, FormatterOptions.COMPACT);
    }

    public static String formatCompact(GddlElement<?> element)
    {
        return format(element, FormatterOptions.COMPACT);
    }

    public static String formatNice(GddlDocument doc)
    {
        return format(doc, FormatterOptions.NICE);
    }

    public static String formatNice(GddlElement<?> element)
    {
        return format(element, FormatterOptions.NICE);
    }

    public static String format(GddlDocument doc, FormatterOptions options)
    {
        StringBuilder b = new StringBuilder();
        Formatter f = new Formatter(b, options);
        f.formatDocument(doc);
        return b.toString();
    }

    public static String format(GddlElement<?> element, FormatterOptions options)
    {
        StringBuilder b = new StringBuilder();
        Formatter f = new Formatter(b, options);
        f.formatElement(element);
        return b.toString();
    }

    public Formatter(StringBuilder builder, FormatterOptions options)
    {
        this.builder = builder;
        this.options = options;
    }

    public void formatDocument(GddlDocument d)
    {
        formatElement(d.getRoot());

        if (d.hasDanglingComment() && options.writeComments)
            formatComment(d.getDanglingComment());
    }

    public void formatElement(GddlElement<?> e)
    {
        formatComment(e);
        appendIndent();
        formatElement(e, false);
    }
    //endregion 

    //region Implementation
    private static final Pattern COMMENT_LINE_SPLITTER = Pattern.compile("\n|\r\n");

    private final BasicIntStack indentLevels = new BasicIntStack();
    private final FormatterOptions options;
    private final StringBuilder builder;

    private int indentLevel = 0;

    private void pushIndent()
    {
        indentLevels.push(indentLevel);
    }

    private void popIndent()
    {
        indentLevel = indentLevels.pop();
    }

    private void clearIndent()
    {
        indentLevel = 0;
    }

    private void incIndent()
    {
        indentLevel++;
    }

    private void appendMultiple(char c, int n)
    {
        //noinspection StringRepeatCanBeUsed
        for (int i = 0; i < n; i++)
            builder.append(c);
    }

    private void appendIndent()
    {
        int tabsToGen = indentLevel;
        for (int i = 0; i < tabsToGen; i++)
        {
            if (options.indentUsingTabs)
            {
                builder.append('\t');
            }
            else
            {
                appendMultiple(' ', options.spacesPerIndent);
            }
        }
    }

    private void formatComment(GddlElement<?> e)
    {
        if (e.hasComment() && options.writeComments)
        {
            formatComment(e.getComment());
        }
    }

    private void formatComment(String comment)
    {
        appendMultiple('\n', options.blankLinesBeforeComment);
        String[] lines = COMMENT_LINE_SPLITTER.split(comment, -1);
        int count = lines.length;
        if (count > 0 && options.trimCommentLines)
        {
            while (count > 0 && lines[count - 1].length() == 0)
                count--;
        }
        for (int i = 0; i < count; i++)
        {
            appendIndent();
            builder.append('#');
            builder.append(lines[i]);
            builder.append('\n');
        }
    }

    private void formatElement(GddlElement<?> e, boolean hasNext)
    {
        if (e.isValue())
        {
            formatValue(e.asValue());
        }
        else if (e.isReference())
        {
            formatReference(e.asReference());
        }
        else if (e.isMap())
        {
            formatMap(e.asMap(), hasNext);
        }
        else if (e.isList())
        {
            formatList(e.asList(), hasNext);
        }
        else
        {
            throw new IllegalStateException("A new Element type has been added without updating Formatter#formatElement.");
        }
    }

    private void formatValue(GddlValue v)
    {
        if (v.isNull())
        {
            builder.append("null");
        }
        else if (v.isBoolean())
        {
            builder.append(v.booleanValue() ? "true" : "false");
        }
        else if (v.isInteger())
        {
            formatInteger(v.intValue());
        }
        else if (v.isDouble())
        {
            formatDoubleCustom(v.doubleValue());
        }
        else if (v.isString())
        {
            builder.append(Utility.escapeString(v.stringValue()));
        }
        else
        {
            throw new IllegalStateException("A new Value type has been added without updating Formatter#formatValue.");
        }
    }

    private void formatInteger(long value)
    {
        builder.append(String.format(Locale.ROOT, "%d", value));
    }

    private void formatDoubleCustom(double value)
    {
        switch (options.floatFormattingStyle)
        {
            case DECIMAL -> formatDoubleDecimal(value);
            case SCIENTIFIC -> formatDoubleScientific(value);
            default -> formatDoubleAuto(value);
        }
    }

    private void formatDoubleAuto(double value)
    {
        if (formatSpecial(value))
            return;

        int exp = (int) Math.floor(Math.log10(Math.abs(value)));
        if (exp >= options.autoScientificNotationUpper || exp < options.autoScientificNotationLower)
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

        int exp = (int) Math.floor(Math.log10(Math.abs(value)));
        double adjusted = value / Math.pow(10, exp);
        formatDoubleDecimal(adjusted);
        builder.append('e');
        if (options.alwaysShowExponentSign)
            formatSign(exp);
        else
            formatNegative(exp);
        formatInteger(Math.abs(exp));
    }

    private void formatDoubleDecimal(double value)
    {
        if (options.alwaysShowNumberSign)
            formatSign(value);
        else
            formatNegative(value);
        value = Math.abs(value);

        double integral = Math.floor(value);
        double fractional = value - integral;

        List<Integer> temp = new ArrayList<>();

        int intDigits = formatIntegral(integral, temp);

        builder.append('.');

        formatFractional(fractional, intDigits, temp);
    }

    private int formatIntegral(double integral, List<Integer> temp)
    {
        if (!(integral > 0))
        {
            builder.append('0');
            return 0;
        }

        int exp = (int) Math.ceil(Math.log10(integral));
        double value = integral / Math.pow(10, exp);

        int nonTrailingDigits = formatDigits(temp, Math.min(exp, options.floatSignificantFigures), value);

        appendMultiple('0', exp - nonTrailingDigits);
        return exp;
    }

    private void formatFractional(double fractional, int intDigits, List<Integer> temp)
    {
        formatDigits(temp, (options.floatSignificantFigures - intDigits), fractional);
    }

    private int formatDigits(List<Integer> temp, int exp, double value)
    {
        temp.clear();
        while (value > 0 && temp.size() < exp)
        {
            value *= 10;
            int digit = (int) Math.floor(value);
            value -= digit;
            temp.add(digit);
        }
        if (temp.size() == 0)
        {
            temp.add(0);
        }
        int nonTrailingDigits = roundDigits(temp, value);
        for (int i = 0; i < nonTrailingDigits; i++)
        {
            builder.append((char) ('0' + temp.get(i)));
        }
        return nonTrailingDigits;
    }

    private int roundDigits(List<Integer> temp, double value)
    {
        int l = temp.size() - 1;
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
        int firstTrailingZero = temp.size();
        for (int i = temp.size() - 1; i >= 0; i--)
        {
            if (temp.get(i) != 0)
            {
                firstTrailingZero = i + 1;
                break;
            }
        }
        return firstTrailingZero;
    }

    private boolean formatSpecial(double value)
    {
        if (Double.isNaN(value))
        {
            builder.append(".NaN");
            return true;
        }

        if (Double.isInfinite(value))
        {
            if (options.alwaysShowNumberSign)
                formatSign(value);
            else
                formatNegative(value);
            builder.append(".Inf");
            return true;
        }

        return false;
    }

    private void formatNegative(double value)
    {
        long l = Double.doubleToRawLongBits(value);
        if (l < 0) builder.append('-');
    }

    private void formatSign(double value)
    {
        long l = Double.doubleToRawLongBits(value);
        builder.append(l < 0 ? "-" : "+");
    }

    private void formatReference(GddlReference r)
    {
        int count = 0;
        for (String it : r.getNameParts())
        {
            if (count++ > 0)
                builder.append(':');
            builder.append(it);
        }

        /*if (r.isResolved())
        {
            builder.append('=');
            if (r.resolvedValue() == null)
                builder.append("NULL");
            else
                builder.append(r.resolvedValue());
        }*/
    }

    private void formatMap(GddlMap c, boolean hasNext0)
    {
        pushIndent();

        boolean oneElementPerLine = c.getFormattingComplexity() > options.oneElementPerLineThreshold;

        if (c.hasTypeName())
        {
            builder.append(c.getTypeName());
            if (options.lineBreaksBeforeOpeningBrace == 0)
                builder.append(' ');
        }
        if (oneElementPerLine && options.lineBreaksBeforeOpeningBrace > 0)
        {
            appendMultiple('\n', options.lineBreaksBeforeOpeningBrace);
            appendIndent();
        }
        else if (options.spacesBeforeOpeningBrace > 0)
        {
            appendMultiple(' ', options.spacesBeforeOpeningBrace);
        }
        builder.append('{');
        if (c.size() == 0 && !oneElementPerLine)
        {
            appendMultiple(' ', options.spacesInEmptyCollection);
        }
        else if (oneElementPerLine && options.lineBreaksAfterOpeningBrace > 0)
        {
            appendMultiple('\n', options.lineBreaksAfterOpeningBrace);
        }
        else if (options.spacesAfterOpeningBrace > 0)
        {
            appendMultiple(' ', options.spacesAfterOpeningBrace);
        }
        pushIndent();
        incIndent();

        boolean first = true;
        List<String> keys = new ArrayList<>(c.keySet());
        keys.sort(String::compareTo);
        for (int i = 0; i < keys.size(); i++)
        {
            String key = keys.get(i);
            final GddlElement<?> e = c.get(key);
            pushIndent();

            if (first && (!oneElementPerLine || options.lineBreaksAfterOpeningBrace == 0))
            {
                clearIndent();
            }
            else if (!first)
            {
                if (oneElementPerLine)
                {
                    builder.append("\n");
                }
                else
                {
                    appendMultiple(' ', options.spacesAfterComma);
                }

                if (!oneElementPerLine)
                    clearIndent();
            }

            boolean hasNext1 = (i + 1) < c.size();

            formatComment(e);
            appendIndent();
            if (!Utility.isValidIdentifier(key))
                key = Utility.escapeString(key);
            builder.append(key);
            appendMultiple(' ',options.spacesBeforeEquals);
            builder.append('=');
            appendMultiple(' ',options.spacesAfterEquals);
            formatElement(e, hasNext1);

            if (hasNext1 && ((!e.isCollection()) || !options.omitCommaAfterClosingBrace))
            {
                appendMultiple(' ', options.spacesBeforeComma);
                builder.append(',');
            }

            first = false;
            popIndent();
        }

        if (c.hasTrailingComment() && options.writeComments)
            formatComment(c.getTrailingComment());

        popIndent();
        if (c.size() == 0 && !oneElementPerLine)
        {
            // Done on the open side
        }
        else if (oneElementPerLine && options.lineBreaksBeforeClosingBrace > 0)
        {
            appendMultiple('\n', options.lineBreaksBeforeClosingBrace);
            appendIndent();
        }
        else if (options.spacesBeforeClosingBrace > 0)
        {
            appendMultiple(' ', options.spacesBeforeClosingBrace);
        }
        builder.append('}');
        if (!hasNext0 || options.omitCommaAfterClosingBrace)
        {
            if (oneElementPerLine && options.lineBreaksAfterClosingBrace > 0)
            {
                appendMultiple('\n', options.lineBreaksAfterClosingBrace);
            }
            else if (options.spacesAfterClosingBrace > 0)
            {
                appendMultiple(' ', options.spacesAfterClosingBrace);
            }
        }

        popIndent();
    }

    private void formatList(GddlList c, boolean hasNext0)
    {
        pushIndent();

        boolean oneElementPerLine = c.getFormattingComplexity() > options.oneElementPerLineThreshold;

        if (oneElementPerLine && options.lineBreaksBeforeOpeningBrace > 0)
        {
            appendMultiple('\n', options.lineBreaksBeforeOpeningBrace);
            appendIndent();
        }
        else if (options.spacesBeforeOpeningBrace > 0)
        {
            appendMultiple(' ', options.spacesBeforeOpeningBrace);
        }
        builder.append('[');
        if (oneElementPerLine && options.lineBreaksAfterOpeningBrace > 0)
        {
            appendMultiple('\n', options.lineBreaksAfterOpeningBrace);
        }
        else if (options.spacesAfterOpeningBrace > 0)
        {
            appendMultiple(' ', options.spacesAfterOpeningBrace);
        }
        pushIndent();
        incIndent();

        boolean first = true;
        for (int i = 0; i < c.size(); i++)
        {
            final GddlElement<?> e = c.get(i);
            pushIndent();

            if (first && (!oneElementPerLine || options.lineBreaksAfterOpeningBrace == 0))
            {
                clearIndent();
            }
            else if (!first)
            {
                if (oneElementPerLine)
                {
                    builder.append("\n");
                }
                else if (options.spacesAfterComma > 0)
                {
                    appendMultiple(' ', options.spacesAfterComma);
                }

                if (!oneElementPerLine)
                    clearIndent();
            }

            boolean hasNext1 = (i + 1) < c.size();

            formatComment(e);
            appendIndent();
            formatElement(e, hasNext1);

            if (hasNext1 && ((!e.isMap() && !e.isList()) || !options.omitCommaAfterClosingBrace))
            {
                appendMultiple(' ', options.spacesBeforeComma);
                builder.append(',');
            }

            first = false;
            popIndent();
        }

        if (c.hasTrailingComment() && options.writeComments)
            formatComment(c.getTrailingComment());

        popIndent();
        if (oneElementPerLine && options.lineBreaksBeforeClosingBrace > 0)
        {
            appendMultiple('\n', options.lineBreaksBeforeClosingBrace);
            appendIndent();
        }
        else if (options.spacesBeforeClosingBrace > 0)
        {
            appendMultiple(' ', options.spacesBeforeClosingBrace);
        }
        builder.append(']');
        if (!hasNext0 || options.omitCommaAfterClosingBrace)
        {
            if (oneElementPerLine && options.lineBreaksAfterClosingBrace > 0)
            {
                appendMultiple('\n', options.lineBreaksAfterClosingBrace);
            }
            else if (options.spacesAfterClosingBrace > 0)
            {
                appendMultiple(' ', options.spacesAfterClosingBrace);
            }
        }

        popIndent();
    }
    //endregion
}
