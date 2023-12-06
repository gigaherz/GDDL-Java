package dev.gigaherz.util.gddl2.structure;

import dev.gigaherz.util.gddl2.internal.Utility;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public final class GddlMap extends GddlElement<GddlMap> implements Map<String, GddlElement<?>>
{
    //region API
    public static GddlMap empty()
    {
        return new GddlMap();
    }

    public static GddlMap of(String key, GddlElement<?> value)
    {
        return new GddlMap(List.of(Map.entry(key, value)));
    }

    public static GddlMap of(String key, GddlElement<?> value, String key2, GddlElement<?> value2)
    {
        return new GddlMap(List.of(Map.entry(key, value), Map.entry(key2, value2)));
    }

    public static GddlMap of(String key, GddlElement<?> value, String key2, GddlElement<?> value2, String key3, GddlElement<?> value3)
    {
        return new GddlMap(List.of(Map.entry(key, value), Map.entry(key2, value2), Map.entry(key3, value3)));
    }

    public static GddlMap of(String key, GddlElement<?> value, String key2, GddlElement<?> value2, String key3, GddlElement<?> value3, String key4, GddlElement<?> value4)
    {
        return new GddlMap(List.of(Map.entry(key, value), Map.entry(key2, value2), Map.entry(key3, value3), Map.entry(key4, value4)));
    }

    @SafeVarargs
    public static GddlMap of(Map.Entry<String, GddlElement<?>>... values)
    {
        return new GddlMap(Arrays.asList(values));
    }

    public static GddlMap of(Collection<Map.Entry<String, GddlElement<?>>> values)
    {
        return new GddlMap(values);
    }

    @Override
    public boolean isMap()
    {
        return true;
    }

    @Override
    public GddlMap asMap()
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

    public boolean hasTypeName()
    {
        return !Utility.isNullOrEmpty(typeName);
    }

    public String getTypeName()
    {
        return typeName;
    }

    @NotNull
    public GddlMap withTypeName(String value)
    {
        if (!Utility.isValidIdentifier(value))
            throw new IllegalArgumentException("Type value must be a valid identifier");
        typeName = value;
        return this;
    }

    @NotNull
    public Optional<GddlElement<?>> find(String name)
    {
        return Optional.ofNullable(contents.get(name));
    }

    @Override
    public GddlElement<?> put(String key, GddlElement<?> value)
    {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);

        var previous = contents.put(key, value);
        onAdd(value);
        if (previous != null && previous != value)
            onRemove(previous);
        return previous;
    }

    public GddlElement<?> put(String key, String value)
    {
        return put(key, GddlValue.of(value));
    }

    public GddlElement<?> put(String key, long value)
    {
        return put(key, GddlValue.of(value));
    }

    public GddlElement<?> put(String key, boolean value)
    {
        return put(key, GddlValue.of(value));
    }

    public GddlElement<?> put(String key, double value)
    {
        return put(key, GddlValue.of(value));
    }

    @Override
    public void putAll(@NotNull Map<? extends String, ? extends GddlElement<?>> m)
    {
        for (var entry : m.entrySet())
        {
            put(entry.getKey(), entry.getValue());
        }
    }

    public GddlElement<?> remove(String key)
    {
        GddlElement<?> e = contents.remove(key);
        if (e != null)
            onRemove(e);
        return e;
    }

    public boolean remove(String key, GddlElement<?> e)
    {
        boolean r = contents.remove(key, e);
        if (r) onRemove(e);
        return r;
    }

    public boolean containsKey(String key)
    {
        return contents.containsKey(key);
    }

    public boolean containsValue(GddlElement<?> element)
    {
        return contents.containsValue(element);
    }

    public GddlElement<?> get(String key)
    {
        return contents.get(key);
    }

    public GddlMap getMap(String key)
    {
        return get(key).asMap();
    }

    public GddlList getList(String key)
    {
        return get(key).asList();
    }

    public String getString(String key)
    {
        return get(key).stringValue();
    }

    public byte getByte(String key)
    {
        return get(key).byteValue();
    }

    public short getShort(String key)
    {
        return get(key).shortValue();
    }

    public int getInt(String key)
    {
        return get(key).intValue();
    }

    public long getLong(String key)
    {
        return get(key).longValue();
    }

    public float getFloat(String key)
    {
        return get(key).floatValue();
    }

    public double getDouble(String key)
    {
        return get(key).doubleValue();
    }

    public boolean getBoolean(String key)
    {
        return get(key).booleanValue();
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
        contents.values().forEach(this::onRemove);
        contents.clear();
    }

    @NotNull
    @Override
    public Set<String> keySet()
    {
        return contents.keySet();
    }

    @NotNull
    @Override
    public Collection<GddlElement<?>> values()
    {
        return contents.values();
    }

    @NotNull
    @Override
    public Set<Entry<String, GddlElement<?>>> entrySet()
    {
        return contentsView.entrySet();
    }

    public int getFormattingComplexity()
    {
        return 2 + contents.values().stream().mapToInt(i -> 3 * i.getFormattingComplexity()).sum();
    }

    public Stream<String> keysOf(GddlElement<?> value)
    {
        return contents.entrySet().stream().filter(kv -> kv.getValue() == value).map(Entry::getKey);
    }
    //endregion

    //region Implementation
    private final Map<String, GddlElement<?>> contents = new LinkedHashMap<>();
    private final Map<String, GddlElement<?>> contentsView = Collections.unmodifiableMap(contents);
    private String trailingComment;

    private String typeName;

    private GddlMap()
    {
    }

    private GddlMap(Collection<Map.Entry<String, GddlElement<?>>> entries)
    {
        for (Map.Entry<String, GddlElement<?>> entry : entries)
        {put(entry.getKey(), entry.getValue());}
    }

    private void onAdd(GddlElement<?> e)
    {
        e.setParent(this);
    }

    private void onRemove(GddlElement<?> e)
    {
        e.setParent(null);
    }

    /**
     * @deprecated Use {@link GddlMap#containsKey(String)}
     */
    @Deprecated
    @Override
    public boolean containsKey(Object key)
    {
        return contents.containsKey(key);
    }

    /**
     * @deprecated Use {@link GddlMap#containsValue(GddlElement)}
     */
    @Deprecated
    @Override
    public boolean containsValue(Object value)
    {
        return contents.containsValue(value);
    }

    /**
     * @deprecated Use {@link GddlMap#get(String)}
     */
    @Deprecated
    @Override
    public GddlElement<?> get(Object key)
    {
        return key instanceof String s ? get(s) : null;
    }

    /**
     * @deprecated Use {@link GddlMap#remove(String)}
     */
    @Deprecated
    @Override
    public GddlElement<?> remove(Object key)
    {
        return key instanceof String s ? remove(s) : null;
    }
    //endregion

    //region Element
    @Override
    protected GddlMap copyInternal()
    {
        var collection = new GddlMap();
        copyTo(collection);
        return collection;
    }

    @Override
    protected void copyTo(GddlMap other)
    {
        super.copyTo(other);
        for (Map.Entry<String, GddlElement<?>> e : contents.entrySet())
        {
            other.put(e.getKey(), e.getValue().copy());
        }
    }

    @Override
    public void resolve(GddlElement<?> root)
    {
        for (GddlElement<?> el : contents.values())
        {
            el.resolve(root);
        }
    }

    @Override
    public GddlMap simplify()
    {
        for (Map.Entry<String, GddlElement<?>> entry : contents.entrySet())
        {
            put(entry.getKey(), entry.getValue().simplify());
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
        return equalsImpl((GddlMap) other);
    }

    @Override
    public boolean equals(GddlMap other)
    {
        if (this == other) return true;
        if (other == null) return false;
        return equalsImpl(other);
    }

    public boolean equalsImpl(@NotNull GddlMap other)
    {
        return contents.equals(other.contents) &&
                Objects.equals(typeName, other.typeName);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), contents, typeName);
    }

    //endregion
}