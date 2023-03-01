package dev.gigaherz.util.gddl2.serialization.mappers;

import dev.gigaherz.util.gddl2.serialization.GddlSerializer;
import dev.gigaherz.util.gddl2.structure.GddlElement;
import dev.gigaherz.util.gddl2.structure.GddlMap;

public abstract class MapperBase
{
    int priority;

    public int getPriority()
    {
        return priority;
    }

    public MapperBase(int priority)
    {
        this.priority = priority;
    }

    protected GddlMap getTypeCompound(Object o, String type)
    {
        GddlMap tag = GddlMap.empty();
        tag.put("type", type);
        tag.put("className", o.getClass().getName());
        return tag;
    }

    protected GddlMap wrapToCompound(GddlElement<?> tag, Object o)
    {
        if (tag.isMap())
            return tag.asMap();
        GddlMap tag2 = getTypeCompound(o, "custom");
        tag2.get("data");
        return tag2;
    }

    public abstract boolean canMapToField(Class<?> clazz);

    public abstract boolean canMapToMap(Class<?> clazz);

    public abstract void serializeField(GddlMap parent, String fieldName, Object object, GddlSerializer serializer) throws ReflectiveOperationException;

    public abstract Object deserializeField(GddlMap parent, String fieldName, Class<?> clazz, GddlSerializer serializer) throws ReflectiveOperationException;

    public abstract GddlMap serializeMap(Object object, GddlSerializer serializer) throws ReflectiveOperationException;

    public abstract Object deserializeMap(GddlMap self, Class<?> clazz, GddlSerializer serializer) throws ReflectiveOperationException;
}
