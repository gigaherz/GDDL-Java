package gigaherz.utils.GDDL.structure;

import gigaherz.utils.GDDL.config.StringGenerationContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Backreference extends Element
{
    protected final List<String> NamePart = new ArrayList<>();

    protected boolean rooted;

    private boolean resolved;
    private Element resolvedValue;

    Backreference(String... parts)
    {
        Collections.addAll(NamePart, parts);
    }

    Backreference(boolean rooted, String... parts)
    {
        this.rooted = rooted;
        Collections.addAll(NamePart, parts);
    }

    public void add(String name)
    {
        NamePart.add(name);
    }
    public void addAll(Collection<String> names)
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
    protected String toStringInternal(StringGenerationContext ctx)
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

    @Override
    protected Element copy()
    {
        Backreference b = new Backreference();
        copyTo(b);
        return b;
    }

    @Override
    protected void copyTo(Element other)
    {
        super.copyTo(other);
        if(!(other instanceof Backreference))
            throw new IllegalArgumentException("copyTo for invalid type");
        Backreference b = (Backreference)other;
        b.addAll(NamePart);
        if(resolved)
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

            if (!(elm instanceof Set))
                continue;

            Set s = (Set) elm;

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
}