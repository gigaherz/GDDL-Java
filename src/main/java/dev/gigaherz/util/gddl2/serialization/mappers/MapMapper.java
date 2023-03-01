package dev.gigaherz.util.gddl2.serialization.mappers;

import dev.gigaherz.util.gddl2.serialization.GddlSerializationException;
import dev.gigaherz.util.gddl2.serialization.GddlSerializer;
import dev.gigaherz.util.gddl2.structure.GddlList;
import dev.gigaherz.util.gddl2.structure.GddlMap;

import java.util.Map;

@SuppressWarnings({"unchecked", "rawtypes"})
public class MapMapper extends MapperBase
{
    public MapMapper(int priority)
    {
        super(priority);
    }

    @Override
    public boolean canMapToField(Class<?> clazz)
    {
        return Map.class.isAssignableFrom(clazz);
    }

    @Override
    public boolean canMapToMap(Class<?> clazz)
    {
        return Map.class.isAssignableFrom(clazz);
    }

    @Override
    public void serializeField(GddlMap parent, String fieldName, Object object, GddlSerializer serializer)
            throws ReflectiveOperationException
    {
        parent.put(fieldName, serializeMap((Map) object, serializer));
    }

    @Override
    public Object deserializeField(GddlMap parent, String fieldName, Class<?> clazz, GddlSerializer serializer)
            throws ReflectiveOperationException
    {
        GddlMap tag2 = (GddlMap) parent.get(fieldName);
        return deserializeMapInternal(tag2, (Class<? extends Map>) clazz, serializer);
    }

    @Override
    public GddlMap serializeMap(Object object, GddlSerializer serializer)
            throws ReflectiveOperationException
    {
        return serializeMap((Map) object, serializer);
    }

    @Override
    public Object deserializeMap(GddlMap self, Class<?> clazz, GddlSerializer serializer)
            throws ReflectiveOperationException
    {
        return deserializeMapInternal(self, (Class<? extends Map>) clazz, serializer);
    }

    private GddlMap serializeMap(Map<Object, Object> m, GddlSerializer serializer)
            throws ReflectiveOperationException
    {
        GddlMap tag = getTypeCompound(m, "map");

        GddlList list = GddlList.empty();
        for (Map.Entry e : m.entrySet())
        {
            GddlMap tag2 = GddlMap.empty();
            Object key = e.getKey();
            Object value = e.getValue();
            if (key != null)
            {
                serializer.serializeTo(tag2, "keyClass", key.getClass().getName());
                serializer.serializeTo(tag2, "key", key);
            }
            if (value != null)
            {
                serializer.serializeTo(tag2, "valueClass", value.getClass().getName());
                serializer.serializeTo(tag2, "value", value);
            }
            list.add(tag2);
        }
        tag.put("elements", list);
        return tag;
    }

    private Map deserializeMapInternal(GddlMap tag, Class<? extends Map> clazz, GddlSerializer serializer)
            throws ReflectiveOperationException
    {
        if (!tag.getString("type").equals("map"))
            throw new GddlSerializationException();

        Class<?> actual = Class.forName(tag.getString("className"));
        if (!clazz.isAssignableFrom(actual))
            throw new GddlSerializationException();

        Map m = (Map) actual.newInstance();

        GddlList list = tag.getList("elements");
        for (int ii = 0; ii < list.size(); ii++)
        {
            GddlMap tag2 = (GddlMap) list.get(ii);

            Object key = null;
            Object value = null;

            if (tag2.containsKey("key"))
            {
                Class<?> clsk = Class.forName(tag2.getString("keyClass"));
                key = serializer.deserializeFrom(tag2, "key", clsk, null);
            }


            if (tag2.containsKey("value"))
            {
                Class<?> cls = Class.forName(tag2.getString("valueClass"));
                value = serializer.deserializeFrom(tag2, "value", cls, null);
            }

            m.put(key, value);
        }

        return m;
    }
}
