package dev.gigaherz.util.gddl2.serialization.mappers;

import dev.gigaherz.util.gddl2.serialization.GddlSerializationException;
import dev.gigaherz.util.gddl2.serialization.GddlSerializer;
import dev.gigaherz.util.gddl2.structure.GddlElement;
import dev.gigaherz.util.gddl2.structure.GddlMap;
import dev.gigaherz.util.gddl2.structure.GddlValue;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class GenericObjectMapper extends MapperBase
{
    public GenericObjectMapper(int priority)
    {
        super(priority);
    }

    @Override
    public boolean canApply(Class<?> clazz)
    {
        return true;
    }

    @Override
    public GddlElement<?> serialize(Object object, GddlSerializer serializer) throws ReflectiveOperationException
    {
        return serializeObject(object, serializer);
    }

    @Override
    public GddlElement<?> serializeVerbose(Object object, GddlSerializer serializer) throws ReflectiveOperationException
    {
        return serialize(object, serializer);
    }

    @Override
    public Object deserialize(GddlElement<?> element, Class<?> clazz, GddlSerializer serializer) throws ReflectiveOperationException
    {
        return deserializeObject(element, clazz, serializer);
    }

    @Override
    public Object deserializeVerbose(GddlMap map, Class<?> clazz, GddlSerializer serializer) throws ReflectiveOperationException
    {
        return deserialize(map, clazz, serializer);
    }

    private GddlElement<?> serializeObject(Object o, GddlSerializer serializer)
            throws ReflectiveOperationException
    {
        if (o == null)
        {
            return GddlValue.nullValue();
        }

        GddlMap map = makeVerboseMap(o);

        Class<?> cls = o.getClass();

        // The loop skips Object
        while (cls.getSuperclass() != null)
        {
            Field[] fields = cls.getDeclaredFields();
            for (Field f : fields)
            {
                if (Modifier.isStatic(f.getModifiers()))
                    continue;

                f.setAccessible(true);
                map.put(f.getName(), serializer.serialize(f.get(o)));
            }

            cls = cls.getSuperclass();
        }

        return map;
    }

    private Object deserializeObject(GddlElement<?> element, Class<?> clazz, GddlSerializer serializer)
            throws ReflectiveOperationException
    {
        if (element.isNull())
            return null;

        var map = element.asMap();

        Class<?> actual = Class.forName(map.getString("class"));
        if (!clazz.isAssignableFrom(actual))
            throw new GddlSerializationException();

        Class<?> cls = actual;

        Object o = cls.newInstance();

        // The loop skips Object
        while (cls.getSuperclass() != null)
        {
            Field[] fields = cls.getDeclaredFields();
            for (Field f : fields)
            {
                if (Modifier.isStatic(f.getModifiers()))
                    continue;

                f.setAccessible(true);
                f.set(o, serializer.deserialize(map.get(f.getName()), f.getType()));
            }

            cls = cls.getSuperclass();
        }

        return o;
    }
}
