package gigaherz.util.gddl.structure;

import gigaherz.util.gddl.Lexer;
import gigaherz.util.gddl.config.StringGenerationContext;
import gigaherz.util.gddl.config.StringGenerationOptions;

import java.util.*;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class Set extends Element implements List<Element>
{
    private final List<Element> contents = new ArrayList<>();
    private final Map<String, Element> names = new HashMap<>();

    private String typeName; // TODO: getter/setter

    public Set()
    {
    }

    public Set(Collection<Element> init)
    {
        contents.addAll(init);
    }

    public boolean hasTypeName()
    {
        return typeName != null;
    }

    public String getTypeName()
    {
        return typeName;
    }

    public void setTypeName(String value)
    {
        if(!Lexer.isValidIdentifier(value))
            throw new IllegalArgumentException("Type value must be a valid identifier");
        typeName = value;
    }

    @Override
    public Element get(int index)
    {
        return contents.get(index);
    }

    @Override
    public Element set(int index, Element value)
    {
        Element old = contents.get(index);
        if (old.hasName())
            names.remove(old.getName());
        contents.set(index, value);
        if (value.hasName())
            names.put(value.getName(), value);
        return old;
    }

    @Override
    public int size()
    {
        return contents.size();
    }

    @Override
    public boolean isEmpty()
    {
        return contents.isEmpty();
    }

    @Override
    public boolean contains(Object o)
    {
        return contents.contains(o);
    }

    @Override
    public Iterator<Element> iterator()
    {
        return contents.iterator();
    }

    @Override
    public Object[] toArray()
    {
        return contents.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a)
    {
        return contents.toArray(a);
    }

    @Override
    public boolean add(Element e)
    {
        contents.add(e);
        if (e.hasName())
            names.put(e.getName(), e);
        return true;
    }

    @Override
    public boolean containsAll(Collection<?> c)
    {
        return contents.containsAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c)
    {
        boolean changed = false;
        for (Object e : c)
            changed = changed || remove(e);
        return changed;
    }

    @Override
    public boolean retainAll(Collection<?> c)
    {
        boolean changed = false;
        for (Iterator<Element> it = contents.iterator(); it.hasNext(); )
        {
            Element e = it.next();
            if (!c.contains(e))
            {
                it.remove();
                if (e.hasName())
                    names.remove(e.getName());
                changed = true;
            }
        }
        return changed;
    }

    @Override
    public void add(int before, Element e)
    {
        contents.add(before, e);
        if (e.hasName())
            names.put(e.getName(), e);
    }

    @Override
    public boolean addAll(Collection<? extends Element> c)
    {
        boolean changed = false;
        for (Element e : c)
        {
            changed = changed || add(e);
        }
        return changed;
    }

    @Override
    public boolean addAll(int index, Collection<? extends Element> c)
    {
        boolean changed = false;
        for (Element e : c)
        {
            add(index++, e);
            changed = true;
        }
        return changed;
    }

    @Override
    public boolean remove(Object o)
    {
        if (!(o instanceof Element))
            return false;
        Element e = (Element) o;
        boolean r = contents.remove(e);
        if (e.hasName())
            names.remove(e.getName());
        return r;
    }

    @Override
    public Element remove(int index)
    {
        Element e = contents.get(index);
        contents.remove(index);
        if (e.hasName())
            names.remove(e.getName());
        return e;
    }

    @Override
    public int indexOf(Object o)
    {
        return contents.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o)
    {
        return contents.lastIndexOf(o);
    }

    @Override
    public ListIterator<Element> listIterator()
    {
        return contents.listIterator();
    }

    @Override
    public ListIterator<Element> listIterator(int index)
    {
        return contents.listIterator(index);
    }

    @Override
    public List<Element> subList(int fromIndex, int toIndex)
    {
        return contents.subList(fromIndex, toIndex);
    }

    @Override
    public void clear()
    {
        contents.clear();
        names.clear();
    }

    public boolean isSimple()
    {
        return contents.stream().noneMatch(a -> a instanceof Set || a.hasName());
    }

    @Override
    protected String toStringInternal(StringGenerationContext ctx)
    {
        boolean addBraces = ctx.IndentLevel > 0;
        int tabsToGen = ctx.IndentLevel - 1;

        String tabs1 = "";
        for (int i = 0; i < tabsToGen; i++)
        {
            tabs1 += "  ";
        }

        final String tabs2 = addBraces ? "  " + tabs1 : tabs1;

        StringBuilder builder = new StringBuilder();

        boolean _nice = (ctx.Options == StringGenerationOptions.Nice);
        boolean _simple = (isSimple() && contents.size() <= 10);

        int verbosity = 0;
        if(_nice && _simple) verbosity = 1;
        else if(_nice) verbosity = 2;

        ctx.IndentLevel++;

        if (hasTypeName())
        {
            builder.append(typeName);
            builder.append(" ");
        }
        if(addBraces)
        {
            switch(verbosity)
            {
                case 0: builder.append("{"); break;
                case 1: builder.append("{ "); break;
                case 2: builder.append("{\n"); break;
            }
        }

        boolean first = true;
        for(Element e : contents)
        {
            if(!first)
            {
                switch(verbosity)
                {
                    case 0: builder.append(","); break;
                    case 1: builder.append(", "); break;
                    case 2: builder.append(",\n"); break;
                }
            }
            if(verbosity == 2) builder.append(tabs2);

            builder.append(e.toString(ctx));

            first = false;
        }

        if (addBraces)
        {
            switch(verbosity)
            {
                case 0: builder.append("}"); break;
                case 1: builder.append(" }"); break;
                case 2: builder.append("\n"); builder.append(tabs1); builder.append("}"); break;
            }
        }

        ctx.IndentLevel--;

        return builder.toString();
    }

    @Override
    protected Element copy()
    {
        Set b = new Set();
        copyTo(b);
        return b;
    }

    @Override
    protected void copyTo(Element other)
    {
        super.copyTo(other);
        if(!(other instanceof Set))
            throw new IllegalArgumentException("copyTo for invalid type");
        Set b = (Set)other;
        for(Element e : contents) b.add(e.copy());
    }

    @Override
    public void resolve(Element root, Element parent)
    {
        for (Element el : contents)
        {
            el.resolve(root, this);
        }
    }

    @Override
    public Element simplify()
    {
        for (int i = 0; i < contents.size(); i++)
        {
            contents.set(i, contents.get(i).simplify());
        }

        return this;
    }

    public Element find(String name)
    {
        return names.get(name);
    }

    public Stream<Element> byName(String elementName)
    {
        return contents.stream().filter(t -> t.hasName() && t.getName().equals(elementName));
    }

    public Stream<Set> byType(String typeName)
    {
        return contents.stream().filter(t -> t instanceof Set).map(t -> (Set) t).filter(e -> e.typeName.equals(typeName));
    }
}