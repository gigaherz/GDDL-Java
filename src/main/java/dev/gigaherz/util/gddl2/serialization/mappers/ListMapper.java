package dev.gigaherz.util.gddl2.serialization.mappers;

import dev.gigaherz.util.gddl2.serialization.GddlSerializationException;
import dev.gigaherz.util.gddl2.serialization.GddlSerializer;
import dev.gigaherz.util.gddl2.structure.GddlList;
import dev.gigaherz.util.gddl2.structure.GddlMap;

import java.util.List;

@SuppressWarnings("unchecked")
public class ListMapper extends MapperBase
{
    public ListMapper(int priority)
    {
        super(priority);
    }

    @Override
    public boolean canMapToField(Class<?> clazz)
    {
        return List.class.isAssignableFrom(clazz);
    }

    @Override
    public boolean canMapToMap(Class<?> clazz)
    {
        return List.class.isAssignableFrom(clazz);
    }

    @Override
    public void serializeField(GddlMap parent, String fieldName, Object object, GddlSerializer serializer)
            throws ReflectiveOperationException
    {
        parent.put(fieldName, serializeList((List) object, serializer));
    }

    @Override
    public Object deserializeField(GddlMap parent, String fieldName, Class<?> clazz, GddlSerializer serializer)
            throws ReflectiveOperationException
    {
        GddlMap tag2 = (GddlMap) parent.get(fieldName);
        return deserializeList(tag2, (Class<? extends List>) clazz, serializer);
    }

    @Override
    public GddlMap serializeMap(Object object, GddlSerializer serializer)
            throws ReflectiveOperationException
    {
        return serializeList((List) object, serializer);
    }

    @Override
    public Object deserializeMap(GddlMap self, Class<?> clazz, GddlSerializer serializer)
            throws ReflectiveOperationException
    {
        return deserializeList(self, (Class<? extends List>) clazz, serializer);
    }

    private GddlMap serializeList(List l, GddlSerializer serializer)
            throws ReflectiveOperationException
    {
        GddlMap tag = getTypeCompound(l, "list");

        GddlList list = GddlList.empty();
        for (int ii = 0; ii < l.size(); ii++)
        {
            Object o = l.get(ii);
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

    private List deserializeList(GddlMap tag, Class<? extends List> clazz, GddlSerializer serializer)
            throws ReflectiveOperationException
    {
        if (!tag.getString("type").equals("list"))
            throw new GddlSerializationException();

        Class<?> actual = Class.forName(tag.getString("className"));
        if (!clazz.isAssignableFrom(actual))
            throw new GddlSerializationException();

        List l = (List) actual.newInstance();

        GddlList list = tag.getList("elements");

        for (int ii = 0; ii < list.size(); ii++)
        {
            l.add(null);
        }

        for (int ii = 0; ii < list.size(); ii++)
        {
            GddlMap tag2 = (GddlMap) list.get(ii);

            int index = (int)tag2.getInt("index");

            if (!tag2.containsKey("value"))
            {
                continue;
            }

            Class<?> cls = Class.forName(tag2.getString("valueClass"));
            Object value = serializer.deserializeFrom(tag2, "value", cls, null);

            l.set(index, value);
        }

        return l;
    }
}
