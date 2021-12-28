package dev.gigaherz.util.gddl2.util;

import java.util.*;
import java.util.function.Supplier;

/**
 * Minimal naive implementation of a map that allows multiple values for the same key.
 *
 * @param <TKey>   The type of the keys
 * @param <TValue> The type of the values
 */
public class MultiMap<TKey, TValue>
{
    private final Map<TKey, Collection<TValue>> storage;
    private final Supplier<Collection<TValue>> collectionFactory;

    public MultiMap()
    {
        this(HashMap::new, HashSet::new);
    }

    public MultiMap(Supplier<Map<TKey, Collection<TValue>>> storageFactory, Supplier<Collection<TValue>> collectionFactory)
    {
        this.storage = storageFactory.get();
        this.collectionFactory = collectionFactory;
    }

    private Optional<Collection<TValue>> getOrEmpty(TKey key)
    {
        return Optional.ofNullable(storage.get(key));
    }

    private Collection<TValue> getOrCreate(TKey key)
    {
        return storage.computeIfAbsent(key, _key -> collectionFactory.get());
    }

    public Collection<TValue> get(TKey key)
    {
        return getOrEmpty(key).orElseGet(Collections::emptySet);
    }

    private boolean contains(TKey key, TValue value)
    {
        return getOrEmpty(key).map(collection -> collection.contains(value)).orElse(false);
    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean remove(TKey key, TValue value)
    {
        return getOrEmpty(key).map(collection -> collection.remove(value)).orElse(false);
    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean put(TKey key, TValue value)
    {
        if (contains(key, value))
            return false;
        return getOrCreate(key).add(value);
    }

    public void clear()
    {
        storage.clear();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MultiMap<?, ?> multiMap = (MultiMap<?, ?>) o;
        return storage.equals(multiMap.storage);
    }

    @Override
    public int hashCode()
    {
        return storage.hashCode();
    }

    public int size()
    {
        return storage.size();
    }
}