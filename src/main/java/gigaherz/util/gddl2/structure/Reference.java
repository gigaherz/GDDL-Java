package gigaherz.util.gddl2.structure;

import java.util.*;

public class Reference extends Element
{
    public static Reference absolute(String... parts)
    {
        return new Reference(true, parts);
    }

    public static Reference relative(String... parts)
    {
        return new Reference(false, parts);
    }

    protected final List<String> nameParts = new ArrayList<>();

    private boolean resolved;
    private Element resolvedValue;

    protected boolean rooted;

    private Reference(boolean rooted, String... parts)
    {
        this.rooted = rooted;
        Collections.addAll(nameParts, parts);
    }

    public void add(String name)
    {
        nameParts.add(name);
    }

    public void addAll(String... names)
    {
        nameParts.addAll(Arrays.asList(names));
    }

    public void addAll(java.util.Collection<String> names)
    {
        nameParts.addAll(names);
    }

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
    public Element resolvedValue()
    {
        return resolvedValue;
    }

    public Reference withName(String name)
    {
        super.withName(name);
        return this;
    }

    @Override
    protected Reference copy()
    {
        Reference b = new Reference(rooted);
        copyTo(b);
        return b;
    }

    @Override
    protected void copyTo(Element other)
    {
        super.copyTo(other);
        if (!(other instanceof Reference))
            throw new IllegalArgumentException("copyTo for invalid type");
        Reference b = (Reference) other;
        b.addAll(nameParts);
        if (resolved)
        {
            b.resolved = true;
            b.resolvedValue = resolvedValue;
        }
    }

    @Override
    public void resolve(Element root, Element parent)
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

    private boolean tryResolve(Element root, Element parent, boolean relative)
    {
        Element elm = relative ? parent : root;

        boolean parentRoot = parent.hasName() && nameParts.get(0).equals(parent.getName());

        for (int i = parentRoot ? 1 : 0; i < nameParts.size(); i++)
        {
            String part = nameParts.get(i);

            if (!(elm instanceof Collection))
                continue;

            Collection s = (Collection) elm;

            var ne = s.get(part);
            if (ne.isPresent())
            {
                elm = ne.get();
                continue;
            }

            resolvedValue = null;
            return false;
        }

        if (!elm.isResolved())
            elm.resolve(root, parent);

        resolvedValue = elm.resolvedValue();

        return resolvedValue != null;
    }

    @Override
    public Element simplify()
    {
        if (resolved && resolvedValue != null)
        {
            Element resolved = resolvedValue.copy();
            resolved.setName(getName());
            return resolved;
        }

        return this;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Reference reference = (Reference) o;
        return resolved == reference.resolved &&
                rooted == reference.rooted &&
                nameParts.equals(reference.nameParts) &&
                Objects.equals(resolvedValue, reference.resolvedValue);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), nameParts, resolved, resolvedValue, rooted);
    }
}