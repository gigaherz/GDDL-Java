package gigaherz.util.gddl2;

import gigaherz.util.gddl2.structure.Collection;
import gigaherz.util.gddl2.structure.Element;
import gigaherz.util.gddl2.structure.Value;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class StructureTest
{
    @Test
    public void collectionOfAddsNames()
    {
        Element value = Value.of(true).withName("test");
        Collection collection = Collection.of(value);
        List<Element> l = collection.byName("test").collect(Collectors.toList());
        assertEquals(1, l.size());
        assertEquals(l.get(0), value);
    }
}
