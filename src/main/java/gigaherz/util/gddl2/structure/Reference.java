package gigaherz.util.gddl2.structure;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class Reference extends Element<Reference>
{
    // Factory Methods

    /**
     * Constructs an absolute reference to the given path.
     * @param parts The target, as an array of names of each element along the path
     * @return A Reference set to the given path
     */
    public static Reference absolute(String... parts)
    {
        return new Reference(true, parts);
    }

    /**
     * Constructs a relative reference to the given path.
     * @param parts The target, as an array of names of each element along the path
     * @return A Reference set to the given path
     */
    public static Reference relative(String... parts)
    {
        return new Reference(false, parts);
    }

    private final List<String> nameParts = new ArrayList<>();

    private boolean resolved;
    private Element<?> resolvedValue;

    protected final boolean rooted;

    private Reference(boolean rooted, String... parts)
    {
        this.rooted = rooted;
        Collections.addAll(nameParts, parts);
    }

    /**
     * Adds a new name to the path this Reference represents
     * @param name The name of a named element
     */
    public void add(String name)
    {
        nameParts.add(name);
    }

    /**
     * Appends the given array of names to the path this Reference represents
     * @param names The array of names
     */
    public void addAll(String... names)
    {
        nameParts.addAll(Arrays.asList(names));
    }

    /**
     * Appends the given collection of names to the path this Reference represents
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
    public Element<?> resolvedValue()
    {
        return resolvedValue;
    }

    @Override
    public Reference withName(String name)
    {
        super.withName(name);
        return this;
    }

    @Override
    protected Reference copyInternal()
    {
        Reference reference = new Reference(rooted);
        copyTo(reference);
        return reference;
    }

    @Override
    protected void copyTo(Reference other)
    {
        super.copyTo(other);
        other.addAll(nameParts);
    }

    @Override
    public void resolve(Element<?> root, @Nullable Collection parent)
    {
        if (isResolved())
            return;

        if (!rooted && tryResolve(root, parent, true))
        {
            resolved = true;
            return;
        }

        resolved = tryResolve(root, parent, false);
    }

    private boolean tryResolve(Element<?> root, @Nullable Collection parent, boolean relative)
    {
        Element<?> target;
        if (relative)
        {
            target = parent != null ? parent : this;
        }
        else
        {
            target = root;
        }

        boolean parentRoot = target.hasName() && nameParts.get(0).equals(target.getName());

        for (int i = parentRoot ? 1 : 0; i < nameParts.size(); i++)
        {
            String part = nameParts.get(i);

            if (!(target instanceof Collection))
                continue;

            Collection s = (Collection) target;

            var ne = s.get(part);
            if (ne.isPresent())
            {
                target = ne.get();
                continue;
            }

            resolvedValue = null;
            return false;
        }

        if (!target.isResolved())
            target.resolve(root, target.getParentInternal());

        resolvedValue = target.resolvedValue();

        return resolvedValue != null;
    }

    @Override
    public Element<?> simplify()
    {
        if (resolved && resolvedValue != null)
        {
            Element<?> resolved = resolvedValue.copy();
            resolved.setName(getName());
            return resolved;
        }

        return this;
    }

    @Override
    public boolean equals(Object other)
    {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        return equalsImpl((Reference) other);
    }

    @Override
    public boolean equals(Reference other)
    {
        if (this == other) return true;
        if (null == other) return false;
        return equalsImpl(other);
    }

    @Override
    public boolean equalsImpl(@NotNull Reference reference)
    {
        return super.equalsImpl(reference) &&
                rooted == reference.rooted &&
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
}