package dev.gigaherz.util.gddl2;

import dev.gigaherz.util.gddl2.serialization.FormatterOptions;
import dev.gigaherz.util.gddl2.serialization.Formatter;
import dev.gigaherz.util.gddl2.structure.GddlElement;
import dev.gigaherz.util.gddl2.structure.GddlList;
import dev.gigaherz.util.gddl2.structure.GddlMap;
import dev.gigaherz.util.gddl2.structure.GddlValue;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FormattingTest
{
    @Test
    public void testValuesCompact()
    {
        assertEquals("null", formatOneCompact(GddlValue.nullValue()));
        assertEquals("false", formatOneCompact(GddlValue.of(false)));
        assertEquals("true", formatOneCompact(GddlValue.of(true)));
        assertEquals("1", formatOneCompact(GddlValue.of(1)));
        assertEquals("1.0", formatOneCompact(GddlValue.of(1.0)));
        assertEquals("\"1\"", formatOneCompact(GddlValue.of("1")));
    }

    @Test
    public void testIntegersCompact()
    {
        assertEquals("0", formatOneCompact(GddlValue.of(0)));
        assertEquals("1", formatOneCompact(GddlValue.of(1)));
        assertEquals("10", formatOneCompact(GddlValue.of(10)));
        assertEquals("100", formatOneCompact(GddlValue.of(100)));
        assertEquals("1000000000000000000", formatOneCompact(GddlValue.of(1000000000000000000L)));
        assertEquals("9223372036854775807", formatOneCompact(GddlValue.of(Long.MAX_VALUE)));
        assertEquals("-1", formatOneCompact(GddlValue.of(-1)));
        assertEquals("-10", formatOneCompact(GddlValue.of(-10)));
        assertEquals("-100", formatOneCompact(GddlValue.of(-100)));
        assertEquals("-1000000000000000000", formatOneCompact(GddlValue.of(-1000000000000000000L)));
        assertEquals("-9223372036854775808", formatOneCompact(GddlValue.of(Long.MIN_VALUE)));
    }

    @Test
    public void testFloatsCompact()
    {
        assertEquals("1.0", formatOneCompact(GddlValue.of(1.0)));
        assertEquals("1.01", formatOneCompact(GddlValue.of(1.01)));
        assertEquals("1.000000000001", formatOneCompact(GddlValue.of(1.000000000001)));
        assertEquals("0.1", formatOneCompact(GddlValue.of(0.1)));
        assertEquals("1.0e-8", formatOneCompact(GddlValue.of(0.00000001)));
        assertEquals("1.0e10", formatOneCompact(GddlValue.of(10000000000.0)));
        assertEquals("3.0e-50", formatOneCompact(GddlValue.of(3e-50)));
        assertEquals("1.0e18", formatOneCompact(GddlValue.of(1000000000000000000.0)));
        assertEquals("1.999999999999999e15", formatOneCompact(GddlValue.of(1999999999999999.0)));
        assertEquals("2.0e32", formatOneCompact(GddlValue.of(199999999999999999999999999999999.0)));
        assertEquals(".NaN", formatOneCompact(GddlValue.of(Float.NaN)));
        assertEquals(".Inf", formatOneCompact(GddlValue.of(Float.POSITIVE_INFINITY)));
        assertEquals("-.Inf", formatOneCompact(GddlValue.of(Float.NEGATIVE_INFINITY)));
    }

    @Test
    public void testStringsCompact()
    {
        assertEquals("\"1\"", formatOneCompact(GddlValue.of("1")));
    }

    @Test
    public void testListsCompact()
    {
        assertEquals("[]", formatOneCompact(GddlList.empty()));
        assertEquals("[[]]", formatOneCompact(GddlList.of(GddlList.empty())));
        assertEquals("[[],[]]", formatOneCompact(GddlList.of(GddlList.empty(),GddlList.empty())));
        assertEquals("[[],{}]", formatOneCompact(GddlList.of(GddlList.empty(), GddlMap.empty())));
        assertEquals("[1]", formatOneCompact(GddlList.of(GddlValue.of(1))));
        assertEquals("[\"1\"]", formatOneCompact(GddlList.of(GddlValue.of("1"))));
        assertEquals("[null]", formatOneCompact(GddlList.of(GddlValue.nullValue())));
    }

    @Test
    public void testMapsCompact()
    {
        assertEquals("{}", formatOneCompact(GddlMap.empty()));
        assertEquals("{a=[]}", formatOneCompact(GddlMap.of("a",GddlList.empty())));
        assertEquals("{a=[],b=[]}", formatOneCompact(GddlMap.of("a",GddlList.empty(), "b", GddlList.empty())));
        assertEquals("{a=[],b={}}", formatOneCompact(GddlMap.of("a",GddlList.empty(), "b", GddlMap.empty())));
        assertEquals("{a=1}", formatOneCompact(GddlMap.of("a",GddlValue.of(1))));
        assertEquals("{a=\"1\"}", formatOneCompact(GddlMap.of("a",GddlValue.of("1"))));
        assertEquals("{a=null}", formatOneCompact(GddlMap.of("a",GddlValue.nullValue())));
        assertEquals("{\"a b\"=1}", formatOneCompact(GddlMap.of("a b",GddlValue.of(1))));
    }

    public static String formatOneCompact(GddlElement<?> e)
    {
        StringBuilder b = new StringBuilder();
        new Formatter(b, FormatterOptions.COMPACT).formatElement(e);
        return b.toString();
    }
}
