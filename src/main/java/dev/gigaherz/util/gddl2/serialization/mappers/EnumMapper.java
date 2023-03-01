package dev.gigaherz.util.gddl2.serialization.mappers;

import dev.gigaherz.util.gddl2.serialization.GddlSerializationException;
import dev.gigaherz.util.gddl2.serialization.GddlSerializer;
import dev.gigaherz.util.gddl2.structure.GddlMap;

@SuppressWarnings("unchecked")
public class EnumMapper extends MapperBase
{
    public EnumMapper(int priority)
    {
        super(priority);
    }

    @Override
    public boolean canMapToField(Class<?> clazz)
    {
        return clazz.isEnum();
    }

    @Override
    public boolean canMapToMap(Class<?> clazz)
    {
        return false;
    }

    @Override
    public GddlMap serializeMap(Object object, GddlSerializer serializer)
            throws ReflectiveOperationException
    {
        return null;
    }

    @Override
    public Object deserializeMap(GddlMap self, Class<?> clazz, GddlSerializer serializer)
            throws ReflectiveOperationException
    {
        return null;
    }

    @Override
    public void serializeField(GddlMap parent, String fieldName, Object object, GddlSerializer serializer)
            throws ReflectiveOperationException
    {
        GddlMap tag2 = GddlMap.empty();
        serializeEnum(tag2, ((Enum) object));
        parent.put(fieldName, tag2);
    }

    @Override
    public Object deserializeField(GddlMap parent, String fieldName, Class<?> clazz, GddlSerializer serializer)
            throws ReflectiveOperationException
    {
        GddlMap tag2 = (GddlMap) parent.get(fieldName);
        return deserializeEnum(tag2, (Class<? extends Enum>) clazz);
    }

    private static void serializeEnum(GddlMap tag, Enum o)
    {
        tag.put("type", "enum");
        tag.put("className", o.getClass().getName());
        tag.put("valueName", o.name());
    }

    private static Object deserializeEnum(GddlMap tag, Class<? extends Enum> clazz)
    {
        if (!tag.getString("type").equals("enum"))
            throw new GddlSerializationException();
        if (!tag.getString("className").equals(clazz.getName()))
            throw new GddlSerializationException();

        return Enum.valueOf(clazz, tag.getString("value"));
    }
}
