package dev.gigaherz.util.gddl2.tests.structure;

import dev.gigaherz.util.gddl2.queries.Query;
import dev.gigaherz.util.gddl2.structure.GddlMap;
import dev.gigaherz.util.gddl2.structure.GddlReference;
import dev.gigaherz.util.gddl2.structure.GddlValue;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReferenceTest
{
    @Test
    public void referenceResolvesToElement()
    {
        GddlMap root = GddlMap.of("child", GddlValue.of("child"));
        GddlReference r = GddlReference.of(new Query().absolute());
        r.resolve(root);
        assertEquals(root, r.resolvedValue());
    }

    @Test
    public void absoluteChildReferenceResolvesToChild()
    {
        GddlValue relativeChild = GddlValue.of("relative child");
        GddlValue absoluteChild = GddlValue.of("absolute child");
        GddlMap parent = GddlMap.of("parent", GddlValue.of("parent"), "child", relativeChild);
        GddlMap root = GddlMap.of("root", GddlValue.of("root"), "child", absoluteChild, "parent", parent);
        GddlReference r = GddlReference.of(new Query().absolute().byKey("child"));
        parent.put("reference", r);
        r.resolve(root);
        assertEquals(absoluteChild, r.resolvedValue());
    }

    @Test
    public void relativeChildReferenceResolvesToChild()
    {
        GddlValue relativeChild = GddlValue.of("relative child");
        GddlValue absoluteChild = GddlValue.of("absolute child");
        GddlMap parent = GddlMap.of("parent", GddlValue.of("parent"), "child", relativeChild);
        GddlMap root = GddlMap.of("root", GddlValue.of("root"), "child", absoluteChild, "parent", parent);
        GddlReference r = GddlReference.of(new Query().byKey("child"));
        parent.put("reference", r);
        r.resolve(root);
        assertEquals(relativeChild, r.resolvedValue());
    }

    @Test
    public void nestedChildReferenceResolvesToChild()
    {
        GddlValue relativeChild = GddlValue.of("relative child");
        GddlValue absoluteChild = GddlValue.of("absolute child");
        GddlMap parent2 = GddlMap.of("parent", GddlValue.of("parent"), "child", relativeChild);
        GddlMap parent = GddlMap.of("parent", parent2);
        GddlMap root = GddlMap.of("root", GddlValue.of("root"), "child", absoluteChild, "parent", parent);
        GddlReference r = GddlReference.of(new Query().byKey("parent").byKey("child"));
        parent.put("reference", r);
        r.resolve(root);
        assertEquals(relativeChild, r.resolvedValue());
    }
}
