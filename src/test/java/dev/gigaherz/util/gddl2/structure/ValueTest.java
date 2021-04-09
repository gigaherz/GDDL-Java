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
        assertThrows(NullPointerException.class, v::getBoolean);
        assertThrows(NullPointerException.class, v::getInteger);
        assertThrows(NullPointerException.class, v::getDouble);
        assertThrows(NullPointerException.class, v::getString);
    }

    @Test
    public void ofBooleanTrueWorks()
    {
        Value v = Value.of(true);
        assertFalse(v.isNull());
        assertTrue(v.getBoolean());
        assertThrows(ClassCastException.class, v::getInteger);
        assertThrows(ClassCastException.class, v::getDouble);
        assertThrows(ClassCastException.class, v::getString);
    }

    @Test
    public void ofBooleanFalseWorks()
    {
        Value v = Value.of(false);
        assertFalse(v.isNull());
        assertFalse(v.getBoolean());
        assertThrows(ClassCastException.class, v::getInteger);
        assertThrows(ClassCastException.class, v::getDouble);
        assertThrows(ClassCastException.class, v::getString);
    }

    @Test
    public void ofLongWorks()
    {
        Value v = Value.of(1);
        assertFalse(v.isNull());
        assertEquals(1L, v.getInteger());
        assertThrows(ClassCastException.class, v::getBoolean);
        assertThrows(ClassCastException.class, v::getDouble);
        assertThrows(ClassCastException.class, v::getString);
    }

    @Test
    public void ofDoubleWorks()
    {
        Value v = Value.of(1.0);
        assertFalse(v.isNull());
        assertEquals(1L, v.getDouble(), 1E-10);
        assertThrows(ClassCastException.class, v::getBoolean);
        assertThrows(ClassCastException.class, v::getInteger);
        assertThrows(ClassCastException.class, v::getString);
    }

    @Test
    public void ofStringWorks()
    {
        Value v = Value.of("1");
        assertFalse(v.isNull());
        assertEquals("1", v.getString());
        assertThrows(ClassCastException.class, v::getBoolean);
        assertThrows(ClassCastException.class, v::getInteger);
        assertThrows(ClassCastException.class, v::getDouble);
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
        assertTrue(v.getBoolean());
    }

    @Test
    public void copyOfBooleanFalseWorks()
    {
        Value v = Value.of(false).copy();
        assertFalse(v.isNull());
        assertFalse(v.getBoolean());
    }

    @Test
    public void copyOfLongWorks()
    {
        Value v = Value.of(1).copy();
        assertFalse(v.isNull());
        assertEquals(1L, v.getInteger());
    }

    @Test
    public void copyOfDoubleWorks()
    {
        Value v = Value.of(1.0).copy();
        assertFalse(v.isNull());
        assertEquals(1L, v.getDouble(), 1E-10);
    }

    @Test
    public void copyOfStringWorks()
    {
        Value v = Value.of("1").copy();
        assertFalse(v.isNull());
        assertEquals("1", v.getString());
    }

    @Test
    public void setNullWorks()
    {
        Value v = Value.of(1);
        assertFalse(v.isNull());
        v.setNull();
        assertTrue(v.isNull());
    }

    @Test
    public void setBooleanWorks()
    {
        Value v = Value.nullValue();
        assertTrue(v.isNull());
        v.setBoolean(false);
        assertFalse(v.isNull());
        assertFalse(v.getBoolean());
    }

    @Test
    public void setLongWorks()
    {
        Value v = Value.nullValue();
        assertTrue(v.isNull());
        v.setInteger(1);
        assertFalse(v.isNull());
        assertEquals(1, v.getInteger());
    }

    @Test
    public void setDoubleWorks()
    {
        Value v = Value.nullValue();
        assertTrue(v.isNull());
        v.setDouble(1);
        assertFalse(v.isNull());
        assertEquals(1, v.getDouble(), 1E-10);
    }

    @Test
    public void setStringWorks()
    {
        Value v = Value.nullValue();
        assertTrue(v.isNull());
        v.setString("a");
        assertFalse(v.isNull());
        assertEquals("a", v.getString());
    }
}
