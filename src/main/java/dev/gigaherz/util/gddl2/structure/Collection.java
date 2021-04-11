package dev.gigaherz.util.gddl2.structure;

import dev.gigaherz.util.gddl2.util.MultiMap;
import dev.gigaherz.util.gddl2.util.Utility;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public final class Collection extends Element<Collection> implements List<Element<?>>
{
    //region API
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

    public Collection()
    {
    }

    public Collection(java.util.Collection<Element<?>> init)
    {
        this.addAll(init);
    }

    public boolean hasTrailingComment()
    {
        return !Utility.isNullOrEmpty(trailingComment);
    }

    public String getTrailingComment()
    {
        return trailingComment;
    }

    public void setTrailingComment(String trailingComment)
    {
        this.trailingComment = trailingComment;
    }

    public boolean hasTypeName()
    {
        return !Utility.isNullOrEmpty(typeName);
    }

    public String getTypeName()
    {
        return typeName;
    }

    @NotNull
    public Collection withTypeName(String value)
    {
        if (!Utility.isValidIdentifier(value))
            throw new IllegalArgumentException("Type value must be a valid identifier");
        typeName = value;
        return this;
    }

    @NotNull
    public Optional<Element<?>> get(String name)
    {
        return names.get(name).stream().findFirst();
    }

    @NotNull
    public Stream<Element<?>> byName(String elementName)
    {
        return names.get(elementName).stream();
    }

    @NotNull
    public Stream<Collection> byType(String typeName)
    {
        return contents.stream().filter(t -> t instanceof Collection).map(t -> (Collection) t).filter(e -> e.typeName.equals(typeName));
    }

    @Override
    public boolean add(Element<?> e)
    {
        Objects.requireNonNull(e);

        contents.add(e);
        onAdd(e);
        return true;
    }

    @Override
    public void add(int before, @NotNull Element<?> e)
    {
        Objects.requireNonNull(e);

        contents.add(before, e);
        onAdd(e);
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

    public boolean contains(Element<?> element)
    {
        return contents.contains(element);
    }

    @Override
    public Element<?> get(int index)
    {
        return contents.get(index);
    }

    @Override
    public Element<?> set(int index, @NotNull Element<?> e)
    {
        Objects.requireNonNull(e);

        Element<?> old = contents.get(index);
        if (old == e)
            return old;

        onRemove(old);
        contents.set(index, e);
        onAdd(e);
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

    public int indexOf(Element<?> o)
    {
        return contents.indexOf(o);
    }

    public int lastIndexOf(Element<?> o)
    {
        return contents.lastIndexOf(o);
    }

    public Collection withName(String name)
    {
        super.withName(name);
        return this;
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


    public boolean hasNames()
    {
        return names.size() > 0;
    }
    //endregion

    //region Implementation
    private final List<Element<?>> contents = new ArrayList<>();
    private final MultiMap<String, Element<?>> names = new MultiMap<>();
    private String trailingComment;

    private String typeName;

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

    /*package-private*/ void setName(@NotNull Element<?> e, String name)
    {
        String currentName = e.getName();
        if (!Objects.equals(currentName, name))
        {
            onRemove(e);
            e.setName(name);
            onAdd(e);
        }
    }
    //endregion

    //region Other List<> Methods
    @Override
    @NotNull
    public Object @NotNull [] toArray()
    {
        return contents.toArray();
    }

    @Override
    @NotNull
    public <T> T @NotNull [] toArray(@NotNull T @NotNull [] a)
    {
        //noinspection SuspiciousToArrayCall
        return contents.toArray(a);
    }

    @Override
    public boolean remove(Object o)
    {
        return o instanceof Element && remove((Element<?>) o);
    }

    @Override
    public boolean contains(Object o)
    {
        return o instanceof Element && contains((Element<?>) o);
    }

    @Override
    public int indexOf(Object o)
    {
        return o instanceof Element ? indexOf((Element<?>) o) : -1;
    }

    @Override
    public int lastIndexOf(Object o)
    {
        return o instanceof Element ? lastIndexOf((Element<?>) o) : -1;
    }
    //endregion

    //region Element
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
    //endregion

    //region Equality
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
        if (other == null) return false;
        return equalsImpl(other);
    }

    @Override
    public boolean equalsImpl(@NotNull Collection other)
    {
        return super.equalsImpl(other) &&
                contents.equals(other.contents) &&
                names.equals(other.names) &&
                Objects.equals(typeName, other.typeName);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), contents, names, typeName);
    }

    public boolean namedEquals(Element<?> other)
    {
        if (this == other) return true;
        if (other == null) return false;
        return other.isCollection() && namedEqualsImpl(other.asCollection());
    }

    public boolean namedEquals(Collection other)
    {
        if (this == other) return true;
        if (other == null) return false;
        return namedEqualsImpl(other);
    }

    private boolean namedEqualsImpl(@NotNull Collection other)
    {
        return super.equalsImpl(other) &&
                names.equals(other.names) &&
                Objects.equals(typeName, other.typeName);
    }
    //endregion

    //region Iterators
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
            public void set(Element<?> e)
            {
                Objects.requireNonNull(e);

                onRemove(current);
                lit.set(e);
                onAdd(e);
            }

            @Override
            public void add(Element<?> e)
            {
                Objects.requireNonNull(e);

                lit.add(e);
                onAdd(e);
            }
        };
    }

    @Override
    @NotNull
    public List<Element<?>> subList(int fromIndex, int toIndex)
    {
        return Collections.unmodifiableList(contents.subList(fromIndex, toIndex));
    }
    //endregion
}