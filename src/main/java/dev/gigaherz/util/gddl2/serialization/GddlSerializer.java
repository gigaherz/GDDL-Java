package dev.gigaherz.util.gddl2.serialization;

import dev.gigaherz.util.gddl2.serialization.mappers.*;
import dev.gigaherz.util.gddl2.structure.GddlMap;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public class GddlSerializer
{
    public static final int PRIORITY_INTERFACE = Integer.MAX_VALUE;
    public static final int PRIORITY_USER = 0;
    public static final int PRIORITY_PRIMITIVE = -200;
    public static final int PRIORITY_COLLECTION = -300;

    private final List<MapperBase> mappers = new ArrayList<MapperBase>();
    private final GenericObjectMapper generic = new GenericObjectMapper(Integer.MIN_VALUE);


    public GddlSerializer()
    {
        this(true);
    }

    public GddlSerializer(boolean registerStockMappers)
    {
        if (registerStockMappers)
        {
            // Must have highest priority so that something such as "extends Map implements ICustomNBTSerializable",
            // favor the interface over the base class
            //registerNBTMapper(new CustomSerializableMapper(PRIORITY_INTERFACE));
            registerMapper(new PrimitiveTypeMapper(PRIORITY_PRIMITIVE));
            registerMapper(new StringMapper(PRIORITY_PRIMITIVE));
            registerMapper(new EnumMapper(PRIORITY_PRIMITIVE));
            registerMapper(new ArrayMapper(PRIORITY_PRIMITIVE));
            registerMapper(new ListMapper(PRIORITY_COLLECTION));
            registerMapper(new MapMapper(PRIORITY_COLLECTION));
            registerMapper(new SetMapper(PRIORITY_COLLECTION));
            registerMapper(new GddlMapper(PRIORITY_COLLECTION + 1));
        }
    }

    public void registerMapper(MapperBase mapper)
    {
        int prio = mapper.getPriority();
        for (int i = 0; i < mappers.size(); i++)
        {
            MapperBase existing = mappers.get(i);
            if (existing.getPriority() < prio)
            {
                mappers.add(i, mapper);
                return;
            }
            else if (existing.equals(mapper))
            {
                throw new IllegalArgumentException();
            }
        }
        mappers.add(mapper);
    }

    private MapperBase findTopFieldMapperForClass(Class<?> clazz)
    {
        for (MapperBase mapper : mappers)
        {
            if (mapper.canMapToField(clazz))
            {
                return mapper;
            }
        }

        return null;
    }

    private MapperBase findTopCompoundMapperForClass(Class<?> clazz)
    {
        for (MapperBase mapper : mappers)
        {
            if (mapper.canMapToMap(clazz))
            {
                return mapper;
            }
        }

        return null;
    }

    // ==============================================================================================================
    // Serializing
    public void serializeTo(GddlMap tag, String fieldName, Object object)
            throws ReflectiveOperationException
    {
        if (object != null)
        {
            MapperBase mapper = findTopFieldMapperForClass(object.getClass());
            if (mapper != null)
            {
                mapper.serializeField(tag, fieldName, object, this);
                return;
            }
        }

        generic.serializeField(tag, fieldName, object, this);
    }

    public GddlMap serialize(Object object)
            throws ReflectiveOperationException
    {
        if (object != null)
        {
            MapperBase mapper = findTopCompoundMapperForClass(object.getClass());
            if (mapper != null)
            {
                return mapper.serializeMap(object, this);
            }
        }

        return generic.serializeMap(object, this);
    }

    // ==============================================================================================================
    // Deserializing
    public <T> T deserialize(Class<? extends T> clazz, GddlMap tag)
            throws ReflectiveOperationException
    {
        MapperBase mapper = findTopCompoundMapperForClass(clazz);
        if (mapper != null)
        {
            return (T)mapper.deserializeMap(tag, clazz, this);
        }

        return (T)generic.deserializeMap(tag, clazz, this);
    }

    public Object deserializeFrom(GddlMap parent, String fieldName, Class<?> clazz, Object currentValue)
            throws ReflectiveOperationException
    {
        if (!parent.containsKey(fieldName))
            return currentValue;

        MapperBase mapper = findTopFieldMapperForClass(clazz);
        if (mapper != null)
        {
            return mapper.deserializeField(parent, fieldName, clazz, this);
        }

        return generic.deserializeField(parent, fieldName, clazz, this);
    }
}
