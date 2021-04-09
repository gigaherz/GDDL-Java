package dev.gigaherz.util.gddl2.structure;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class CollectionTest
{
    @Test
    public void emptyCollectionContainsNoItems()
    {
        Collection collection = Collection.empty();
        assertEquals(0, collection.size());
    }

    @Test
    public void collectionOfAddsElements()
    {
        Collection collection = Collection.of(Value.of(1));
        assertEquals(1, collection.size());
    }

    @Test
    public void collectionOfAddsNames()
    {
        Element<?> named = Value.of(true).withName("test");
        Collection collection = Collection.of(named);
        List<Element<?>> l = collection.byName("test").collect(Collectors.toList());
        assertEquals(1, l.size());
        assertEquals(l.get(0), named);
    }

    @Test
    public void collectionAddAddsElements()
    {
        Collection collection = Collection.empty();
        assertEquals(0, collection.size());
        collection.add(Value.of(1));
        assertEquals(1, collection.size());
    }

    @Test
    public void collectionAddAddsNames()
    {
        Value named = Value.of(1).withName("test");
        Collection collection = Collection.empty();
        assertEquals(0, collection.size());
        collection.add(named);
        List<Element<?>> l = collection.byName("test").collect(Collectors.toList());
        assertEquals(1, l.size());
        assertEquals(l.get(0), named);
    }

    @Test
    public void collectionAddAllAddsElements()
    {
        Collection collection = Collection.empty();
        assertEquals(0, collection.size());
        collection.addAll(Arrays.asList(Value.of(1), Value.of(2), Value.of(3)));
        assertEquals(3, collection.size());
    }

    @Test
    public void collectionGetReturnsElements()
    {
        Value second = Value.of(2);
        Collection collection = Collection.of(Value.of(1), second, Value.of(3));
        assertEquals(second, collection.get(1));
    }

    @Test
    public void collectionAddInsertsElements()
    {
        Value second = Value.of(2);
        Value third = Value.of(3);
        Collection collection = Collection.of(Value.of(1), second, Value.of(4));
        assertEquals(second, collection.get(1));
        collection.add(1, third);
        assertEquals(third, collection.get(1));
    }

    @Test
    public void collectionRemoveElementRemovesElements()
    {
        Value first = Value.of(1);
        Value second = Value.of("test");
        Collection collection = Collection.of(first, second);
        assertEquals(2, collection.size());
        collection.remove(second);
        assertEquals(1, collection.size());
        assertEquals(first, collection.get(0));
    }

    @Test
    public void collectionRemoveIndexRemovesElements()
    {
        Value second = Value.of("test");
        Collection collection = Collection.of(Value.of(1), second);
        assertEquals(2, collection.size());
        collection.remove(0);
        assertEquals(1, collection.size());
        assertEquals(second, collection.get(0));
    }

    @Test
    public void collectionRemoveElementRemovesNames()
    {
        Value first = Value.of(1);
        Value second = Value.of("test");
        Collection collection = Collection.of(first, second);
        assertEquals(2, collection.size());
        collection.remove(second);
        assertEquals(1, collection.size());
        assertEquals(first, collection.get(0));
        List<Element<?>> byName = collection.byName("test").collect(Collectors.toList());
        assertEquals(0, byName.size());
    }

    @Test
    public void collectionRemoveIndexRemovesNames()
    {
        Value named = Value.of("test").withName("test");
        Collection collection = Collection.of(named, Value.of(1));
        assertEquals(2, collection.size());
        collection.remove(0);
        assertEquals(1, collection.size());
        List<Element<?>> byName = collection.byName("test").collect(Collectors.toList());
        assertEquals(0, byName.size());
    }
}
