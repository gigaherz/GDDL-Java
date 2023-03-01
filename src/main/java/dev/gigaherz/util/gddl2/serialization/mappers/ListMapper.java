package dev.gigaherz.util.gddl2.serialization.mappers;

import dev.gigaherz.util.gddl2.serialization.GddlSerializationException;
import dev.gigaherz.util.gddl2.serialization.GddlSerializer;
import dev.gigaherz.util.gddl2.structure.GddlElement;
import dev.gigaherz.util.gddl2.structure.GddlList;
import dev.gigaherz.util.gddl2.structure.GddlMap;

import java.util.List;

@SuppressWarnings("unchecked")
public class ListMapper extends MapperBase
{
    public ListMapper(int priority)
    {
        super(priority);
    }

    @Override
    public boolean canApply(Class<?> clazz)
    {
        return List.class.isAssignableFrom(clazz);
    }

    @Override
    public GddlElement<?> serialize(Object object, GddlSerializer serializer)
            throws ReflectiveOperationException
    {
        return serializeList((List) object, serializer);
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
        return deserializeList(element.asMap(), (Class<? extends List>) clazz, serializer);
    }

    @Override
    public Object deserializeVerbose(GddlMap map, Class<?> clazz, GddlSerializer serializer) throws ReflectiveOperationException
    {
        return deserialize(map, clazz, serializer);
    }

    private GddlElement<?> serializeList(List l, GddlSerializer serializer)
            throws ReflectiveOperationException
    {
        GddlMap map = makeVerboseMap(l);

        GddlList list = GddlList.empty();
        for (var entry : l)
        {
            list.add(serializer.serializeVerbose(entry));
        }
        map.put("elements", list);
        return map;
    }

    private List deserializeList(GddlMap map, Class<? extends List> clazz, GddlSerializer serializer)
            throws ReflectiveOperationException
    {
        Class<?> actual = Class.forName(map.getString("class"));
        if (!clazz.isAssignableFrom(actual))
            throw new GddlSerializationException();

        var list = (List) actual.newInstance();

        var elements = map.getList("elements");

        for (var entry : elements)
        {
            list.add(serializer.deserializeVerbose(entry));
        }

        return list;
    }
}
