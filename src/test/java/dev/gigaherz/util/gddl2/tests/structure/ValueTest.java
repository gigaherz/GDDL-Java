package dev.gigaherz.util.gddl2.tests.structure;

import dev.gigaherz.util.gddl2.structure.GddlValue;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Callable;

import static org.junit.jupiter.api.Assertions.*;

public class ValueTest
{
    @Test
    public void equalsAndHashcodeWork()
    {
        GddlValue vNull = GddlValue.nullValue();
        GddlValue vSame1 = GddlValue.of(1);
        GddlValue vSame2 = GddlValue.of(1);
        GddlValue vDifferent = GddlValue.of("s");
        assertEquals(vSame1, vSame2);
        assertEquals(vSame2, vSame1);
        assertNotEquals(vSame1, vNull);
        assertNotEquals(vNull, vSame1);
        assertNotEquals(vSame1, vDifferent);
        assertNotEquals(vDifferent, vSame1);
        assertEquals(vSame1.hashCode(), vSame2.hashCode());
        // No I can't test that the hash codes are different for the other cases, because they don't need to be.
    }

    @Test
    public void nullValueWorks()
    {
        GddlValue v = GddlValue.nullValue();
        assertTrue(v.isNull());
        assertThrows(NullPointerException.class, v::booleanValue);
        assertThrows(NullPointerException.class, v::intValue);
        assertThrows(NullPointerException.class, v::doubleValue);
        assertThrows(NullPointerException.class, v::stringValue);
    }

    @Test
    public void ofBooleanTrueWorks()
    {
        GddlValue v = GddlValue.of(true);
        assertFalse(v.isNull());
        assertTrue(v.booleanValue());
        assertThrows(ClassCastException.class, v::intValue);
        assertThrows(ClassCastException.class, v::doubleValue);
        assertThrows(ClassCastException.class, v::stringValue);
    }

    @Test
    public void ofBooleanFalseWorks()
    {
        GddlValue v = GddlValue.of(false);
        assertFalse(v.isNull());
        assertFalse(v.booleanValue());
        assertThrows(ClassCastException.class, v::intValue);
        assertThrows(ClassCastException.class, v::doubleValue);
        assertThrows(ClassCastException.class, v::stringValue);
    }

    @Test
    public void ofLongWorks()
    {
        GddlValue v = GddlValue.of(1);
        assertFalse(v.isNull());
        assertEquals(1L, v.intValue());
        assertThrows(ClassCastException.class, v::booleanValue);
        assertThrows(ClassCastException.class, v::doubleValue);
        assertThrows(ClassCastException.class, v::stringValue);
    }

    @Test
    public void ofDoubleWorks()
    {
        GddlValue v = GddlValue.of(1.0);
        assertFalse(v.isNull());
        assertEquals(1L, v.doubleValue(), 1E-10);
        assertThrows(ClassCastException.class, v::booleanValue);
        assertThrows(ClassCastException.class, v::intValue);
        assertThrows(ClassCastException.class, v::stringValue);
    }

    @Test
    public void ofStringWorks()
    {
        GddlValue v = GddlValue.of("1");
        assertFalse(v.isNull());
        assertEquals("1", v.stringValue());
        assertThrows(ClassCastException.class, v::booleanValue);
        assertThrows(ClassCastException.class, v::intValue);
        assertThrows(ClassCastException.class, v::doubleValue);
    }

    @Test
    public void copyOfNullWorks()
    {
        GddlValue v = GddlValue.nullValue().copy();
        assertTrue(v.isNull());
    }

    @Test
    public void copyOfBooleanTrueWorks()
    {
        GddlValue v = GddlValue.of(true).copy();
        assertFalse(v.isNull());
        assertTrue(v.booleanValue());
    }

    @Test
    public void copyOfBooleanFalseWorks()
    {
        GddlValue v = GddlValue.of(false).copy();
        assertFalse(v.isNull());
        assertFalse(v.booleanValue());
    }

    @Test
    public void copyOfLongWorks()
    {
        GddlValue v = GddlValue.of(1).copy();
        assertFalse(v.isNull());
        assertEquals(1L, v.intValue());
    }

    @Test
    public void copyOfDoubleWorks()
    {
        GddlValue v = GddlValue.of(1.0).copy();
        assertFalse(v.isNull());
        assertEquals(1L, v.doubleValue(), 1E-10);
    }

    @Test
    public void copyOfStringWorks()
    {
        GddlValue v = GddlValue.of("1").copy();
        assertFalse(v.isNull());
        assertEquals("1", v.stringValue());
    }

    public static <T extends Throwable, V> void assertThrows(Class<T> expectedThrowable,
                                                             Callable<V> callable)
    {
        Assertions.assertThrows(expectedThrowable, () -> {
            var v = callable.call();
            fail("Expected callable to throw, but it returned " + v);
        });
    }
}
