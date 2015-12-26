package gigaherz.utils.GDDL.structure;

import gigaherz.utils.GDDL.Utility;
import gigaherz.utils.GDDL.config.StringGenerationContext;
import gigaherz.utils.GDDL.config.StringGenerationOptions;

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
        //return !(contents.Any(a => a is Set) || contents.Where(a => a.hasName()).Any(a => a is Set));
        return false;
    }

    protected String toStringInternal()
    {
        if (hasTypeName())
            return String.format("%s %s", typeName, toStringInternal(true));
        return toStringInternal(true);
    }

    protected String toStringInternal(boolean addBraces)
    {
        StringBuilder b = new StringBuilder();
        if (addBraces) b.append("{");
        boolean first = true;
        for (Element e : contents)
        {
            if (!first) b.append(", ");
            b.append(e.toString());
            first = false;
        }
        if (addBraces) b.append("}");
        return b.toString();
    }

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

        boolean nice = (ctx.Options == StringGenerationOptions.Nice) && (!isSimple() || contents.size() > 10);

        ctx.IndentLevel++;

        String result = nice
                ? Utility.joinStream(",\n", contents.stream().map(a -> tabs2 + a.toString(ctx)))
                : Utility.joinStream(", ", contents.stream().map(a -> a.toString(ctx)));

        if (addBraces)
        {
            result = String.format(nice ? "{{%n{2}%n%s}}" : "{{{2}}}", tabs1, result);
        }

        if (hasTypeName())
        {
            result = String.format("%s %s", typeName, result);
        }

        ctx.IndentLevel--;

        return result;
    }

    public void resolve(Element root)
    {
        for (Element el : contents)
        {
            el.resolve(root);
        }
    }

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