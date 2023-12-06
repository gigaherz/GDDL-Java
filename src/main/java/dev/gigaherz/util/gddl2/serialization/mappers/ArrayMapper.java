package dev.gigaherz.util.gddl2.serialization.mappers;

import dev.gigaherz.util.gddl2.serialization.GddlSerializer;
import dev.gigaherz.util.gddl2.structure.GddlElement;
import dev.gigaherz.util.gddl2.structure.GddlList;
import dev.gigaherz.util.gddl2.structure.GddlMap;

import java.lang.reflect.Array;
import java.util.function.ObjIntConsumer;

public class ArrayMapper extends Mapper
{
    public ArrayMapper(int priority)
    {
        super(priority);
    }

    @Override
    public boolean canApply(Class<?> clazz)
    {
        return clazz.isArray();
    }

    @Override
    public GddlElement<?> serialize(Object object, GddlSerializer serializer)
            throws ReflectiveOperationException
    {
        return serializeArray(object, serializer);
    }

    @Override
    public GddlElement<?> serializeVerbose(Object object, GddlSerializer serializer) throws ReflectiveOperationException
    {
        return wrapVerbose(object, serialize(object, serializer));
    }

    @Override
    public Object deserialize(GddlElement<?> element, Class<?> clazz, GddlSerializer serializer)
            throws ReflectiveOperationException
    {
        return deserializeArray(element, clazz, serializer);
    }

    @Override
    public Object deserializeVerbose(GddlMap map, Class<?> clazz, GddlSerializer serializer) throws ReflectiveOperationException
    {
        return deserialize(unwrapVerbose(map), clazz, serializer);
    }

    private GddlElement<?> serializeArray(Object object, GddlSerializer serializer)
            throws ReflectiveOperationException
    {
        GddlList list = GddlList.empty();
        for (int ii = 0; ii < Array.getLength(object); ii++)
        {
            var element = Array.get(object, ii);
            list.add(serializer.serialize(element));
        }
        return list;
    }

    private Object deserializeArray(GddlElement<?> map, Class<?> clazz, GddlSerializer serializer)
            throws ReflectiveOperationException
    {
        GddlList list = map.asList();

        var arrayType = clazz.getComponentType();
        Object o = Array.newInstance(arrayType, list.size());

        if (arrayType == Byte.class || arrayType == byte.class)
        {
            iterateList(list, arrayType, serializer, (value, i) -> Array.setByte(o, i, (Byte) value));
        }
        else if (arrayType == Short.class || arrayType == short.class)
        {
            iterateList(list, arrayType, serializer, (value, i) -> Array.setShort(o, i, (Short) value));
        }
        else if (arrayType == Integer.class || arrayType == int.class)
        {
            iterateList(list, arrayType, serializer, (value, i) -> Array.setInt(o, i, (Integer) value));
        }
        else if (arrayType == Long.class || arrayType == long.class)
        {
            iterateList(list, arrayType, serializer, (value, i) -> Array.setLong(o, i, (Long) value));
        }
        else if (arrayType == Float.class || arrayType == float.class)
        {
            iterateList(list, arrayType, serializer, (value, i) -> Array.setFloat(o, i, (Float) value));
        }
        else if (arrayType == Double.class || arrayType == double.class)
        {
            iterateList(list, arrayType, serializer, (value, i) -> Array.setDouble(o, i, (Double) value));
        }
        else if (arrayType == Boolean.class || arrayType == boolean.class)
        {
            iterateList(list, arrayType, serializer, (value, i) -> Array.setBoolean(o, i, (Boolean) value));
        }
        else if (arrayType == Character.class || arrayType == char.class)
        {
            iterateList(list, arrayType, serializer, (value, i) -> Array.setChar(o, i, (Character) value));
        }
        else
        {
            iterateList(list, arrayType, serializer, (value, i) -> Array.set(o, i, value));
        }

        return o;
    }

    private void iterateList(GddlList list, Class<?> arrayType, GddlSerializer serializer, ObjIntConsumer<Object> consumer)
            throws ReflectiveOperationException
    {
        for (int i = 0; i < list.size(); i++)
        {
            var entry = list.get(i);

            var value = entry.isNull() ? null : serializer.deserialize(entry, arrayType);

            consumer.accept(value, i);
        }
    }
}
