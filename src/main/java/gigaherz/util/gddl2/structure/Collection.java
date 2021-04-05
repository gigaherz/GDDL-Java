package gigaherz.util.gddl2.structure;

import gigaherz.util.gddl2.util.MultiMap;
import gigaherz.util.gddl2.util.Utility;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public final class Collection extends Element<Collection> implements List<Element<?>>
{
    // Factory methods
    public static Collection empty()
    {
        return new Collection();
    }

    public static Collection of(Element<?>... initial)
    {
        return new Collection(Arrays.asList(initial));
    }

    public static Collection copyOf(java.util.Collection<Element<?>> initial)
    {
        return new Collection(initial);
    }

    // Implementation
    private final List<Element<?>> contents = new ArrayList<>();
    private final MultiMap<String, Element<?>> names = new MultiMap<>();

    private String typeName;

    private Collection()
    {
    }

    private Collection(java.util.Collection<Element<?>> init)
    {
        this.addAll(init);
    }

    private void onAdd(Element<?> e)
    {
        if (e.hasName())
            names.put(e.getName(), e);
        e.setParentInternal(this);
    }

    private void onRemove(Element<?> e)
    {
        if (e.hasName())
            names.remove(e.getName(), e);
        e.setParentInternal(null);
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
        if (!Utility.isValidIdentifier(value))
            throw new IllegalArgumentException("Type value must be a valid identifier");
        typeName = value;
        return this;
    }

    @Override
    public Element<?> get(int index)
    {
        return contents.get(index);
    }

    @Override
    public Element<?> set(int index, Element value)
    {
        Element<?> old = contents.get(index);
        if (old == value)
            return old;

        onRemove(old);
        contents.set(index, value);
        onAdd(value);
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
    @NotNull
    public Object[] toArray()
    {
        return contents.toArray();
    }

    @Override
    @NotNull
    public <T> T[] toArray(@NotNull T[] a)
    {
        //noinspection SuspiciousToArrayCall
        return contents.toArray(a);
    }

    @Override
    public boolean add(Element e)
    {
        contents.add(e);
        onAdd(e);
        return true;
    }

    @Override
    public boolean containsAll(@NotNull java.util.Collection<?> c)
    {
        return contents.containsAll(c);
    }

    @Override
    public boolean removeAll(java.util.Collection<?> c)
    {
        return this.removeIf(c::contains);
    }

    @Override
    public boolean retainAll(@NotNull java.util.Collection<?> c)
    {
        return this.removeIf(Predicate.not(c::contains));
    }

    @Override
    public void add(int before, Element e)
    {
        contents.add(before, e);
        onAdd(e);
    }

    @Override
    public boolean addAll(java.util.Collection<? extends Element<?>> c)
    {
        c.forEach(this::add);
        return !c.isEmpty();
    }

    @Override
    public boolean addAll(int index, java.util.Collection<? extends Element<?>> c)
    {
        for (Element<?> e : c)
        {
            add(index++, e);
        }
        return !c.isEmpty();
    }

    @Override
    public boolean remove(Object o)
    {
        return o instanceof Element && remove((Element<?>) o);
    }

    public boolean remove(Element<?> e)
    {
        boolean r = contents.remove(e);
        onRemove(e);
        return r;
    }

    @Override
    public Element<?> remove(int index)
    {
        Element<?> e = contents.get(index);
        contents.remove(index);
        onRemove(e);
        return e;
    }

    public void setName(Element<?> e, String name)
    {
        String currentName = e.getName();
        if (!Objects.equals(currentName, name))
        {
            onRemove(e);
            e.setName(name);
            onAdd(e);
        }
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
    public void clear()
    {
        contents.forEach(e -> e.setParentInternal(null));
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
    protected Collection copyInternal()
    {
        var collection = new Collection();
        copyTo(collection);
        return collection;
    }

    @Override
    protected void copyTo(Collection other)
    {
        super.copyTo(other);
        for (Element<?> e : contents)
        {
            other.add(e.copyInternal());
        }
    }

    @Override
    public void resolve(Element<?> root, @Nullable Collection parent)
    {
        for (Element<?> el : contents)
        {
            el.resolve(root, this);
        }
    }

    @Override
    public Collection simplify()
    {
        for (int i = 0; i < contents.size(); i++)
        {
            contents.set(i, contents.get(i).simplify());
        }

        return this;
    }

    public Optional<Element<?>> get(String name)
    {
        return names.get(name).stream().findFirst();
    }

    public Stream<Element<?>> byName(String elementName)
    {
        return names.get(elementName).stream();
    }

    public Stream<Collection> byType(String typeName)
    {
        return contents.stream().filter(t -> t instanceof Collection).map(t -> (Collection) t).filter(e -> e.typeName.equals(typeName));
    }

    @Override
    public boolean equals(Object other)
    {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        return equalsImpl((Collection) other);
    }

    @Override
    public boolean equals(Collection other)
    {
        if (this == other) return true;
        if (null == other) return false;
        return equalsImpl(other);
    }

    @Override
    public boolean equalsImpl(@NotNull Collection collection)
    {
        return super.equalsImpl(collection) &&
                contents.equals(collection.contents) &&
                names.equals(collection.names) &&
                Objects.equals(typeName, collection.typeName);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), contents, names, typeName);
    }

    @Override
    @NotNull
    public Iterator<Element<?>> iterator()
    {
        return new Iterator<>()
        {
            private Element<?> current;
            private final Iterator<Element<?>> it = contents.iterator();

            @Override
            public boolean hasNext()
            {
                return it.hasNext();
            }

            @Override
            public Element<?> next()
            {
                current = it.next();
                return current;
            }

            @Override
            public void remove()
            {
                onRemove(current);
            }
        };
    }

    @Override
    @NotNull
    public ListIterator<Element<?>> listIterator()
    {
        return listIterator(0);
    }

    @Override
    @NotNull
    public ListIterator<Element<?>> listIterator(int index)
    {
        return new ListIterator<>()
        {
            private final ListIterator<Element<?>> lit = contents.listIterator(index);
            private Element<?> current = contents.get(index);

            @Override
            public boolean hasNext()
            {
                return lit.hasNext();
            }

            @Override
            public Element<?> next()
            {
                current = lit.next();
                return current;
            }

            @Override
            public boolean hasPrevious()
            {
                return lit.hasPrevious();
            }

            @Override
            public Element<?> previous()
            {
                current = lit.previous();
                return current;
            }

            @Override
            public int nextIndex()
            {
                return lit.nextIndex();
            }

            @Override
            public int previousIndex()
            {
                return lit.previousIndex();
            }

            @Override
            public void remove()
            {
                lit.remove();
                onRemove(current);
            }

            @Override
            public void set(Element element)
            {
                onRemove(current);
                lit.set(element);
                onAdd(element);
            }

            @Override
            public void add(Element element)
            {
                lit.add(element);
                onAdd(element);
            }
        };
    }

    @Override
    @NotNull
    public List<Element<?>> subList(int fromIndex, int toIndex)
    {
        return Collections.unmodifiableList(contents.subList(fromIndex, toIndex));
    }
}