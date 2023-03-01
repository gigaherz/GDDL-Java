package dev.gigaherz.util.gddl2.serialization.mappers;

import dev.gigaherz.util.gddl2.serialization.GddlSerializer;
import dev.gigaherz.util.gddl2.structure.GddlElement;
import dev.gigaherz.util.gddl2.structure.GddlMap;
import dev.gigaherz.util.gddl2.structure.GddlValue;

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
    public boolean canApply(Class<?> clazz)
    {
        return clazz.isPrimitive() || isBoxType(clazz);
    }

    @Override
    public GddlElement<?> serialize(Object object, GddlSerializer serializer) throws ReflectiveOperationException
    {
        if (object instanceof Byte b)
        {
            return GddlValue.of(b);
        }
        else if (object instanceof Short s)
        {
            return GddlValue.of(s);
        }
        else if (object instanceof Integer i)
        {
            return GddlValue.of(i);
        }
        else if (object instanceof Long l)
        {
            return GddlValue.of(l);
        }
        else if (object instanceof Float f)
        {
            return GddlValue.of(f);
        }
        else if (object instanceof Double d)
        {
            return GddlValue.of(d);
        }
        else if (object instanceof Boolean b)
        {
            return GddlValue.of(b);
        }
        else if (object instanceof Character c)
        {
            return GddlValue.of(c);
        }
        throw new IllegalStateException("Class is not a java primitive!");
    }

    @Override
    public GddlElement<?> serializeVerbose(Object object, GddlSerializer serializer) throws ReflectiveOperationException
    {
        return wrapVerbose(object, serialize(object, serializer));
    }

    @Override
    public Object deserialize(GddlElement<?> element, Class<?> clazz, GddlSerializer serializer) throws ReflectiveOperationException
    {
        if (clazz == Byte.class || clazz == byte.class)
        {
            return element.byteValue();
        }
        else if (clazz == Short.class || clazz == short.class)
        {
            return element.shortValue();
        }
        else if (clazz == Integer.class || clazz == int.class)
        {
            return element.intValue();
        }
        else if (clazz == Long.class || clazz == long.class)
        {
            return element.longValue();
        }
        else if (clazz == Float.class || clazz == float.class)
        {
            return element.floatValue();
        }
        else if (clazz == Double.class || clazz == double.class)
        {
            return element.doubleValue();
        }
        else if (clazz == Boolean.class || clazz == boolean.class)
        {
            return element.booleanValue();
        }
        else if (clazz == Character.class || clazz == char.class)
        {
            return (char)element.intValue();
        }
        return null;
    }

    @Override
    public Object deserializeVerbose(GddlMap map, Class<?> clazz, GddlSerializer serializer) throws ReflectiveOperationException
    {
        return deserialize(unwrapVerbose(map), clazz, serializer);
    }
}
