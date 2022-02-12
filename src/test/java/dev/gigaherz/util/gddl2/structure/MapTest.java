package dev.gigaherz.util.gddl2.structure;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MapTest
{
    @Test
    public void emptyMapContainsNoItems()
    {
        GddlMap collection = GddlMap.empty();
        assertEquals(0, collection.size());
    }

    @Test
    public void mapOfAddsNames()
    {
        GddlElement<?> element = GddlValue.of(true);
        GddlMap collection = GddlMap.of("test", element);
        GddlElement<?> value = collection.get("test");
        assertNotNull(value);
        assertEquals(element, value);
    }

}
