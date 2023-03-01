package dev.gigaherz.util.gddl2.serialization.mappers;

import dev.gigaherz.util.gddl2.serialization.GddlSerializer;
import dev.gigaherz.util.gddl2.structure.GddlMap;

public class StringMapper extends MapperBase
{
    public StringMapper(int priority)
    {
        super(priority);
    }

    @Override
    public boolean canMapToField(Class<?> clazz)
    {
        return clazz == String.class;
    }

    @Override
    public boolean canMapToMap(Class<?> clazz)
    {
        return false;
    }

    @Override
    public void serializeField(GddlMap parent, String fieldName, Object object, GddlSerializer serializer) throws ReflectiveOperationException
    {
        parent.put(fieldName, (String) object);
    }

    @Override
    public Object deserializeField(GddlMap parent, String fieldName, Class<?> clazz, GddlSerializer serializer) throws ReflectiveOperationException
    {
        return parent.get(fieldName).stringValue();
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
