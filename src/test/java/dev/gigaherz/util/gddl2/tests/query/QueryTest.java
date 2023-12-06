package dev.gigaherz.util.gddl2.tests.query;

import dev.gigaherz.util.gddl2.queries.Query;
import dev.gigaherz.util.gddl2.structure.GddlList;
import dev.gigaherz.util.gddl2.structure.GddlMap;
import dev.gigaherz.util.gddl2.structure.GddlValue;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class QueryTest
{
    @Test
    public void queryObjectReturnsElement()
    {
        var map = GddlMap.of(
                "key1", GddlValue.of("Text"),
                "key2", GddlValue.of(1)
        );
        assertEquals(List.of(GddlValue.of("Text")), Query.fromString("/key1").apply(map).toList());
        assertEquals(List.of(GddlValue.of(1)), Query.fromString("/key2").apply(map).toList());
        assertEquals(List.of(), Query.fromString("/key3").apply(map).toList());
    }

    @Test
    public void queryListReturnsElement()
    {
        var list = GddlList.of(
                GddlValue.of("Text"),
                GddlValue.of(1)
        );
        assertEquals(List.of(GddlValue.of("Text")), Query.fromString("/[0]").apply(list).toList());
        assertEquals(List.of(GddlValue.of(1)), Query.fromString("/[1]").apply(list).toList());
        assertEquals(List.of(), Query.fromString("/[2]").apply(list).toList());
        assertEquals(List.of(GddlValue.of(1)), Query.fromString("/[^1]").apply(list).toList());
        assertEquals(List.of(GddlValue.of("Text")), Query.fromString("/[^2]").apply(list).toList());
        assertThrows(IllegalArgumentException.class, () -> Query.fromString("/[-1]").apply(list).toList());
    }

    @Test
    public void queryListRangeReturnsRange()
    {
        var list = GddlList.of(
                GddlValue.of("Text"),
                GddlValue.of(1),
                GddlValue.of(false)
        );
        assertEquals(List.of(GddlValue.of("Text")), Query.fromString("/[0..1]").apply(list).toList());
        assertEquals(List.of(GddlValue.of("Text"), GddlValue.of(1)), Query.fromString("/[0...1]").apply(list).toList());
        assertEquals(List.of(GddlValue.of("Text"), GddlValue.of(1), GddlValue.of(false)), Query.fromString("/[0..^0]").apply(list).toList());
        assertEquals(List.of(GddlValue.of(1), GddlValue.of(false)), Query.fromString("/[^2...^0]").apply(list).toList());
    }

    @Test
    public void queryListInsideObject()
    {
        var list1 = GddlList.of(
                GddlValue.of(12345)
        );
        var list = GddlList.of(
                GddlValue.of("A"),
                GddlValue.of(314),
                list1
        );
        var map = GddlMap.of(
                "key1", GddlValue.of("Text"),
                "key2", GddlValue.of(1),
                "key3", list
        );
        assertEquals(List.of(GddlValue.of(314)), Query.fromString("/key3/[1..^1]").apply(map).toList());
        assertEquals(List.of(GddlValue.of(314)), Query.fromString("/key3[1..^1]").apply(map).toList());
        assertEquals(List.of(GddlValue.of(12345)), Query.fromString("/key3[2][0]").apply(map).toList());
    }

    @Test
    public void queryObjectInsideList()
    {
        var map1 = GddlMap.of(
                "key1", GddlValue.of("Text"),
                "key2", GddlValue.of(1)
        );
        var list = GddlList.of(
                GddlValue.of("A"),
                GddlValue.of(314),
                map1
        );
        assertEquals(List.of(GddlValue.of("Text")), Query.fromString("/[2]/key1").apply(list).toList());
        assertEquals(List.of(GddlValue.of(1)), Query.fromString("/[2]/key2").apply(list).toList());
    }
}
