package dev.gigaherz.util.gddl2.serialization.mappers;

import dev.gigaherz.util.gddl2.serialization.GddlSerializationException;
import dev.gigaherz.util.gddl2.serialization.GddlSerializer;
import dev.gigaherz.util.gddl2.structure.GddlList;
import dev.gigaherz.util.gddl2.structure.GddlMap;

import java.lang.reflect.Array;

public class ArrayMapper extends MapperBase
{
    public ArrayMapper(int priority)
    {
        super(priority);
    }

    @Override
    public boolean canMapToField(Class<?> clazz)
    {
        return clazz.isArray();
    }

    @Override
    public boolean canMapToMap(Class<?> clazz)
    {
        return clazz.isArray();
    }

    @Override
    public void serializeField(GddlMap parent, String fieldName, Object object, GddlSerializer serializer)
            throws ReflectiveOperationException
    {
        parent.put(fieldName, serializeArray(object, serializer));
    }

    @Override
    public Object deserializeField(GddlMap parent, String fieldName, Class<?> clazz, GddlSerializer serializer)
            throws ReflectiveOperationException
    {
        GddlMap tag2 = (GddlMap) parent.get(fieldName);
        return deserializeArray(tag2, clazz, serializer);
    }

    @Override
    public GddlMap serializeMap(Object object, GddlSerializer serializer)
            throws ReflectiveOperationException
    {
        return serializeArray(object, serializer);
    }

    @Override
    public Object deserializeMap(GddlMap self, Class<?> clazz, GddlSerializer serializer)
            throws ReflectiveOperationException
    {
        return deserializeArray(self, clazz, serializer);
    }

    private GddlMap serializeArray(Object a, GddlSerializer serializer)
            throws ReflectiveOperationException
    {
        GddlMap tag = getTypeCompound(a, "array");

        GddlList list = GddlList.empty();
        for (int ii = 0; ii < Array.getLength(a); ii++)
        {
            Object o = Array.get(a, ii);
            GddlMap tag2 = GddlMap.empty();
            tag2.put("index", ii);
            if (o != null)
            {
                serializer.serializeTo(tag2, "valueClass", o.getClass().getName());
                serializer.serializeTo(tag2, "value", o);
            }
            list.add(tag2);
        }
        tag.put("elements", list);
        return tag;
    }

    private Object deserializeArray(GddlMap tag, Class<?> clazz, GddlSerializer serializer)
            throws ReflectiveOperationException
    {
        if (!tag.getString("type").equals("array"))
            throw new GddlSerializationException();

        GddlList list = tag.getList("elements");

        Object o = Array.newInstance(clazz.getComponentType(), list.size());

        for (var gddlElement : list)
        {
            GddlMap tag2 = (GddlMap) gddlElement;

            int index = (int) tag2.getInt("index");

            if (!tag2.containsKey("value"))
            {
                continue;
            }

            Class<?> cls = Class.forName(tag2.getString("valueClass"));
            Object value = serializer.deserializeFrom(tag2, "value", cls, null);

            if (cls == Byte.class || cls == byte.class)
            {
                Array.setByte(o, index, (Byte) value);
            }
            else if (cls == Short.class || cls == short.class)
            {
                Array.setShort(o, index, (Short) value);
            }
            else if (cls == Integer.class || cls == int.class)
            {
                Array.setInt(o, index, (Integer) value);
            }
            else if (cls == Long.class || cls == long.class)
            {
                Array.setLong(o, index, (Long) value);
            }
            else if (cls == Float.class || cls == float.class)
            {
                Array.setFloat(o, index, (Float) value);
            }
            else if (cls == Double.class || cls == double.class)
            {
                Array.setDouble(o, index, (Double) value);
            }
            else if (cls == Boolean.class || cls == boolean.class)
            {
                Array.setBoolean(o, index, (Boolean) value);
            }
            else if (cls == Character.class || cls == char.class)
            {
                Array.setChar(o, index, (Character) value);
            }
            else
            {
                Array.set(o, index, value);
            }
        }

        return o;
    }
}
