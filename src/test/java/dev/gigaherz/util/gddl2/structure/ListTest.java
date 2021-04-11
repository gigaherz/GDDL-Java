package dev.gigaherz.util.gddl2.structure;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class ListTest
{
    @Test
    public void emptyCollectionContainsNoItems()
    {
        GddlList collection = GddlList.empty();
        assertEquals(0, collection.size());
    }

    @Test
    public void collectionOfAddsElements()
    {
        GddlList collection = GddlList.of(GddlValue.of(1));
        assertEquals(1, collection.size());
    }

    @Test
    public void collectionAddAddsElements()
    {
        GddlList collection = GddlList.empty();
        assertEquals(0, collection.size());
        collection.add(GddlValue.of(1));
        assertEquals(1, collection.size());
    }

    @Test
    public void collectionAddAllAddsElements()
    {
        GddlList collection = GddlList.empty();
        assertEquals(0, collection.size());
        collection.addAll(Arrays.asList(GddlValue.of(1), GddlValue.of(2), GddlValue.of(3)));
        assertEquals(3, collection.size());
    }

    @Test
    public void collectionGetReturnsElements()
    {
        GddlValue second = GddlValue.of(2);
        GddlList collection = GddlList.of(GddlValue.of(1), second, GddlValue.of(3));
        assertEquals(second, collection.get(1));
    }

    @Test
    public void collectionAddInsertsElements()
    {
        GddlValue second = GddlValue.of(2);
        GddlValue third = GddlValue.of(3);
        GddlList collection = GddlList.of(GddlValue.of(1), second, GddlValue.of(4));
        assertEquals(second, collection.get(1));
        collection.add(1, third);
        assertEquals(third, collection.get(1));
    }

    @Test
    public void collectionRemoveElementRemovesElements()
    {
        GddlValue first = GddlValue.of(1);
        GddlValue second = GddlValue.of("test");
        GddlList collection = GddlList.of(first, second);
        assertEquals(2, collection.size());
        collection.remove(second);
        assertEquals(1, collection.size());
        assertEquals(first, collection.get(0));
    }

    @Test
    public void collectionRemoveIndexRemovesElements()
    {
        GddlValue second = GddlValue.of("test");
        GddlList collection = GddlList.of(GddlValue.of(1), second);
        assertEquals(2, collection.size());
        collection.remove(0);
        assertEquals(1, collection.size());
        assertEquals(second, collection.get(0));
    }
}
