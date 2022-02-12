package dev.gigaherz.util.gddl2.structure;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class GddlReference extends GddlElement<GddlReference>
{
    //region API

    /**
     * Constructs an absolute reference to the given path.
     *
     * @param parts The target, as an array of names of each element along the path
     * @return A Reference set to the given path
     */
    public static GddlReference absolute(String... parts)
    {
        return new GddlReference(true, parts);
    }

    /**
     * Constructs a relative reference to the given path.
     *
     * @param parts The target, as an array of names of each element along the path
     * @return A Reference set to the given path
     */
    public static GddlReference relative(String... parts)
    {
        return new GddlReference(false, parts);
    }

    @Override
    public boolean isReference()
    {
        return true;
    }

    @Override
    public GddlReference asReference()
    {
        return this;
    }

    /**
     * Adds a new name to the path this Reference represents
     *
     * @param name The name of a named element
     */
    public void add(String name)
    {
        nameParts.add(name);
    }

    /**
     * Appends the given array of names to the path this Reference represents
     *
     * @param names The array of names
     */
    public void addAll(String... names)
    {
        nameParts.addAll(Arrays.asList(names));
    }

    /**
     * Appends the given collection of names to the path this Reference represents
     *
     * @param names The collection of names
     */
    public void addAll(java.util.Collection<String> names)
    {
        nameParts.addAll(names);
    }

    /**
     * @return The current path of this reference
     */
    public List<String> getNameParts()
    {
        return Collections.unmodifiableList(nameParts);
    }

    @Override
    public boolean isResolved()
    {
        return resolved;
    }

    @Override
    public GddlElement<?> resolvedValue()
    {
        return resolvedValue;
    }

    //endregion

    //region Implementation
    private final List<String> nameParts = new ArrayList<>();

    private boolean resolved;
    private GddlElement<?> resolvedValue;

    protected final boolean rooted;

    private GddlReference(boolean rooted, String... parts)
    {
        this.rooted = rooted;
        Collections.addAll(nameParts, parts);
    }
    //endregion

    //region Element
    @Override
    protected GddlReference copyInternal()
    {
        GddlReference reference = new GddlReference(rooted);
        copyTo(reference);
        return reference;
    }

    @Override
    protected void copyTo(GddlReference other)
    {
        super.copyTo(other);
        other.addAll(nameParts);
    }

    @Override
    public void resolve(GddlElement<?> root)
    {
        if (isResolved())
            return;

        resolved = tryResolve(root, !rooted);
    }

    private boolean tryResolve(GddlElement<?> root, boolean relative)
    {
        var parent = getParent();

        GddlElement<?> target;
        if (relative)
        {
            target = parent;
            if (target == null) // In case this element is itself the root.
                target = this;
        }
        else
        {
            target = root;
        }

        boolean parentRoot = false;

        if (target.getParent() != null)
        {
            var targetParent = target.getParent();
            if (targetParent.isMap())
            {
                parentRoot = targetParent.asMap().keysOf(target).anyMatch(key -> key.equals(nameParts.get(0)));
            }
        }

        for (int i = parentRoot ? 1 : 0; i < nameParts.size(); i++)
        {
            String part = nameParts.get(i);

            if (!target.isMap())
                continue;

            GddlMap s = target.asMap();

            var ne = s.get(part);
            if (ne != null)
            {
                target = ne;
                continue;
            }

            resolvedValue = null;
            return false;
        }

        if (target != this && !target.isResolved())
            target.resolve(root);

        resolvedValue = target.resolvedValue();

        if (resolvedValue == this)
            throw new IllegalStateException("Invalid cyclic reference: Reference resolves to itself.");

        while(parent != null)
        {
            if (resolvedValue == parent)
                throw new IllegalStateException("Invalid cyclic reference: Reference resolves to a parent of the current element.");
            parent = parent.getParent();
        }

        return resolvedValue != null;
    }

    @Override
    public GddlElement<?> simplify()
    {
        if (resolved && resolvedValue != null)
        {
            return resolvedValue.copy();
        }

        return this;
    }
    //endregion

    //region Equality
    @Override
    public boolean equals(Object other)
    {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        return equalsImpl((GddlReference) other);
    }

    @Override
    public boolean equals(GddlReference other)
    {
        if (this == other) return true;
        if (other == null) return false;
        return equalsImpl(other);
    }

    public boolean equalsImpl(@NotNull GddlReference reference)
    {
        return rooted == reference.rooted &&
                nameParts.equals(reference.nameParts) &&
                (resolved
                        ? reference.resolved && Objects.equals(resolvedValue, reference.resolvedValue)
                        : !reference.resolved);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), nameParts, resolved, resolvedValue, rooted);
    }
    //endregion
}