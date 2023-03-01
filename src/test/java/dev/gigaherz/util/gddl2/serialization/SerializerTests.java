package dev.gigaherz.util.gddl2.serialization;

import dev.gigaherz.util.gddl2.structure.GddlMap;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SerializerTests
{
    @Test
    public void testSerialize()
    {
        testSerialize(null, "{type=\"null\"}");
        testSerialize(new TestSingleByte().prepare(), "{type=\"object\",className=\"dev.gigaherz.util.gddl2.serialization.SerializerTests$TestSingleByte\",value1=10}");
        testSerialize(new TestSingleShort().prepare(), "{type=\"object\",className=\"dev.gigaherz.util.gddl2.serialization.SerializerTests$TestSingleShort\",value1=10}");
        testSerialize(new TestSingleInt().prepare(), "{type=\"object\",className=\"dev.gigaherz.util.gddl2.serialization.SerializerTests$TestSingleInt\",value1=10}");
        testSerialize(new TestSingleLong().prepare(), "{type=\"object\",className=\"dev.gigaherz.util.gddl2.serialization.SerializerTests$TestSingleLong\",value1=10}");
        testSerialize(new TestSingleFloat().prepare(), "{type=\"object\",className=\"dev.gigaherz.util.gddl2.serialization.SerializerTests$TestSingleFloat\",value1=10.0}");
        testSerialize(new TestSingleDouble().prepare(), "{type=\"object\",className=\"dev.gigaherz.util.gddl2.serialization.SerializerTests$TestSingleDouble\",value1=10.0}");
        testSerialize(new TestSingleBoolean().prepare(), "{type=\"object\",className=\"dev.gigaherz.util.gddl2.serialization.SerializerTests$TestSingleBoolean\",value1=true}");
        testSerialize(new TestString().prepare(), "{type=\"object\",className=\"dev.gigaherz.util.gddl2.serialization.SerializerTests$TestString\",value1=\"Test8\"}");
    }

    @Test
    public void testRoundTrip()
    {
        testRoundTrip(new TestSingleByte().prepare());
        testRoundTrip(new TestSingleShort().prepare());
        testRoundTrip(new TestSingleInt().prepare());
        testRoundTrip(new TestSingleLong().prepare());
        testRoundTrip(new TestSingleFloat().prepare());
        testRoundTrip(new TestSingleDouble().prepare());
        testRoundTrip(new TestSingleBoolean().prepare());
        testRoundTrip(new TestString().prepare());
        testRoundTrip(new TestArrayOfFloats().prepare());
        testRoundTrip(new TestArrayOfStrings().prepare());
        testRoundTrip(new TestListOfStrings().prepare());
        testRoundTrip(new TestSetOfStrings().prepare());
        testRoundTrip(new TestMapOfStrings().prepare());
        testRoundTrip(new TestMapOfLists().prepare());

        testRoundTrip(new TestListOfTests().prepare());
    }

    private static void testSerialize(Object o, String expected)
    {
        GddlMap serialized;

        var serializer = new GddlSerializer();

        serialized = assertDoesNotThrow(() -> serializer.serialize(o));

        String result = serialized.toString();

        assertEquals(expected, result);
    }

    private static void testRoundTrip(Object o)
    {
        GddlMap serialized;

        var serializer = new GddlSerializer();

        serialized = assertDoesNotThrow(() -> serializer.serialize(o));

        Object result = assertDoesNotThrow(() -> serializer.deserialize(o.getClass(), serialized));

        assertEquals(o, result);
    }

    private static abstract class AbstractTest
    {
        public AbstractTest prepare()
        {
            return this;
        }

        @Override
        public abstract boolean equals(Object obj);
    }

    public static class TestSingleByte extends AbstractTest
    {
        byte value1 = 10;

        @Override
        public boolean equals(Object obj)
        {
            if (!(obj instanceof TestSingleByte))
                return false;
            TestSingleByte other = (TestSingleByte) obj;
            return value1 == other.value1;
        }
    }

    public static class TestSingleShort extends AbstractTest
    {
        short value1 = 10;

        @Override
        public boolean equals(Object obj)
        {
            if (!(obj instanceof TestSingleShort))
                return false;
            TestSingleShort other = (TestSingleShort) obj;
            return value1 == other.value1;
        }
    }

    public static class TestSingleInt extends AbstractTest
    {
        int value1 = 10;

        @Override
        public boolean equals(Object obj)
        {
            if (!(obj instanceof TestSingleInt))
                return false;
            TestSingleInt other = (TestSingleInt) obj;
            return value1 == other.value1;
        }
    }

    public static class TestSingleLong extends AbstractTest
    {
        long value1 = 10;

        @Override
        public boolean equals(Object obj)
        {
            if (!(obj instanceof TestSingleLong))
                return false;
            TestSingleLong other = (TestSingleLong) obj;
            return value1 == other.value1;
        }
    }

    public static class TestSingleFloat extends AbstractTest
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
    }

    public static class TestSingleDouble extends AbstractTest
    {
        double value1 = 10;

        @Override
        public boolean equals(Object obj)
        {
            if (!(obj instanceof TestSingleDouble))
                return false;
            TestSingleDouble other = (TestSingleDouble) obj;
            return value1 == other.value1;
        }
    }

    public static class TestSingleBoolean extends AbstractTest
    {
        boolean value1 = true;

        @Override
        public boolean equals(Object obj)
        {
            if (!(obj instanceof TestSingleBoolean))
                return false;
            TestSingleBoolean other = (TestSingleBoolean) obj;
            return value1 == other.value1;
        }
    }

    public static class TestString extends AbstractTest
    {
        String value1 = "Test8";

        @Override
        public boolean equals(Object obj)
        {
            if (!(obj instanceof TestString))
                return false;
            TestString other = (TestString) obj;
            return value1.equals(other.value1);
        }
    }

    public static class TestListOfStrings extends AbstractTest
    {
        List<String> value1 = new ArrayList<String>();

        @Override
        public AbstractTest prepare()
        {
            value1.add("Test1");
            value1.add("Test2");
            value1.add("Test3");
            value1.add("Test4");
            return this;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (!(obj instanceof TestListOfStrings))
                return false;
            TestListOfStrings other = (TestListOfStrings) obj;
            return value1.equals(other.value1);
        }
    }

    public static class TestArrayOfFloats extends AbstractTest
    {
        float[] value1;

        @Override
        public AbstractTest prepare()
        {
            value1 = new float[5];
            value1[0] = 1.0f;
            value1[1] = 1.1f;
            value1[2] = 1.2f;
            value1[3] = Float.POSITIVE_INFINITY;
            value1[4] = Float.NaN;
            return this;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (!(obj instanceof TestArrayOfFloats))
                return false;
            TestArrayOfFloats other = (TestArrayOfFloats) obj;

            return Arrays.equals(value1, other.value1);
        }
    }

    public static class TestArrayOfStrings extends AbstractTest
    {
        String[] value1;

        @Override
        public AbstractTest prepare()
        {
            value1 = new String[4];
            value1[0] = "Test1";
            value1[1] = "Test2";
            value1[2] = "Test3";
            value1[3] = null;
            return this;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (!(obj instanceof TestArrayOfStrings))
                return false;
            TestArrayOfStrings other = (TestArrayOfStrings) obj;

            return Arrays.equals(value1, other.value1);
        }
    }

    public static class TestSetOfStrings extends AbstractTest
    {
        Set<String> value1 = new HashSet<String>();

        @Override
        public AbstractTest prepare()
        {
            value1.add("Test1");
            value1.add("Test2");
            value1.add("Test3");
            value1.add(null);
            return this;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (!(obj instanceof TestSetOfStrings))
                return false;
            TestSetOfStrings other = (TestSetOfStrings) obj;
            return value1.equals(other.value1);
        }
    }

    public static class TestMapOfStrings extends AbstractTest
    {
        Map<String, String> value1 = new HashMap<String, String>();

        @Override
        public AbstractTest prepare()
        {
            value1.put("Key1", "Value1");
            value1.put("Key2", "Value2");
            value1.put("Key3", "Value3");
            value1.put("Key4", "Value4");
            value1.put("Key4", null);
            value1.put(null, "Value4");
            return this;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (!(obj instanceof TestMapOfStrings))
                return false;
            TestMapOfStrings other = (TestMapOfStrings) obj;
            return value1.equals(other.value1);
        }
    }

    public static class TestMapOfLists extends AbstractTest
    {
        Map<String, List> value1 = new HashMap<String, List>();

        @Override
        public AbstractTest prepare()
        {
            value1.put("Key1", makeListFrom("1", "2", "3"));
            value1.put("Key2", makeListFrom("a", "b", "c"));
            value1.put("Key3", makeListFrom(null, "", "\t"));
            return this;
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
            if (!(obj instanceof TestMapOfLists))
                return false;
            TestMapOfLists other = (TestMapOfLists) obj;
            return value1.equals(other.value1);
        }
    }

    public static class TestListOfTests extends AbstractTest
    {
        List<AbstractTest> value1 = new ArrayList<AbstractTest>();

        @Override
        public AbstractTest prepare()
        {
            value1.add(new TestSingleByte().prepare());
            value1.add(new TestSingleShort().prepare());
            value1.add(new TestSingleInt().prepare());
            value1.add(new TestSingleLong().prepare());
            value1.add(new TestSingleFloat().prepare());
            value1.add(new TestSingleDouble().prepare());
            value1.add(new TestSingleBoolean().prepare());
            value1.add(new TestString().prepare());
            value1.add(new TestArrayOfFloats().prepare());
            value1.add(new TestArrayOfStrings().prepare());
            value1.add(new TestListOfStrings().prepare());
            value1.add(new TestSetOfStrings().prepare());
            value1.add(new TestMapOfStrings().prepare());
            value1.add(new TestMapOfLists().prepare());
            return this;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (!(obj instanceof TestListOfTests))
                return false;
            TestListOfTests other = (TestListOfTests) obj;
            return value1.equals(other.value1);
        }
    }
}
