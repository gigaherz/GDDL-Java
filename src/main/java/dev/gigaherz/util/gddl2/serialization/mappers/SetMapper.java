package dev.gigaherz.util.gddl2.serialization.mappers;

import dev.gigaherz.util.gddl2.serialization.GddlSerializationException;
import dev.gigaherz.util.gddl2.serialization.GddlSerializer;
import dev.gigaherz.util.gddl2.structure.GddlElement;
import dev.gigaherz.util.gddl2.structure.GddlList;
import dev.gigaherz.util.gddl2.structure.GddlMap;

import java.util.Set;

@SuppressWarnings({"unchecked", "rawtypes"})
public class SetMapper extends Mapper
{
    public SetMapper(int priority)
    {
        super(priority);
    }

    @Override
    public boolean canApply(Class<?> clazz)
    {
        return Set.class.isAssignableFrom(clazz);
    }

    @Override
    public GddlMap serialize(Object object, GddlSerializer serializer)
            throws ReflectiveOperationException
    {
        return serializeSet((Set) object, serializer);
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
        return deserializeSet(element.asMap(), (Class<? extends Set>) clazz, serializer);
    }

    @Override
    public Object deserializeVerbose(GddlMap map, Class<?> clazz, GddlSerializer serializer) throws ReflectiveOperationException
    {
        return deserialize(map, clazz, serializer);
    }

    private GddlMap serializeSet(Set object, GddlSerializer serializer)
            throws ReflectiveOperationException
    {
        var map = makeVerboseMap(object);

        var list = GddlList.empty();
        for (var entry : object)
        {
            list.add(serializer.serializeVerbose(entry));
        }
        map.put("elements", list);

        return map;
    }

    private Set deserializeSet(GddlMap map, Class<? extends Set> clazz, GddlSerializer serializer)
            throws ReflectiveOperationException
    {
        Class<?> actual = Class.forName(map.getString("class"));
        if (!clazz.isAssignableFrom(actual))
            throw new GddlSerializationException();

        Set s = (Set) actual.getDeclaredConstructor().newInstance();

        var elements = map.getList("elements");
        for (var entry : elements)
        {
            s.add(serializer.deserializeVerbose(entry));
        }

        return s;
    }
}
