package dev.gigaherz.util.gddl2.structure;

import org.junit.Test;

import static org.junit.Assert.*;

public class ValueTest
{
    @Test
    public void equalsAndHashcodeWork()
    {
        Value vNull = Value.nullValue();
        Value vSame1 = Value.of(1);
        Value vSame2 = Value.of(1);
        Value vDifferent = Value.of("s");
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
        Value v = Value.nullValue();
        assertTrue(v.isNull());
        assertThrows(NullPointerException.class, v::asBoolean);
        assertThrows(NullPointerException.class, v::asInteger);
        assertThrows(NullPointerException.class, v::asDouble);
        assertThrows(NullPointerException.class, v::asString);
    }

    @Test
    public void ofBooleanTrueWorks()
    {
        Value v = Value.of(true);
        assertFalse(v.isNull());
        assertTrue(v.asBoolean());
        assertThrows(ClassCastException.class, v::asInteger);
        assertThrows(ClassCastException.class, v::asDouble);
        assertThrows(ClassCastException.class, v::asString);
    }

    @Test
    public void ofBooleanFalseWorks()
    {
        Value v = Value.of(false);
        assertFalse(v.isNull());
        assertFalse(v.asBoolean());
        assertThrows(ClassCastException.class, v::asInteger);
        assertThrows(ClassCastException.class, v::asDouble);
        assertThrows(ClassCastException.class, v::asString);
    }

    @Test
    public void ofLongWorks()
    {
        Value v = Value.of(1);
        assertFalse(v.isNull());
        assertEquals(1L, v.asInteger());
        assertThrows(ClassCastException.class, v::asBoolean);
        assertThrows(ClassCastException.class, v::asDouble);
        assertThrows(ClassCastException.class, v::asString);
    }

    @Test
    public void ofDoubleWorks()
    {
        Value v = Value.of(1.0);
        assertFalse(v.isNull());
        assertEquals(1L, v.asDouble(), 1E-10);
        assertThrows(ClassCastException.class, v::asBoolean);
        assertThrows(ClassCastException.class, v::asInteger);
        assertThrows(ClassCastException.class, v::asString);
    }

    @Test
    public void ofStringWorks()
    {
        Value v = Value.of("1");
        assertFalse(v.isNull());
        assertEquals("1", v.asString());
        assertThrows(ClassCastException.class, v::asBoolean);
        assertThrows(ClassCastException.class, v::asInteger);
        assertThrows(ClassCastException.class, v::asDouble);
    }

    @Test
    public void copyOfNullWorks()
    {
        Value v = Value.nullValue().copy();
        assertTrue(v.isNull());
    }

    @Test
    public void copyOfBooleanTrueWorks()
    {
        Value v = Value.of(true).copy();
        assertFalse(v.isNull());
        assertTrue(v.asBoolean());
    }

    @Test
    public void copyOfBooleanFalseWorks()
    {
        Value v = Value.of(false).copy();
        assertFalse(v.isNull());
        assertFalse(v.asBoolean());
    }

    @Test
    public void copyOfLongWorks()
    {
        Value v = Value.of(1).copy();
        assertFalse(v.isNull());
        assertEquals(1L, v.asInteger());
    }

    @Test
    public void copyOfDoubleWorks()
    {
        Value v = Value.of(1.0).copy();
        assertFalse(v.isNull());
        assertEquals(1L, v.asDouble(), 1E-10);
    }

    @Test
    public void copyOfStringWorks()
    {
        Value v = Value.of("1").copy();
        assertFalse(v.isNull());
        assertEquals("1", v.asString());
    }
}
