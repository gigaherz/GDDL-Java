package dev.gigaherz.util.gddl2.structure;

import dev.gigaherz.util.gddl2.exceptions.ResolutionException;
import dev.gigaherz.util.gddl2.queries.Query;
import dev.gigaherz.util.gddl2.queries.QueryComponent;

import java.util.List;
import java.util.Objects;

public final class GddlReference extends GddlElement<GddlReference>
{
    //region API

    public static GddlReference of(Query query)
    {
        return new GddlReference(query);
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


    @Override
    public boolean isResolved()
    {
        return resolvedValue != null;
    }

    @Override
    public GddlElement<?> resolvedValue()
    {
        return resolvedValue;
    }

    public GddlReference(Query path)
    {
        this.path = path;
    }

    public boolean isAbsolute()
    {
        return path.isAbsolute();
    }

    public List<QueryComponent> getNameParts()
    {
        return path.pathComponents();
    }
    //endregion

    //region Implementation
    private final Query path;

    private GddlElement<?> resolvedValue;
    //endregion

    //region Element

    @Override
    protected GddlReference copyInternal()
    {
        var reference = new GddlReference(path.copy());
        copyTo(reference);
        return reference;
    }

    @Override
    protected void copyTo(GddlReference other)
    {
        super.copyTo(other);
        path.copyTo(other.path);
    }

    @Override
    public void resolve(GddlElement<?> root)
    {
        if (isResolved())
            return;

        TryResolve(root, !isAbsolute());
    }

    private void TryResolve(GddlElement<?> root, boolean relative)
    {
        try
        {
            var parent = getParent();

            GddlElement<?> target;
            if (relative)
            {
                target = parent != null ? parent : this;
            }
            else
            {
                target = root;
            }

            target = path.apply(target).findFirst().orElse(null);

            if (target != null)
            {
                if (!target.isResolved())
                    target.resolve(root);

                resolvedValue = target.resolvedValue();

                if (resolvedValue != null)
                {
                    //noinspection NumberEquality
                    if (resolvedValue == this)
                        throw new IllegalStateException("Invalid cyclic reference: Reference resolves to itself.");

                    while (parent != null)
                    {
                        //noinspection NumberEquality
                        if (resolvedValue == parent)
                            throw new IllegalStateException("Invalid cyclic reference: Reference resolves to a parent of the current element.");
                        parent = parent.getParent();
                    }
                }
            }
        }
        catch (Exception ex)
        {
            throw new ResolutionException("Error resolving reference '" + this + "'", ex);
        }
    }

    @Override
    public GddlElement<?> simplify()
    {
        if (resolvedValue != null)
            return resolvedValue.copy();

        return this;
    }
    //endregion

    //region Equality

    @Override
    public boolean equals(Object other)
    {
        if (other == this) return true;
        if (other == null || getClass() != other.getClass()) return false;
        return equalsImpl((GddlReference) other);
    }

    @Override
    public boolean equals(GddlReference other)
    {
        //noinspection NumberEquality
        if (other == this) return true;
        if (other == null) return false;
        return equalsImpl(other);
    }

    private boolean equalsImpl(GddlReference other)
    {
        return super.equalsImpl(other) &&
                Objects.equals(path, other.path) /*&&
                (IsResolved
                    ? other.IsResolved && Equals(ResolvedValue, other.ResolvedValue)
                    : !other.IsResolved)*/;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), path);
    }
    //endregion
}