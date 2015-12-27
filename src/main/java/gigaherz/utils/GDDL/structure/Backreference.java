package gigaherz.utils.GDDL.structure;

import gigaherz.utils.GDDL.config.StringGenerationContext;

import java.util.ArrayList;
import java.util.List;

public class Backreference extends Element
{
    protected final List<String> NamePart = new ArrayList<>();

    // TODO: Figure out what this syntax feature was meant to be used for XD
    protected boolean Rooted;

    private boolean resolved;
    private Element resolvedValue;

    Backreference(boolean rooted, String I)
    {
        Rooted = rooted;
        NamePart.add(I);
    }

    public void append(String I)
    {
        NamePart.add(I);
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
    protected String toStringInternal()
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
    protected String toStringInternal(StringGenerationContext ctx)
    {
        return toStringInternal();
    }

    @Override
    public void resolve(Element root)
    {
        if (isResolved())
            return;

        Element elm = root;

        for (String it : NamePart)
        {
            if (!(elm instanceof Set))
                continue;

            Set s = (Set) elm;

            Element ne = s.find(it);
            if (ne != null)
            {
                elm = ne;
                continue;
            }

            resolvedValue = null;
            resolved = true;
            return;
        }

        resolvedValue = elm;
        resolved = true;

        if (!elm.isResolved())
            elm.resolve(root);

        resolvedValue = elm.resolvedValue();
    }

    @Override
    public Element simplify()
    {
        if (resolved && resolvedValue != null)
            return resolvedValue;

        return this;
    }
}