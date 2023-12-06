package dev.gigaherz.util.gddl2.serialization.mappers;

import dev.gigaherz.util.gddl2.serialization.GddlSerializer;
import dev.gigaherz.util.gddl2.structure.GddlElement;
import dev.gigaherz.util.gddl2.structure.GddlMap;

public class PassthroughMapper extends Mapper
{
    public PassthroughMapper(int priority)
    {
        super(priority);
    }

    @Override
    public boolean canApply(Class<?> clazz)
    {
        return GddlElement.class.isAssignableFrom(clazz);
    }

    @Override
    public GddlElement<?> serialize(Object object, GddlSerializer serializer) throws ReflectiveOperationException
    {
        return (GddlElement<?>) object;
    }

    @Override
    public GddlElement<?> serializeVerbose(Object object, GddlSerializer serializer) throws ReflectiveOperationException
    {
        return wrapVerbose(object, serialize(object, serializer));
    }

    @Override
    public Object deserialize(GddlElement<?> map, Class<?> clazz, GddlSerializer serializer) throws ReflectiveOperationException
    {
        return clazz.cast(map);
    }

    @Override
    public Object deserializeVerbose(GddlMap map, Class<?> clazz, GddlSerializer serializer) throws ReflectiveOperationException
    {
        return unwrapVerbose(map);
    }
}
