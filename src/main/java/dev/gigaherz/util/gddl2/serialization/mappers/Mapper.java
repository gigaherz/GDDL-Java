package dev.gigaherz.util.gddl2.serialization.mappers;

import dev.gigaherz.util.gddl2.serialization.GddlSerializer;
import dev.gigaherz.util.gddl2.structure.GddlElement;
import dev.gigaherz.util.gddl2.structure.GddlMap;

public abstract class Mapper
{
    int priority;

    public int getPriority()
    {
        return priority;
    }

    public Mapper(int priority)
    {
        this.priority = priority;
    }

    public abstract boolean canApply(Class<?> clazz);

    protected static GddlMap makeVerboseMap(Object object)
    {
        var map = GddlMap.empty();
        map.put("class", object.getClass().getName());
        return map;
    }

    protected GddlMap wrapVerbose(Object object, GddlElement<?> element)
    {
        var map = makeVerboseMap(object);
        map.put("value", element);
        return map;
    }

    protected GddlElement<?> unwrapVerbose(GddlMap map)
    {
        return map.get("value");
    }

    public abstract GddlElement<?> serialize(Object object, GddlSerializer serializer) throws ReflectiveOperationException;

    public abstract GddlElement<?> serializeVerbose(Object object, GddlSerializer serializer) throws ReflectiveOperationException;

    public abstract Object deserialize(GddlElement<?> element, Class<?> clazz, GddlSerializer serializer) throws ReflectiveOperationException;

    public abstract Object deserializeVerbose(GddlMap map, Class<?> clazz, GddlSerializer serializer) throws ReflectiveOperationException;
}
