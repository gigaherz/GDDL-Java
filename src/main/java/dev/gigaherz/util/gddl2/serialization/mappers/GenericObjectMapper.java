package dev.gigaherz.util.gddl2.serialization.mappers;

import dev.gigaherz.util.gddl2.serialization.GddlSerializationException;
import dev.gigaherz.util.gddl2.serialization.GddlSerializer;
import dev.gigaherz.util.gddl2.structure.GddlMap;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class GenericObjectMapper extends MapperBase
{
    public GenericObjectMapper(int priority)
    {
        super(priority);
    }

    @Override
    public boolean canMapToField(Class<?> clazz)
    {
        return true;
    }

    @Override
    public boolean canMapToMap(Class<?> clazz)
    {
        return true;
    }

    @Override
    public void serializeField(GddlMap parent, String fieldName, Object object, GddlSerializer serializer) throws ReflectiveOperationException
    {
        GddlMap tag2 = serializeObject(object, serializer);
        parent.put(fieldName, tag2);
    }

    @Override
    public Object deserializeField(GddlMap parent, String fieldName, Class<?> clazz, GddlSerializer serializer) throws ReflectiveOperationException
    {
        GddlMap tag2 = (GddlMap) parent.get(fieldName);
        return deserializeObject(tag2, clazz, serializer);
    }

    @Override
    public GddlMap serializeMap(Object object, GddlSerializer serializer) throws ReflectiveOperationException
    {
        return serializeObject(object, serializer);
    }

    @Override
    public Object deserializeMap(GddlMap self, Class<?> clazz, GddlSerializer serializer) throws ReflectiveOperationException
    {
        return deserializeObject(self, clazz, serializer);
    }

    private GddlMap serializeObject(Object o, GddlSerializer serializer)
            throws ReflectiveOperationException
    {
        if (o == null)
        {
            GddlMap tag = GddlMap.empty();
            tag.put("type", "null");
            return tag;
        }

        GddlMap tag = getTypeCompound(o, "object");

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
                serializer.serializeTo(tag, f.getName(), f.get(o));
            }

            cls = cls.getSuperclass();
        }

        return tag;
    }

    private Object deserializeObject(GddlMap tag, Class<?> clazz, GddlSerializer serializer)
            throws ReflectiveOperationException
    {
        if (tag.getString("type").equals("null"))
            return null;

        if (!tag.getString("type").equals("object"))
            throw new GddlSerializationException();

        Class<?> actual = Class.forName(tag.getString("className"));
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
                f.set(o, serializer.deserializeFrom(tag, f.getName(), f.getType(), f.get(o)));
            }

            cls = cls.getSuperclass();
        }

        return o;
    }
}
