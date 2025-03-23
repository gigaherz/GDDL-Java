package dev.gigaherz.util.gddl2.serialization.mappers;

import dev.gigaherz.util.gddl2.serialization.GddlSerializer;
import dev.gigaherz.util.gddl2.structure.GddlElement;
import dev.gigaherz.util.gddl2.structure.GddlMap;
import dev.gigaherz.util.gddl2.structure.GddlValue;

@SuppressWarnings("unchecked")
public class EnumMapper extends Mapper
{
    public EnumMapper(int priority)
    {
        super(priority);
    }

    @Override
    public boolean canApply(Class<?> clazz)
    {
        return clazz.isEnum();
    }

    @Override
    public GddlElement<?> serialize(Object object, GddlSerializer serializer)
    {
        return GddlValue.of(((Enum<?>) object).name());
    }

    @Override
    public GddlElement<?> serializeVerbose(Object object, GddlSerializer serializer)
    {
        return wrapVerbose(object, serialize(object, serializer));
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Object deserialize(GddlElement<?> element, Class<?> clazz, GddlSerializer serializer)
    {
        return Enum.valueOf((Class) clazz, element.stringValue());
    }

    @Override
    public Object deserializeVerbose(GddlMap map, Class<?> clazz, GddlSerializer serializer)
    {
        return deserialize(unwrapVerbose(map), clazz, serializer);
    }
}
