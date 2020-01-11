package gigaherz.util.gddl2.structure;

import gigaherz.util.gddl2.config.StringGenerationContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Reference extends Element
{
    protected final List<String> NamePart = new ArrayList<>();

    private boolean resolved;
    private Element resolvedValue;

    protected boolean rooted;

    Reference(String... parts)
    {
        Collections.addAll(NamePart, parts);
    }

    Reference(boolean rooted, String... parts)
    {
        this.rooted = rooted;
        Collections.addAll(NamePart, parts);
    }

    public void add(String name)
    {
        NamePart.add(name);
    }

    public void addAll(String... names)
    {
        NamePart.addAll(Arrays.asList(names));
    }

    public void addAll(java.util.Collection<String> names)
    {
        NamePart.addAll(names);
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

    @Override
    protected Element copy()
    {
        Reference b = new Reference();
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
        b.addAll(NamePart);
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

        boolean parentRoot = false;
        if (parent.hasName() && NamePart.get(0).equals(parent.getName()))
        {
            parentRoot = true;
        }

        for (int i = parentRoot ? 1 : 0; i < NamePart.size(); i++)
        {
            String part = NamePart.get(i);

            if (!(elm instanceof Collection))
                continue;

            Collection s = (Collection) elm;

            Element ne = s.find(part);
            if (ne != null)
            {
                elm = ne;
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
    protected String toStringImpl(StringGenerationContext ctx)
    {
        StringBuilder ss = new StringBuilder();
        int count = 0;
        for (String it : NamePart)
        {
            if (count++ > 0)
                ss.append(':');
            ss.append(it);
        }

        if (isResolved())
        {
            ss.append('=');
            if (resolvedValue() == null)
                ss.append("NULL");
            else
                ss.append(resolvedValue());
        }

        return ss.toString();
    }
}