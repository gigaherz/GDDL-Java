package gigaherz.util.gddl2.structure;

import gigaherz.util.gddl2.Lexer;
import gigaherz.util.gddl2.util.MultiMap;

import java.util.*;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class Collection extends Element implements List<Element>
{
    // Factory methods
    public static Collection empty()
    {
        return new Collection();
    }

    public static Collection of(Element... initial)
    {
        return new Collection(Arrays.asList(initial));
    }

    public static Collection copyOf(java.util.Collection<Element> initial)
    {
        return new Collection(initial);
    }

    // Implementation
    private final List<Element> contents = new ArrayList<>();
    private final MultiMap<String, Element> names = new MultiMap<>();

    private String typeName;

    private Collection()
    {
    }

    private Collection(java.util.Collection<Element> init)
    {
        this.addAll(init);
    }

    public boolean hasTypeName()
    {
        return typeName != null;
    }

    public String getTypeName()
    {
        return typeName;
    }

    public Collection withTypeName(String value)
    {
        if (!Lexer.isValidIdentifier(value))
            throw new IllegalArgumentException("Type value must be a valid identifier");
        typeName = value;
        return this;
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
            names.remove(old.getName(), old);
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
    public boolean containsAll(java.util.Collection<?> c)
    {
        return contents.containsAll(c);
    }

    @Override
    public boolean removeAll(java.util.Collection<?> c)
    {
        boolean changed = false;
        for (Object e : c)
        { changed = changed || remove(e); }
        return changed;
    }

    @Override
    public boolean retainAll(java.util.Collection<?> c)
    {
        boolean changed = false;
        for (Iterator<Element> it = contents.iterator(); it.hasNext(); )
        {
            Element e = it.next();
            if (!c.contains(e))
            {
                it.remove();
                if (e.hasName())
                    names.remove(e.getName(), e);
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
    public boolean addAll(java.util.Collection<? extends Element> c)
    {
        c.forEach(this::add);
        return !c.isEmpty();
    }

    @Override
    public boolean addAll(int index, java.util.Collection<? extends Element> c)
    {
        for (Element e : c)
        {
            add(index++, e);
        }
        return !c.isEmpty();
    }

    @Override
    public boolean remove(Object o)
    {
        if (!(o instanceof Element))
            return false;
        Element e = (Element) o;
        boolean r = contents.remove(e);
        if (e.hasName())
            names.remove(e.getName(), e);
        return r;
    }

    @Override
    public Element remove(int index)
    {
        Element e = contents.get(index);
        contents.remove(index);
        if (e.hasName())
            names.remove(e.getName(), e);
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
        return contents.stream().noneMatch(a -> a instanceof Collection || a.hasName());
    }

    public Collection withName(String name)
    {
        super.withName(name);
        return this;
    }

    @Override
    protected Collection copy()
    {
        Collection b = new Collection();
        copyTo(b);
        return b;
    }

    @Override
    protected void copyTo(Element other)
    {
        super.copyTo(other);
        if (!(other instanceof Collection))
            throw new IllegalArgumentException("copyTo for invalid type");
        Collection b = (Collection) other;
        for (Element e : contents) { b.add(e.copy()); }
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

    public Optional<Element> get(String name)
    {
        return names.get(name).stream().findFirst();
    }

    public Stream<Element> byName(String elementName)
    {
        return contents.stream().filter(t -> t.hasName() && t.getName().equals(elementName));
    }

    public Stream<Collection> byType(String typeName)
    {
        return contents.stream().filter(t -> t instanceof Collection).map(t -> (Collection) t).filter(e -> e.typeName.equals(typeName));
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Collection elements = (Collection) o;
        return contents.equals(elements.contents) &&
                names.equals(elements.names) &&
                Objects.equals(typeName, elements.typeName);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), contents, names, typeName);
    }
}