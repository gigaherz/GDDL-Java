package dev.gigaherz.util.gddl2.tests.serialization;

import dev.gigaherz.util.gddl2.serialization.GddlSerializer;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class SerializerTests
{
    @Test
    public void testSerialize()
    {
        testSerialize(null, "null");
        testSerialize((byte)10, "10");
        testSerialize((short)10, "10");
        testSerialize(10, "10");
        testSerialize(10L, "10");
        testSerialize(10.0f, "10.0");
        testSerialize(10.0, "10.0");
        testSerialize("10", "\"10\"");
        testSerialize(new byte[]{1,2,3}, "[1,2,3]");
        testSerialize(new int[]{1,2,3}, "[1,2,3]");
        testSerialize(new long[]{1,2,3}, "[1,2,3]");
        testSerialize(new double[]{1,2,3}, "[1.0,2.0,3.0]");
        testSerialize(List.of(1,2,3), "{class=\"java.util.ImmutableCollections$ListN\",elements=[{class=\"java.lang.Integer\",value=1},{class=\"java.lang.Integer\",value=2},{class=\"java.lang.Integer\",value=3}]}");
        testSerialize(new ByteField(), "{class=\""+ByteField.class.getName()+"\",value1=10}");
        testSerialize(new ShortField(), "{class=\""+ShortField.class.getName()+"\",value1=10}");
        testSerialize(new IntField(), "{class=\""+IntField.class.getName()+"\",value1=10}");
        testSerialize(new LongField(), "{class=\""+LongField.class.getName()+"\",value1=10}");
        testSerialize(new FloatField(), "{class=\""+FloatField.class.getName()+"\",value1=10.0}");
        testSerialize(new DoubleField(), "{class=\""+DoubleField.class.getName()+"\",value1=10.0}");
        testSerialize(new BooleanField(), "{class=\""+BooleanField.class.getName()+"\",value1=true}");
        testSerialize(new StringField(), "{class=\""+StringField.class.getName()+"\",value1=\"Test8\"}");
        testSerialize(new ArrayOfBytesField(), "{class=\""+ArrayOfBytesField.class.getName()+"\",value1=[1,2,3,4]}");
        testSerialize(new ArrayOfStringsField(), "{class=\""+ArrayOfStringsField.class.getName()+"\",value1=[\"Test1\",\"Test2\",\"Test3\",null]}");
        testSerialize(new NullInListField(), "{class=\""+NullInListField.class.getName()+"\",value1={class=\"java.util.ArrayList\",elements=[null]}}");
    }

    @Test
    public void testRoundTrip()
    {
        testRoundTrip((byte)10);
        testRoundTrip((short)10);
        testRoundTrip(10);
        testRoundTrip(10L);
        testRoundTrip(10.0f);
        testRoundTrip(10.0);
        testRoundTrip("10");
        testRoundTrip(new byte[]{1,2,3});
        testRoundTrip(new int[]{1,2,3});
        testRoundTrip(new long[]{1,2,3});
        testRoundTrip(new double[]{1,2,3});
        testRoundTrip(new ByteField());
        testRoundTrip(new ShortField());
        testRoundTrip(new IntField());
        testRoundTrip(new LongField());
        testRoundTrip(new FloatField());
        testRoundTrip(new DoubleField());
        testRoundTrip(new BooleanField());
        testRoundTrip(new StringField());
        testRoundTrip(new TestArrayOfFloats());
        testRoundTrip(new NullInListField());
        testRoundTrip(new ListOfStringsField());
        testRoundTrip(new SetOfStringsField());
        testRoundTrip(new MapOfStringsField());
        testRoundTrip(new MapOfListsField());

        testRoundTrip(new ComplexListField());
    }

    private static void testSerialize(Object o, String expected)
    {
        var serializer = new GddlSerializer();

        var serialized = assertDoesNotThrow(() -> serializer.serialize(o));

        String result = serialized.toString();

        assertEquals(expected, result);
    }

    private static void testRoundTrip(Object o)
    {
        var serializer = new GddlSerializer();

        var serialized = assertDoesNotThrow(() -> serializer.serialize(o));

        Object result = assertDoesNotThrow(() -> serializer.deserialize(serialized, o.getClass()));

        if (o.getClass().isArray())
        {
            var ct = o.getClass().getComponentType();
            if (ct == byte.class)
                assertArrayEquals((byte[]) o, (byte[]) result);
            else if(ct == short.class)
                assertArrayEquals((short[]) o, (short[]) result);
            else if(ct == int.class)
                assertArrayEquals((int[]) o, (int[]) result);
            else if(ct == long.class)
                assertArrayEquals((long[]) o, (long[]) result);
            else if(ct == float.class)
                assertArrayEquals((float[]) o, (float[]) result);
            else if(ct == double.class)
                assertArrayEquals((double[]) o, (double[]) result);
            else if(ct == boolean.class)
                assertArrayEquals((boolean[]) o, (boolean[]) result);
            else
                assertArrayEquals((Object[]) o, (Object[]) result);
        }
        else
        {
            assertEquals(o, result);
        }
    }

    public static class ByteField
    {
        byte value1 = 10;

        @Override
        public boolean equals(Object obj)
        {
            if (!(obj instanceof ByteField other))
                return false;
            return value1 == other.value1;
        }

        @Override
        public String toString()
        {
            return "TestSingleByte[" + value1 + "]";
        }
    }

    public static class ShortField
    {
        short value1 = 10;

        @Override
        public boolean equals(Object obj)
        {
            if (!(obj instanceof ShortField other))
                return false;
            return value1 == other.value1;
        }

        @Override
        public String toString()
        {
            return "TestSingleShort[" + value1 + "]";
        }
    }

    public static class IntField
    {
        int value1 = 10;

        @Override
        public boolean equals(Object obj)
        {
            if (!(obj instanceof IntField other))
                return false;
            return value1 == other.value1;
        }

        @Override
        public String toString()
        {
            return "TestSingleInt[" + value1 + "]";
        }
    }

    public static class LongField
    {
        long value1 = 10;

        @Override
        public boolean equals(Object obj)
        {
            if (!(obj instanceof LongField other))
                return false;
            return value1 == other.value1;
        }

        @Override
        public String toString()
        {
            return "TestSingleLong[" + value1 + "]";
        }
    }

    public static class FloatField
    {
        float value1 = 10;

        @Override
        public boolean equals(Object obj)
        {
            if (!(obj instanceof FloatField other))
                return false;
            return value1 == other.value1;
        }

        @Override
        public String toString()
        {
            return "TestSingleFloat[" + value1 + "]";
        }
    }

    public static class DoubleField
    {
        double value1 = 10;

        @Override
        public boolean equals(Object obj)
        {
            if (!(obj instanceof DoubleField other))
                return false;
            return value1 == other.value1;
        }

        @Override
        public String toString()
        {
            return "TestSingleDouble[" + value1 + "]";
        }
    }

    public static class BooleanField
    {
        boolean value1 = true;

        @Override
        public boolean equals(Object obj)
        {
            if (!(obj instanceof BooleanField other))
                return false;
            return value1 == other.value1;
        }

        @Override
        public String toString()
        {
            return "TestSingleBoolean[" + value1 + "]";
        }
    }

    public static class StringField
    {
        String value1 = "Test8";

        @Override
        public boolean equals(Object obj)
        {
            if (!(obj instanceof StringField other))
                return false;
            return value1.equals(other.value1);
        }

        @Override
        public String toString()
        {
            return "TestString[" + value1 + "]";
        }
    }

    public static class ListOfStringsField
    {
        List<String> value1 = new ArrayList<>();

        public ListOfStringsField()
        {
            value1.add("Test1");
            value1.add("Test2");
            value1.add("Test3");
            value1.add("Test4");
        }

        @Override
        public boolean equals(Object obj)
        {
            if (!(obj instanceof ListOfStringsField other))
                return false;
            return value1.equals(other.value1);
        }

        @Override
        public String toString()
        {
            return "TestListOfStrings[" + value1 + "]";
        }
    }

    public static class TestArrayOfFloats 
    {
        float[] value1;

        public TestArrayOfFloats()
        {
            value1 = new float[5];
            value1[0] = 1.0f;
            value1[1] = 1.1f;
            value1[2] = 1.2f;
            value1[3] = Float.POSITIVE_INFINITY;
            value1[4] = Float.NaN;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (!(obj instanceof TestArrayOfFloats other))
                return false;

            return Arrays.equals(value1, other.value1);
        }

        @Override
        public String toString()
        {
            return "TestArrayOfFloats[" + Arrays.toString(value1) + "]";
        }
    }

    public static class ArrayOfBytesField
    {
        byte[] value1;

        public ArrayOfBytesField()
        {
            value1 = new byte[4];
            value1[0] = 1;
            value1[1] = 2;
            value1[2] = 3;
            value1[3] = 4;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (!(obj instanceof ArrayOfBytesField other))
                return false;

            return Arrays.equals(value1, other.value1);
        }

        @Override
        public String toString()
        {
            return "ArrayOfBytesField[" + Arrays.toString(value1) + "]";
        }
    }

    public static class ArrayOfStringsField
    {
        String[] value1;

        public ArrayOfStringsField()
        {
            value1 = new String[4];
            value1[0] = "Test1";
            value1[1] = "Test2";
            value1[2] = "Test3";
            value1[3] = null;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (!(obj instanceof ArrayOfStringsField other))
                return false;

            return Arrays.equals(value1, other.value1);
        }

        @Override
        public String toString()
        {
            return "TestArrayOfStrings[" + Arrays.toString(value1) + "]";
        }
    }

    public static class NullInListField
    {
        List<Object> value1;

        public NullInListField()
        {
            value1 = new ArrayList<>();
            value1.add(null);
        }

        @Override
        public boolean equals(Object obj)
        {
            if (!(obj instanceof NullInListField other))
                return false;

            return Objects.equals(value1, other.value1);
        }

        @Override
        public String toString()
        {
            return "TestListOfNull[" + value1 + "]";
        }
    }

    public static class SetOfStringsField
    {
        Set<String> value1 = new HashSet<>();

        public SetOfStringsField()
        {
            value1.add("Test1");
            value1.add("Test2");
            value1.add("Test3");
            value1.add(null);
        }

        @Override
        public boolean equals(Object obj)
        {
            if (!(obj instanceof SetOfStringsField other))
                return false;
            return value1.equals(other.value1);
        }

        @Override
        public String toString()
        {
            return "TestSetOfStrings[" + value1 + "]";
        }
    }

    public static class MapOfStringsField
    {
        Map<String, String> value1 = new HashMap<>();

        public MapOfStringsField()
        {
            value1.put("Key1", "Value1");
            value1.put("Key2", "Value2");
            value1.put("Key3", "Value3");
            value1.put("Key4", "Value4");
            value1.put("Key5", null);
            value1.put(null, "Value4");
        }

        @Override
        public boolean equals(Object obj)
        {
            if (!(obj instanceof MapOfStringsField other))
                return false;
            return value1.equals(other.value1);
        }

        @Override
        public String toString()
        {
            return "TestMapOfStrings[" + value1 + "]";
        }
    }

    public static class MapOfListsField
    {
        Map<String, List<Object>> value1 = new HashMap<>();

        public MapOfListsField()
        {
            value1.put("Key1", makeListFrom("1", "2", "3"));
            value1.put("Key2", makeListFrom("a", "b", "c"));
            value1.put("Key3", makeListFrom(null, "", "\t"));
        }

        private List<Object> makeListFrom(String... s)
        {
            return new ArrayList<>(Arrays.asList(s));
        }

        @Override
        public boolean equals(Object obj)
        {
            return obj instanceof MapOfListsField other && value1.equals(other.value1);
        }

        @Override
        public String toString()
        {
            return "TestMapOfLists[" + value1 + "]";
        }
    }

    public static class ComplexListField
    {
        List<Object> value1 = new ArrayList<>();

        public ComplexListField()
        {
            value1.add(new ByteField());
            value1.add(new ShortField());
            value1.add(new IntField());
            value1.add(new LongField());
            value1.add(new FloatField());
            value1.add(new DoubleField());
            value1.add(new BooleanField());
            value1.add(new StringField());
            value1.add(new TestArrayOfFloats());
            value1.add(new NullInListField());
            value1.add(new ListOfStringsField());
            value1.add(new SetOfStringsField());
            value1.add(new MapOfStringsField());
            value1.add(new MapOfListsField());
        }

        @Override
        public boolean equals(Object obj)
        {
            if (!(obj instanceof ComplexListField other))
                return false;
            return value1.equals(other.value1);
        }

        @Override
        public String toString()
        {
            return "TestListOfTests[" + value1 + "]";
        }
    }
}
