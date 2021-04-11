package dev.gigaherz.util.gddl2.structure;

import dev.gigaherz.util.gddl2.util.Utility;
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
        return Optional.ofNullable(names.get(name));
    }

    @NotNull
    public Stream<Map.Entry<String, GddlMap>> byType(String typeName)
    {
        return names.entrySet().stream()
                .filter(e -> e.getValue().isMap())
                .map(e -> Map.entry(e.getKey(), e.getValue().asMap()))
                .filter(e -> e.getValue().hasTypeName() && e.getValue().getTypeName().equals(typeName));
    }

    @Override
    public GddlElement<?> put(String key, GddlElement<?> value)
    {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);

        var previous = names.put(key, value);
        onAdd(value);
        if (previous != null && previous != value)
            onRemove(previous);
        return previous;
    }

    @Override
    public void putAll(@NotNull Map<? extends String, ? extends GddlElement<?>> m)
    {

    }

    public GddlElement<?> remove(String key)
    {
        GddlElement<?> e = names.remove(key);
        if (e != null)
            onRemove(e);
        return e;
    }

    public boolean remove(String key, GddlElement<?> e)
    {
        boolean r = names.remove(key, e);
        if (r) onRemove(e);
        return r;
    }

    public boolean containsKey(String key)
    {
        return names.containsKey(key);
    }

    public boolean containsValue(GddlElement<?> element)
    {
        return names.containsValue(element);
    }

    public GddlElement<?> get(String key)
    {
        return names.get(key);
    }

    @Override
    public int size()
    {
        return names.size();
    }

    @Override
    public boolean isEmpty()
    {
        return names.isEmpty();
    }

    @Override
    public void clear()
    {
        names.values().forEach(e -> e.setParent(null));
        names.clear();
    }

    @NotNull
    @Override
    public Set<String> keySet()
    {
        return names.keySet();
    }

    @NotNull
    @Override
    public Collection<GddlElement<?>> values()
    {
        return names.values();
    }

    @NotNull
    @Override
    public Set<Entry<String, GddlElement<?>>> entrySet()
    {
        return names.entrySet();
    }

    public int getFormattingComplexity()
    {
        return 2 + names.values().stream().mapToInt(i -> 3 * i.getFormattingComplexity()).sum();
    }

    public Stream<String> getKeys(GddlElement<?> value)
    {
        return names.entrySet().stream().filter(kv -> kv.getValue() == value).map(kv -> kv.getKey());
    }
    //endregion

    //region Implementation
    private final Map<String, GddlElement<?>> names = new HashMap<>();
    private String trailingComment;

    private String typeName;

    private GddlMap()
    {
    }

    private GddlMap(List<Map.Entry<String, GddlElement<?>>> entries)
    {
        for(Map.Entry<String, GddlElement<?>> entry : entries)
            names.put(entry.getKey(), entry.getValue());
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
        return names.containsKey(key);
    }

    /**
     * @deprecated Use {@link GddlMap#containsValue(GddlElement)}
     */
    @Deprecated
    @Override
    public boolean containsValue(Object value)
    {
        return names.containsValue(value);
    }

    /**
     * @deprecated Use {@link GddlMap#get(String)}
     */
    @Deprecated
    @Override
    public GddlElement<?> get(Object key)
    {
        return null;
    }

    /**
     * @deprecated Use {@link GddlMap#remove(String)}
     */
    @Deprecated
    @Override
    public GddlElement<?> remove(Object key)
    {
        return null;
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
        for (Map.Entry<String, GddlElement<?>> e : names.entrySet())
        {
            other.put(e.getKey(), e.getValue().copy());
        }
    }

    @Override
    public void resolve(GddlElement<?> root)
    {
        for (GddlElement<?> el : names.values())
        {
            el.resolve(root);
        }
    }

    @Override
    public GddlMap simplify()
    {
        for(Map.Entry<String, GddlElement<?>> entry : names.entrySet())
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
        return names.equals(other.names) &&
                Objects.equals(typeName, other.typeName);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), names, typeName);
    }

    //endregion
}