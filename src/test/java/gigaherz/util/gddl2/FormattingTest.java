package gigaherz.util.gddl2;

import gigaherz.util.gddl2.serialization.Formatter;
import gigaherz.util.gddl2.serialization.FormatterOptions;
import gigaherz.util.gddl2.structure.Element;
import gigaherz.util.gddl2.structure.Value;
import org.junit.Test;

import static org.junit.Assert.*;

public class FormattingTest
{
    @Test
    public void testValuesCompact()
    {
        assertEquals("null", formatOneCompact(Value.nullValue()));
        assertEquals("false", formatOneCompact(Value.of(false)));
        assertEquals("true", formatOneCompact(Value.of(true)));
        assertEquals("1", formatOneCompact(Value.of(1)));
        assertEquals("1.0", formatOneCompact(Value.of(1.0)));
        assertEquals("\"1\"", formatOneCompact(Value.of("1")));
    }

    @Test
    public void testFloatsCompact()
    {
        assertEquals("1.0", formatOneCompact(Value.of(1.0)));
        assertEquals("1.01", formatOneCompact(Value.of(1.01)));
        assertEquals("1.000000000001", formatOneCompact(Value.of(1.000000000001)));
        assertEquals("0.1", formatOneCompact(Value.of(0.1)));
        assertEquals("1.0e-8", formatOneCompact(Value.of(0.00000001)));
        assertEquals("1.0e10", formatOneCompact(Value.of(10000000000.0)));
        assertEquals("3.0e-50", formatOneCompact(Value.of(3e-50)));
        assertEquals("1.999999999999999e15", formatOneCompact(Value.of(1999999999999999.0)));
        assertEquals("2.0e32", formatOneCompact(Value.of(199999999999999999999999999999999.0)));
    }

    @Test
    public void testStringsCompact()
    {
        assertEquals("\"1\"", formatOneCompact(Value.of("1")));
    }

    public static String formatOneCompact(Element e)
    {
        StringBuilder b = new StringBuilder();
        new Formatter(b, FormatterOptions.COMPACT).formatStandalone(e);
        return b.toString();
    }
}