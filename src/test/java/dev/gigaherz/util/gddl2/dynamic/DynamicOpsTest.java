package dev.gigaherz.util.gddl2.dynamic;

import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import dev.gigaherz.util.gddl2.structure.Collection;
import dev.gigaherz.util.gddl2.structure.Element;
import dev.gigaherz.util.gddl2.structure.Value;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;

import static org.junit.Assert.*;

public class DynamicOpsTest
{
    @Test
    public void testPrimitives()
    {
        assertRoundTrip(Value.of(1), Codec.LONG, 1L);
        assertRoundTrip(Value.of(true), Codec.BOOL, true);
        assertRoundTrip(Value.of(false), Codec.BOOL, false);
        assertRoundTrip(Value.of("a"), Codec.STRING, "a");
        assertRoundTrip(Value.of(1.0), Codec.DOUBLE, 1.0);

        assertRoundTrip(Value.of(1), Codec.BYTE, (byte)1);
        assertRoundTrip(Value.of(1), Codec.SHORT, (short)1);
        assertRoundTrip(Value.of(1), Codec.INT, 1);
        assertRoundTrip(Value.of(1.0), Codec.FLOAT, 1.0f);
    }

    @Test
    public void testObject()
    {
        var expected = Collection.of(Value.of(1).withName("first"), Value.of(2).withName("second"));
        assertRoundTrip(expected, namedPairCodec(Codec.INT, Codec.INT), Pair.of(1, 2), Collection::namedEquals);
    }

    @Test
    public void testList()
    {
        var expected = Collection.of(Value.of(1), Value.of(2), Value.of(3));
        assertRoundTrip(expected, Codec.list(Codec.INT), List.of(1,2,3));
    }

    @Test
    public void testMap()
    {
        Collection expected = Collection.of(Value.of("1").withName("a"), Value.of("2").withName("b"), Value.of("3").withName("c"));
        assertRoundTrip(expected, Codec.unboundedMap(Codec.STRING, Codec.STRING), Map.of("a","1", "b", "2", "c", "3"), Collection::namedEquals);
    }

    private <T> void assertRoundTrip(Element<?> expected, Codec<T> codec, T value)
    {
        var result = codec.encodeStart(GDDLOps.INSTANCE, value);
        var optional = result.result();
        if (optional.isEmpty()) fail("Decode error: " + result.error().get());
        assertEquals(expected, optional.get());
        var result2 = codec.decode(GDDLOps.INSTANCE, optional.get());
        var optional2 = result2.result();
        if (optional2.isEmpty()) fail("Decode error: " + result2.error().get());
        assertEquals(value, optional2.get().getFirst());
    }

    private <T> void assertRoundTrip(Collection expected, Codec<T> codec, T value, BiPredicate<Collection, Element<?>> equality)
    {
        var result = codec.encodeStart(GDDLOps.INSTANCE, value);
        var optional = result.result();
        if (optional.isEmpty()) fail("Decode error: " + result.error().get());
        assertTrue(equality.test(expected, optional.get()));
        var result2 = codec.decode(GDDLOps.INSTANCE, optional.get());
        var optional2 = result2.result();
        if (optional2.isEmpty()) fail("Decode error: " + result2.error().get());
        assertEquals(value, optional2.get().getFirst());
    }

    private static <F, S> Codec<Pair<F, S>> namedPairCodec(Codec<F> first, Codec<S> second)
    {
        return Codec.pair(
                first.fieldOf("first").codec(),
                second.fieldOf("second").codec()
        );
    }
}
