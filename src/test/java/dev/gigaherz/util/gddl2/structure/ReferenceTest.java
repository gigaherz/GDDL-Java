package dev.gigaherz.util.gddl2.structure;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReferenceTest
{
    @Test
    public void referenceResolvesToElement()
    {
        GddlMap root = GddlMap.of("child", GddlValue.of("child"));
        GddlReference r = GddlReference.absolute();
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
        GddlReference r = GddlReference.absolute("child");
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
        GddlReference r = GddlReference.relative("child");
        parent.put("reference", r);
        r.resolve(root);
        assertEquals(relativeChild, r.resolvedValue());
    }

    @Test
    public void nestedChildReferenceResolvesToChild()
    {
        GddlValue relativeChild = GddlValue.of("relative child");
        GddlValue absoluteChild = GddlValue.of("absolute child");
        GddlMap parent = GddlMap.of("parent", GddlValue.of("the parent"), "child", relativeChild);
        GddlMap root = GddlMap.of("root", GddlValue.of("root"), "child", absoluteChild, "parent", parent);
        GddlReference r = GddlReference.relative("parent", "child");
        parent.put("reference", r);
        r.resolve(root);
        assertEquals(relativeChild, r.resolvedValue());
    }
}
