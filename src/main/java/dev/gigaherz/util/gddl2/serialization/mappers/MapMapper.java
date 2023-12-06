package dev.gigaherz.util.gddl2.serialization.mappers;

import dev.gigaherz.util.gddl2.serialization.GddlSerializationException;
import dev.gigaherz.util.gddl2.serialization.GddlSerializer;
import dev.gigaherz.util.gddl2.structure.GddlElement;
import dev.gigaherz.util.gddl2.structure.GddlList;
import dev.gigaherz.util.gddl2.structure.GddlMap;

import java.util.Map;

@SuppressWarnings({"unchecked", "rawtypes"})
public class MapMapper extends Mapper
{
    public MapMapper(int priority)
    {
        super(priority);
    }

    @Override
    public boolean canApply(Class<?> clazz)
    {
        return Map.class.isAssignableFrom(clazz);
    }

    @Override
    public GddlMap serialize(Object object, GddlSerializer serializer)
            throws ReflectiveOperationException
    {
        return serializeMap((Map) object, serializer);
    }

    @Override
    public GddlElement<?> serializeVerbose(Object object, GddlSerializer serializer) throws ReflectiveOperationException
    {
        return serialize(object, serializer);
    }

    @Override
    public Object deserialize(GddlElement<?> element, Class<?> clazz, GddlSerializer serializer)
            throws ReflectiveOperationException
    {
        return deserializeMap(element.asMap(), (Class<? extends Map>) clazz, serializer);
    }

    @Override
    public Object deserializeVerbose(GddlMap map, Class<?> clazz, GddlSerializer serializer) throws ReflectiveOperationException
    {
        return deserialize(map, clazz, serializer);
    }

    private GddlMap serializeMap(Map<Object, Object> object, GddlSerializer serializer)
            throws ReflectiveOperationException
    {
        GddlMap map = makeVerboseMap(object);
        GddlList list = GddlList.empty();
        for (Map.Entry e : object.entrySet())
        {
            var entryMap = GddlMap.empty();
            var key = e.getKey();
            var value = e.getValue();
            entryMap.put("key", serializer.serializeVerbose(key));
            entryMap.put("value", serializer.serializeVerbose(value));
            list.add(entryMap);
        }
        map.put("elements", list);
        return map;
    }

    private Map deserializeMap(GddlMap map, Class<? extends Map> clazz, GddlSerializer serializer)
            throws ReflectiveOperationException
    {
        Class<?> actual = Class.forName(map.getString("class"));
        if (!clazz.isAssignableFrom(actual))
            throw new GddlSerializationException();

        Map m = (Map) actual.getDeclaredConstructor().newInstance();

        var elements = map.getList("elements");
        for (var entry : elements)
        {
            var entryMap = entry.asMap();

            var key = serializer.deserializeVerbose(entryMap.get("key"));
            var value = serializer.deserializeVerbose(entryMap.get("value"));

            m.put(key, value);
        }

        return m;
    }
}
