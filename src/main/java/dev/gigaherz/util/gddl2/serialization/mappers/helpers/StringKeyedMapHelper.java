package dev.gigaherz.util.gddl2.serialization.mappers.helpers;

import dev.gigaherz.util.gddl2.serialization.GddlSerializer;
import dev.gigaherz.util.gddl2.structure.GddlMap;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class StringKeyedMapHelper<T>
{
    private final Supplier<Map<String, T>> mapFactory;

    public StringKeyedMapHelper()
    {
        this(HashMap::new);
    }

    public StringKeyedMapHelper(Supplier<Map<String, T>> mapFactory)
    {
        this.mapFactory = mapFactory;
    }

    public GddlMap serialize(Map<String, T> object, GddlSerializer serializer)
            throws ReflectiveOperationException
    {
        GddlMap map = GddlMap.empty();
        for (Map.Entry<String, T> e : object.entrySet())
        {
            map.put(e.getKey(), serializer.serializeVerbose(e.getValue()));
        }
        return map;
    }

    public Map<String, T> deserialize(GddlMap map, GddlSerializer serializer)
            throws ReflectiveOperationException
    {
        var outMap = mapFactory.get();
        for (var entry : map.entrySet())
        {
            outMap.put(entry.getKey(), serializer.deserializeVerbose(entry.getValue()));
        }
        return outMap;
    }
}
