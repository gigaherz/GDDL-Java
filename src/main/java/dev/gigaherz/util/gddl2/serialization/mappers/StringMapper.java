package dev.gigaherz.util.gddl2.serialization.mappers;

import dev.gigaherz.util.gddl2.serialization.GddlSerializer;
import dev.gigaherz.util.gddl2.structure.GddlElement;
import dev.gigaherz.util.gddl2.structure.GddlMap;
import dev.gigaherz.util.gddl2.structure.GddlValue;

public class StringMapper extends Mapper
{
    public StringMapper(int priority)
    {
        super(priority);
    }

    @Override
    public boolean canApply(Class<?> clazz)
    {
        return clazz == String.class;
    }

    @Override
    public GddlElement<?> serialize(Object object, GddlSerializer serializer) throws ReflectiveOperationException
    {
        return GddlValue.of((String)object);
    }

    @Override
    public GddlElement<?> serializeVerbose(Object object, GddlSerializer serializer) throws ReflectiveOperationException
    {
        return wrapVerbose(object, serialize(object, serializer));
    }

    @Override
    public Object deserialize(GddlElement<?> element, Class<?> clazz, GddlSerializer serializer) throws ReflectiveOperationException
    {
        return element.stringValue();
    }

    @Override
    public Object deserializeVerbose(GddlMap map, Class<?> clazz, GddlSerializer serializer) throws ReflectiveOperationException
    {
        return deserialize(unwrapVerbose(map), clazz, serializer);
    }
}
