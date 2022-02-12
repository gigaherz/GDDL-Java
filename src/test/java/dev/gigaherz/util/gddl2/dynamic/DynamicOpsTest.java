package dev.gigaherz.util.gddl2.dynamic;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import dev.gigaherz.util.gddl2.structure.GddlList;
import dev.gigaherz.util.gddl2.structure.GddlElement;
import dev.gigaherz.util.gddl2.structure.GddlMap;
import dev.gigaherz.util.gddl2.structure.GddlValue;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class DynamicOpsTest
{
    @Test
    public void testPrimitives()
    {
        assertRoundTrip(GddlValue.of(1), Codec.LONG, 1L);
        assertRoundTrip(GddlValue.of(true), Codec.BOOL, true);
        assertRoundTrip(GddlValue.of(false), Codec.BOOL, false);
        assertRoundTrip(GddlValue.of("a"), Codec.STRING, "a");
        assertRoundTrip(GddlValue.of(1.0), Codec.DOUBLE, 1.0);

        assertRoundTrip(GddlValue.of(1), Codec.BYTE, (byte)1);
        assertRoundTrip(GddlValue.of(1), Codec.SHORT, (short)1);
        assertRoundTrip(GddlValue.of(1), Codec.INT, 1);
        assertRoundTrip(GddlValue.of(1.0), Codec.FLOAT, 1.0f);
    }

    @Test
    public void testObject()
    {
        var expected = GddlMap.of("first", GddlValue.of(1), "second", GddlValue.of(2));
        assertRoundTrip(expected, namedPairCodec(Codec.INT, Codec.INT), Pair.of(1, 2));
    }

    @Test
    public void testList()
    {
        var expected = GddlList.of(GddlValue.of(1), GddlValue.of(2), GddlValue.of(3));
        assertRoundTrip(expected, Codec.list(Codec.INT), List.of(1,2,3));
    }

    @Test
    public void testMap()
    {
        GddlMap expected = GddlMap.of("a", GddlValue.of("1"), "b", GddlValue.of("2"), "c", GddlValue.of("3"));
        assertRoundTrip(expected, Codec.unboundedMap(Codec.STRING, Codec.STRING), Map.of("a","1", "b", "2", "c", "3"));
    }

    private <T> void assertRoundTrip(GddlElement<?> expected, Codec<T> codec, T value)
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

    private static <F, S> Codec<Pair<F, S>> namedPairCodec(Codec<F> first, Codec<S> second)
    {
        return Codec.pair(
                first.fieldOf("first").codec(),
                second.fieldOf("second").codec()
        );
    }
}
