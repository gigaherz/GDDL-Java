package dev.gigaherz.util.gddl2.serialization.mappers;

import dev.gigaherz.util.gddl2.serialization.GddlSerializationException;
import dev.gigaherz.util.gddl2.serialization.GddlSerializer;
import dev.gigaherz.util.gddl2.structure.GddlList;
import dev.gigaherz.util.gddl2.structure.GddlMap;

import java.util.Set;

@SuppressWarnings({"unchecked", "rawtypes"})
public class SetMapper extends MapperBase
{
    public SetMapper(int priority)
    {
        super(priority);
    }

    @Override
    public boolean canMapToField(Class<?> clazz)
    {
        return Set.class.isAssignableFrom(clazz);
    }

    @Override
    public boolean canMapToMap(Class<?> clazz)
    {
        return Set.class.isAssignableFrom(clazz);
    }

    @Override
    public void serializeField(GddlMap parent, String fieldName, Object object, GddlSerializer serializer)
            throws ReflectiveOperationException
    {
        parent.put(fieldName, serializeSet((Set) object, serializer));
    }

    @Override
    public Object deserializeField(GddlMap parent, String fieldName, Class<?> clazz, GddlSerializer serializer)
            throws ReflectiveOperationException
    {
        GddlMap tag2 = (GddlMap) parent.get(fieldName);
        return deserializeSet(tag2, (Class<? extends Set>) clazz, serializer);
    }

    @Override
    public GddlMap serializeMap(Object object, GddlSerializer serializer)
            throws ReflectiveOperationException
    {
        return serializeSet((Set) object, serializer);
    }

    @Override
    public Object deserializeMap(GddlMap self, Class<?> clazz, GddlSerializer serializer)
            throws ReflectiveOperationException
    {
        return deserializeSet(self, (Class<? extends Set>) clazz, serializer);
    }

    private GddlMap serializeSet(Set s, GddlSerializer serializer)
            throws ReflectiveOperationException
    {
        GddlMap tag = getTypeCompound(s, "set");

        GddlList list = GddlList.empty();
        for (Object o : s)
        {
            GddlMap tag2 = GddlMap.empty();
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

    private Set deserializeSet(GddlMap tag, Class<? extends Set> clazz, GddlSerializer serializer)
            throws ReflectiveOperationException
    {
        if (!tag.getString("type").equals("set"))
            throw new GddlSerializationException();

        Class<?> actual = Class.forName(tag.getString("className"));
        if (!clazz.isAssignableFrom(actual))
            throw new GddlSerializationException();

        Set s = (Set) actual.newInstance();

        GddlList list = tag.getList("elements");
        for (var gddlElement : list)
        {
            GddlMap tag2 = (GddlMap) gddlElement;

            Object value = null;
            if (tag2.containsKey("value"))
            {
                Class<?> cls = Class.forName(tag2.getString("valueClass"));
                value = serializer.deserializeFrom(tag2, "value", cls, null);
            }

            s.add(value);
        }

        return s;
    }

}
