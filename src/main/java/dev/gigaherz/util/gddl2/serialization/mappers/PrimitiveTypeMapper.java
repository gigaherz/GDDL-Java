package dev.gigaherz.util.gddl2.serialization.mappers;

import dev.gigaherz.util.gddl2.serialization.GddlSerializer;
import dev.gigaherz.util.gddl2.structure.GddlMap;

import java.lang.reflect.Type;

public class PrimitiveTypeMapper extends MapperBase
{
    public PrimitiveTypeMapper(int priority)
    {
        super(priority);
    }

    private static boolean isBoxType(Type type) {
        return type == Integer.class
                || type == Float.class
                || type == Byte.class
                || type == Double.class
                || type == Long.class
                || type == Character.class
                || type == Boolean.class
                || type == Short.class
                || type == Void.class;
    }

    @Override
    public boolean canMapToField(Class<?> clazz)
    {
        return clazz.isPrimitive() || isBoxType(clazz);
    }

    @Override
    public boolean canMapToMap(Class<?> clazz)
    {
        return false;
    }

    @Override
    public void serializeField(GddlMap parent, String fieldName, Object object, GddlSerializer serializer) throws ReflectiveOperationException
    {
        if (object instanceof Byte b)
        {
            parent.put(fieldName, b);
        }
        else if (object instanceof Short s)
        {
            parent.put(fieldName, s);
        }
        else if (object instanceof Integer i)
        {
            parent.put(fieldName, i);
        }
        else if (object instanceof Long l)
        {
            parent.put(fieldName, l);
        }
        else if (object instanceof Float f)
        {
            parent.put(fieldName, f);
        }
        else if (object instanceof Double d)
        {
            parent.put(fieldName, d);
        }
        else if (object instanceof Boolean b)
        {
            parent.put(fieldName, b);
        }
        else if (object instanceof Character c)
        {
            parent.put(fieldName, c);
        }
    }

    @Override
    public Object deserializeField(GddlMap parent, String fieldName, Class<?> clazz, GddlSerializer serializer) throws ReflectiveOperationException
    {
        if (clazz == Byte.class || clazz == byte.class)
        {
            return (byte)parent.get(fieldName).intValue();
        }
        else if (clazz == Short.class || clazz == short.class)
        {
            return (short)parent.get(fieldName).intValue();
        }
        else if (clazz == Integer.class || clazz == int.class)
        {
            return (int)parent.get(fieldName).intValue();
        }
        else if (clazz == Long.class || clazz == long.class)
        {
            return parent.get(fieldName).intValue();
        }
        else if (clazz == Float.class || clazz == float.class)
        {
            return (float)parent.get(fieldName).doubleValue();
        }
        else if (clazz == Double.class || clazz == double.class)
        {
            return parent.get(fieldName).doubleValue();
        }
        else if (clazz == Boolean.class || clazz == boolean.class)
        {
            return parent.get(fieldName).booleanValue();
        }
        else if (clazz == Character.class || clazz == char.class)
        {
            return (char)parent.get(fieldName).intValue();
        }
        return null;
    }

    @Override
    public GddlMap serializeMap(Object object, GddlSerializer serializer) throws ReflectiveOperationException
    {
        return null;
    }

    @Override
    public Object deserializeMap(GddlMap self, Class<?> clazz, GddlSerializer serializer) throws ReflectiveOperationException
    {
        return null;
    }
}
