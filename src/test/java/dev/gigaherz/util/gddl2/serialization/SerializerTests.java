package dev.gigaherz.util.gddl2.serialization;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
        testSerialize(true, "true");
        testSerialize(List.of(1,2,3), "{class=\"java.util.ImmutableCollections$ListN\",elements=[{class=\"java.lang.Integer\",value=1},{class=\"java.lang.Integer\",value=2},{class=\"java.lang.Integer\",value=3}]}");
        testSerialize(new TestSingleString(), "{class=\"dev.gigaherz.util.gddl2.serialization.SerializerTests$TestSingleString\",value1=\"Test8\"}");
        testSerialize(new TestSingleByte(), "{class=\"dev.gigaherz.util.gddl2.serialization.SerializerTests$TestSingleByte\",value1=10}");
        testSerialize(new TestSingleShort(), "{class=\"dev.gigaherz.util.gddl2.serialization.SerializerTests$TestSingleShort\",value1=10}");
        testSerialize(new TestSingleInt(), "{class=\"dev.gigaherz.util.gddl2.serialization.SerializerTests$TestSingleInt\",value1=10}");
        testSerialize(new TestSingleLong(), "{class=\"dev.gigaherz.util.gddl2.serialization.SerializerTests$TestSingleLong\",value1=10}");
        testSerialize(new TestSingleFloat(), "{class=\"dev.gigaherz.util.gddl2.serialization.SerializerTests$TestSingleFloat\",value1=10.0}");
        testSerialize(new TestSingleDouble(), "{class=\"dev.gigaherz.util.gddl2.serialization.SerializerTests$TestSingleDouble\",value1=10.0}");
        testSerialize(new TestSingleBoolean(), "{class=\"dev.gigaherz.util.gddl2.serialization.SerializerTests$TestSingleBoolean\",value1=true}");
        testSerialize(new TestSingleString(), "{class=\"dev.gigaherz.util.gddl2.serialization.SerializerTests$TestSingleString\",value1=\"Test8\"}");
        testSerialize(new TestArrayOfStrings(), "{class=\"dev.gigaherz.util.gddl2.serialization.SerializerTests$TestArrayOfStrings\",value1=[\"Test1\",\"Test2\",\"Test3\",null]}");
        testSerialize(new TestListOfNull(), "{class=\"dev.gigaherz.util.gddl2.serialization.SerializerTests$TestListOfNull\",value1={class=\"java.util.ArrayList\",elements=[null]}}");
    }

    @Test
    public void testRoundTrip()
    {
        testRoundTrip(new TestSingleByte());
        testRoundTrip(new TestSingleShort());
        testRoundTrip(new TestSingleInt());
        testRoundTrip(new TestSingleLong());
        testRoundTrip(new TestSingleFloat());
        testRoundTrip(new TestSingleDouble());
        testRoundTrip(new TestSingleBoolean());
        testRoundTrip(new TestSingleString());
        testRoundTrip(new TestArrayOfFloats());
        testRoundTrip(new TestListOfNull());
        testRoundTrip(new TestListOfStrings());
        testRoundTrip(new TestSetOfStrings());
        testRoundTrip(new TestMapOfStrings());
        testRoundTrip(new TestMapOfLists());

        testRoundTrip(new TestListOfTests());
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

        assertEquals(o, result);
    }

    public static class TestSingleByte 
    {
        byte value1 = 10;

        @Override
        public boolean equals(Object obj)
        {
            if (!(obj instanceof TestSingleByte other))
                return false;
            return value1 == other.value1;
        }

        @Override
        public String toString()
        {
            return "TestSingleByte[" + value1 + "]";
        }
    }

    public static class TestSingleShort 
    {
        short value1 = 10;

        @Override
        public boolean equals(Object obj)
        {
            if (!(obj instanceof TestSingleShort other))
                return false;
            return value1 == other.value1;
        }

        @Override
        public String toString()
        {
            return "TestSingleShort[" + value1 + "]";
        }
    }

    public static class TestSingleInt 
    {
        int value1 = 10;

        @Override
        public boolean equals(Object obj)
        {
            if (!(obj instanceof TestSingleInt other))
                return false;
            return value1 == other.value1;
        }

        @Override
        public String toString()
        {
            return "TestSingleInt[" + value1 + "]";
        }
    }

    public static class TestSingleLong 
    {
        long value1 = 10;

        @Override
        public boolean equals(Object obj)
        {
            if (!(obj instanceof TestSingleLong other))
                return false;
            return value1 == other.value1;
        }

        @Override
        public String toString()
        {
            return "TestSingleLong[" + value1 + "]";
        }
    }

    public static class TestSingleFloat 
    {
        float value1 = 10;

        @Override
        public boolean equals(Object obj)
        {
            if (!(obj instanceof TestSingleFloat))
                return false;
            TestSingleFloat other = (TestSingleFloat) obj;
            return value1 == other.value1;
        }

        @Override
        public String toString()
        {
            return "TestSingleFloat[" + value1 + "]";
        }
    }

    public static class TestSingleDouble 
    {
        double value1 = 10;

        @Override
        public boolean equals(Object obj)
        {
            if (!(obj instanceof TestSingleDouble other))
                return false;
            return value1 == other.value1;
        }

        @Override
        public String toString()
        {
            return "TestSingleDouble[" + value1 + "]";
        }
    }

    public static class TestSingleBoolean 
    {
        boolean value1 = true;

        @Override
        public boolean equals(Object obj)
        {
            if (!(obj instanceof TestSingleBoolean other))
                return false;
            return value1 == other.value1;
        }

        @Override
        public String toString()
        {
            return "TestSingleBoolean[" + value1 + "]";
        }
    }

    public static class TestSingleString
    {
        String value1 = "Test8";

        @Override
        public boolean equals(Object obj)
        {
            if (!(obj instanceof TestSingleString other))
                return false;
            return value1.equals(other.value1);
        }

        @Override
        public String toString()
        {
            return "TestString[" + value1 + "]";
        }
    }

    public static class TestListOfStrings 
    {
        List<String> value1 = new ArrayList<String>();

        public TestListOfStrings()
        {
            value1.add("Test1");
            value1.add("Test2");
            value1.add("Test3");
            value1.add("Test4");
        }

        @Override
        public boolean equals(Object obj)
        {
            if (!(obj instanceof TestListOfStrings other))
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

    public static class TestArrayOfStrings 
    {
        String[] value1;

        public TestArrayOfStrings()
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
            if (!(obj instanceof TestArrayOfStrings other))
                return false;

            return Arrays.equals(value1, other.value1);
        }

        @Override
        public String toString()
        {
            return "TestArrayOfStrings[" + Arrays.toString(value1) + "]";
        }
    }

    public static class TestListOfNull
    {
        List value1;

        public TestListOfNull()
        {
            value1 = new ArrayList();
            value1.add(null);
        }

        @Override
        public boolean equals(Object obj)
        {
            if (!(obj instanceof TestListOfNull other))
                return false;

            return Objects.equals(value1, other.value1);
        }

        @Override
        public String toString()
        {
            return "TestListOfNull[" + value1 + "]";
        }
    }

    public static class TestSetOfStrings 
    {
        Set<String> value1 = new HashSet<>();

        public TestSetOfStrings()
        {
            value1.add("Test1");
            value1.add("Test2");
            value1.add("Test3");
            value1.add(null);
        }

        @Override
        public boolean equals(Object obj)
        {
            if (!(obj instanceof TestSetOfStrings other))
                return false;
            return value1.equals(other.value1);
        }

        @Override
        public String toString()
        {
            return "TestSetOfStrings[" + value1 + "]";
        }
    }

    public static class TestMapOfStrings 
    {
        Map<String, String> value1 = new HashMap<String, String>();

        public TestMapOfStrings()
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
            if (!(obj instanceof TestMapOfStrings other))
                return false;
            return value1.equals(other.value1);
        }

        @Override
        public String toString()
        {
            return "TestMapOfStrings[" + value1 + "]";
        }
    }

    public static class TestMapOfLists 
    {
        Map<String, List> value1 = new HashMap<>();

        public TestMapOfLists()
        {
            value1.put("Key1", makeListFrom("1", "2", "3"));
            value1.put("Key2", makeListFrom("a", "b", "c"));
            value1.put("Key3", makeListFrom(null, "", "\t"));
        }

        private List makeListFrom(String... s)
        {
            List<String> list = new ArrayList<String>();
            list.addAll(Arrays.asList(s));
            return list;
        }

        @Override
        public boolean equals(Object obj)
        {
            return obj instanceof TestMapOfLists other && value1.equals(other.value1);
        }

        @Override
        public String toString()
        {
            return "TestMapOfLists[" + value1 + "]";
        }
    }

    public static class TestListOfTests 
    {
        List<Object> value1 = new ArrayList<>();

        public TestListOfTests()
        {
            value1.add(new TestSingleByte());
            value1.add(new TestSingleShort());
            value1.add(new TestSingleInt());
            value1.add(new TestSingleLong());
            value1.add(new TestSingleFloat());
            value1.add(new TestSingleDouble());
            value1.add(new TestSingleBoolean());
            value1.add(new TestSingleString());
            value1.add(new TestArrayOfFloats());
            value1.add(new TestListOfNull());
            value1.add(new TestListOfStrings());
            value1.add(new TestSetOfStrings());
            value1.add(new TestMapOfStrings());
            value1.add(new TestMapOfLists());
        }

        @Override
        public boolean equals(Object obj)
        {
            if (!(obj instanceof TestListOfTests))
                return false;
            TestListOfTests other = (TestListOfTests) obj;
            return value1.equals(other.value1);
        }

        @Override
        public String toString()
        {
            return "TestListOfTests[" + value1 + "]";
        }
    }
}
