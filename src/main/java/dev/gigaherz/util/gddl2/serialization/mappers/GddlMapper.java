package dev.gigaherz.util.gddl2.serialization.mappers;

import dev.gigaherz.util.gddl2.serialization.GddlSerializer;
import dev.gigaherz.util.gddl2.structure.GddlElement;
import dev.gigaherz.util.gddl2.structure.GddlMap;
import dev.gigaherz.util.gddl2.structure.GddlValue;

public class GddlMapper extends MapperBase
{
    public GddlMapper(int priority)
    {
        super(priority);
    }

    @Override
    public boolean canMapToField(Class<?> clazz)
    {
        return GddlElement.class.isAssignableFrom(clazz);
    }

    @Override
    public boolean canMapToMap(Class<?> clazz)
    {
        return GddlElement.class.isAssignableFrom(clazz);
    }

    @Override
    public void serializeField(GddlMap parent, String fieldName, Object object, GddlSerializer serializer) throws ReflectiveOperationException
    {
        parent.put(fieldName, ((GddlElement<?>) object).copy());
    }

    @Override
    public Object deserializeField(GddlMap parent, String fieldName, Class<?> clazz, GddlSerializer serializer) throws ReflectiveOperationException
    {
        return parent.get(fieldName).copy();
    }

    @Override
    public GddlMap serializeMap(Object object, GddlSerializer serializer) throws ReflectiveOperationException
    {
        GddlElement<?> obj = (GddlElement<?>)object;
        if (obj.isMap())
            return obj.asMap();

        return GddlMap.of("type", GddlValue.of("compound"), "value", obj);
    }

    @Override
    public Object deserializeMap(GddlMap tag, Class<?> clazz, GddlSerializer serializer) throws ReflectiveOperationException
    {
        if (tag.getString("type").equals("compound"))
            return tag.get("value");

        return tag;
    }
}
