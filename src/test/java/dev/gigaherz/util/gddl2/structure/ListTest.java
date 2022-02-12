package dev.gigaherz.util.gddl2.structure;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class ListTest
{
    @Test
    public void emptyListContainsNoItems()
    {
        GddlList list = GddlList.empty();
        assertEquals(0, list.size());
    }

    @Test
    public void listOfAddsElements()
    {
        GddlList list = GddlList.of(GddlValue.of(1));
        assertEquals(1, list.size());
    }

    @Test
    public void listAddAddsElements()
    {
        GddlList list = GddlList.empty();
        assertEquals(0, list.size());
        list.add(GddlValue.of(1));
        assertEquals(1, list.size());
    }

    @Test
    public void listAddAllAddsElements()
    {
        GddlList list = GddlList.empty();
        assertEquals(0, list.size());
        list.addAll(Arrays.asList(GddlValue.of(1), GddlValue.of(2), GddlValue.of(3)));
        assertEquals(3, list.size());
    }

    @Test
    public void listGetReturnsElements()
    {
        GddlValue second = GddlValue.of(2);
        GddlList list = GddlList.of(GddlValue.of(1), second, GddlValue.of(3));
        assertEquals(second, list.get(1));
    }

    @Test
    public void listAddInsertsElements()
    {
        GddlValue second = GddlValue.of(2);
        GddlValue third = GddlValue.of(3);
        GddlList list = GddlList.of(GddlValue.of(1), second, GddlValue.of(4));
        assertEquals(second, list.get(1));
        list.add(1, third);
        assertEquals(third, list.get(1));
    }

    @Test
    public void listRemoveElementRemovesElements()
    {
        GddlValue first = GddlValue.of(1);
        GddlValue second = GddlValue.of("test");
        GddlList list = GddlList.of(first, second);
        assertEquals(2, list.size());
        list.remove(second);
        assertEquals(1, list.size());
        assertEquals(first, list.get(0));
    }

    @Test
    public void listRemoveIndexRemovesElements()
    {
        GddlValue second = GddlValue.of("test");
        GddlList list = GddlList.of(GddlValue.of(1), second);
        assertEquals(2, list.size());
        list.remove(0);
        assertEquals(1, list.size());
        assertEquals(second, list.get(0));
    }
}
