package dev.gigaherz.util.gddl2.structure;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ReferenceTest
{
    @Test
    public void referenceResolvesToElement()
    {
        Collection root = Collection.of(Value.of("child"));
        Reference r = Reference.absolute();
        r.resolve(root, root);
        assertEquals(root, r.resolvedValue());
    }

    @Test
    public void absoluteReferenceResolvesToRoot()
    {
        Collection parent = Collection.of(Value.of("parent"));
        Collection root = Collection.of(Value.of("root"), parent);
        Reference r = Reference.absolute();
        r.resolve(root, parent);
        assertEquals(root, r.resolvedValue());
    }

    @Test
    public void relativeReferenceResolvesToParent()
    {
        Collection parent = Collection.of(Value.of("parent"));
        Collection root = Collection.of(Value.of("root"), parent);
        Reference r = Reference.relative();
        r.resolve(root, parent);
        assertEquals(parent, r.resolvedValue());
    }

    @Test
    public void absoluteChildReferenceResolvesToChild()
    {
        Value relativeChild = Value.of("relative child").withName("child");
        Value absoluteChild = Value.of("absolute child").withName("child");
        Collection parent = Collection.of(Value.of("parent"), relativeChild);
        Collection root = Collection.of(Value.of("root"), absoluteChild, parent);
        Reference r = Reference.absolute("child");
        r.resolve(root, parent);
        assertEquals(absoluteChild, r.resolvedValue());
    }

    @Test
    public void relativeChildReferenceResolvesToChild()
    {
        Value relativeChild = Value.of("relative child").withName("child");
        Value absoluteChild = Value.of("absolute child").withName("child");
        Collection parent = Collection.of(Value.of("parent"), relativeChild);
        Collection root = Collection.of(Value.of("root"), absoluteChild, parent);
        Reference r = Reference.relative("child");
        r.resolve(root, parent);
        assertEquals(relativeChild, r.resolvedValue());
    }

    @Test
    public void nestedChildReferenceResolvesToChild()
    {
        Value relativeChild = Value.of("the child").withName("child");
        Collection parent = Collection.of(Value.of("the parent"), relativeChild).withName("parent");
        Collection root = Collection.of(Value.of("root"), parent);
        Reference r = Reference.relative("parent", "child");
        r.resolve(root, parent);
        assertEquals(relativeChild, r.resolvedValue());
    }
}
