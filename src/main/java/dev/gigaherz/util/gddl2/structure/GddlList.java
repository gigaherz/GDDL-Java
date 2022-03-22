package dev.gigaherz.util.gddl2.structure;

import dev.gigaherz.util.gddl2.util.Range;
import dev.gigaherz.util.gddl2.util.Utility;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public final class GddlList extends GddlElement<GddlList> implements List<GddlElement<?>>
{
    //region API
    public static GddlList empty()
    {
        return new GddlList();
    }

    public static GddlList of(GddlElement<?>... initial)
    {
        return new GddlList(Arrays.asList(initial));
    }

    public static GddlList copyOf(java.util.Collection<GddlElement<?>> initial)
    {
        return new GddlList(initial);
    }

    @Override
    public boolean isList()
    {
        return true;
    }

    @Override
    public GddlList asList()
    {
        return this;
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

    @NotNull
    public Stream<GddlMap> byType(String typeName)
    {
        return contents.stream().filter(GddlElement::isMap).map(GddlElement::asMap).filter(e -> e.hasTypeName() && e.getTypeName().equals(typeName));
    }

    @Override
    public boolean add(GddlElement<?> e)
    {
        Objects.requireNonNull(e);

        contents.add(e);
        onAdd(e);
        return true;
    }

    @Override
    public void add(int before, @NotNull GddlElement<?> e)
    {
        Objects.requireNonNull(e);

        contents.add(before, e);
        onAdd(e);
    }

    public boolean remove(GddlElement<?> e)
    {
        boolean r = contents.remove(e);
        onRemove(e);
        return r;
    }

    @Override
    public GddlElement<?> remove(int index)
    {
        GddlElement<?> at = contents.get(index);
        contents.remove(index);
        onRemove(at);
        return at;
    }

    public boolean contains(GddlElement<?> element)
    {
        return contents.contains(element);
    }

    public List<GddlElement<?>> get(Range range)
    {
        int start = range.offset(contents.size());
        int length = range.length(contents.size());
        return contents.subList(start, length);
    }

    @Override
    public GddlElement<?> get(int index)
    {
        return contents.get(index);
    }

    @Override
    public GddlElement<?> set(int index, @NotNull GddlElement<?> e)
    {
        Objects.requireNonNull(e);

        GddlElement<?> old = contents.get(index);
        if (old != e)
        {
            contents.set(index, e);
            onRemove(old);
            onAdd(e);
        }

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
        contents.forEach(this::onRemove);
        contents.clear();
    }

    public int getFormattingComplexity()
    {
        return 2 + contents.stream().mapToInt(GddlElement::getFormattingComplexity).sum();
    }

    public int indexOf(GddlElement<?> o)
    {
        return contents.indexOf(o);
    }

    public int lastIndexOf(GddlElement<?> o)
    {
        return contents.lastIndexOf(o);
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
    public boolean addAll(java.util.Collection<? extends GddlElement<?>> c)
    {
        c.forEach(this::add);
        return !c.isEmpty();
    }

    @Override
    public boolean addAll(int index, java.util.Collection<? extends GddlElement<?>> c)
    {
        for (GddlElement<?> e : c)
        {
            add(index++, e);
        }
        return !c.isEmpty();
    }
    //endregion

    //region Implementation
    private final List<GddlElement<?>> contents = new ArrayList<>();
    private String trailingComment;

    private GddlList()
    {
    }

    private GddlList(java.util.Collection<GddlElement<?>> init)
    {
        this.addAll(init);
    }

    private void onAdd(GddlElement<?> e)
    {
        e.setParent(this);
    }

    private void onRemove(GddlElement<?> e)
    {
        e.setParent(null);
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
        return o instanceof GddlElement && remove((GddlElement<?>) o);
    }

    @Override
    public boolean contains(Object o)
    {
        return o instanceof GddlElement && contains((GddlElement<?>) o);
    }

    @Override
    public int indexOf(Object o)
    {
        return o instanceof GddlElement ? indexOf((GddlElement<?>) o) : -1;
    }

    @Override
    public int lastIndexOf(Object o)
    {
        return o instanceof GddlElement ? lastIndexOf((GddlElement<?>) o) : -1;
    }
    //endregion

    //region Element
    @Override
    protected GddlList copyInternal()
    {
        var collection = new GddlList();
        copyTo(collection);
        return collection;
    }

    @Override
    protected void copyTo(GddlList other)
    {
        super.copyTo(other);
        for (GddlElement<?> e : contents)
        {
            other.add(e.copy());
        }
    }

    @Override
    public void resolve(GddlElement<?> root)
    {
        for (GddlElement<?> el : contents)
        {
            el.resolve(root);
        }
    }

    @Override
    public GddlList simplify()
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
        return equalsImpl((GddlList) other);
    }

    @Override
    public boolean equals(GddlList other)
    {
        if (this == other) return true;
        if (other == null) return false;
        return equalsImpl(other);
    }

    public boolean equalsImpl(@NotNull GddlList other)
    {
        return Utility.listEquals(contents, other.contents);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), contents);
    }

    //endregion

    //region Iterators
    @Override
    @NotNull
    public Iterator<GddlElement<?>> iterator()
    {
        return new Iterator<>()
        {
            private GddlElement<?> current;
            private final Iterator<GddlElement<?>> it = contents.iterator();

            @Override
            public boolean hasNext()
            {
                return it.hasNext();
            }

            @Override
            public GddlElement<?> next()
            {
                current = it.next();
                return current;
            }

            @Override
            public void remove()
            {
                it.remove();
                onRemove(current);
            }
        };
    }

    @Override
    @NotNull
    public ListIterator<GddlElement<?>> listIterator()
    {
        return listIterator(0);
    }

    @Override
    @NotNull
    public ListIterator<GddlElement<?>> listIterator(int index)
    {
        return new ListIterator<>()
        {
            private final ListIterator<GddlElement<?>> lit = contents.listIterator(index);
            private GddlElement<?> current = contents.get(index);

            @Override
            public boolean hasNext()
            {
                return lit.hasNext();
            }

            @Override
            public GddlElement<?> next()
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
            public GddlElement<?> previous()
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
            public void set(GddlElement<?> e)
            {
                Objects.requireNonNull(e);

                onRemove(current);
                lit.set(e);
                onAdd(e);
            }

            @Override
            public void add(GddlElement<?> e)
            {
                Objects.requireNonNull(e);

                lit.add(e);
                onAdd(e);
            }
        };
    }

    @Override
    @NotNull
    public List<GddlElement<?>> subList(int fromIndex, int toIndex)
    {
        return Collections.unmodifiableList(contents.subList(fromIndex, toIndex));
    }
    //endregion
}