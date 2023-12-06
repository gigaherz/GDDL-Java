package dev.gigaherz.util.gddl2.serialization;

import dev.gigaherz.util.gddl2.serialization.mappers.*;
import dev.gigaherz.util.gddl2.structure.GddlElement;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public class GddlSerializer
{
    public static final int PRIORITY_INTERFACE = Integer.MAX_VALUE;
    public static final int PRIORITY_USER = 0;
    public static final int PRIORITY_PRIMITIVE = -200;
    public static final int PRIORITY_COLLECTION = -300;

    private final List<Mapper> mappers = new ArrayList<Mapper>();
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
            registerMapper(new PassthroughMapper(PRIORITY_COLLECTION + 1));
        }
    }

    public void registerMapper(Mapper mapper)
    {
        int prio = mapper.getPriority();
        for (int i = 0; i < mappers.size(); i++)
        {
            Mapper existing = mappers.get(i);
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

    private Mapper findTopCompoundMapperForClass(Class<?> clazz)
    {
        for (Mapper mapper : mappers)
        {
            if (mapper.canApply(clazz))
            {
                return mapper;
            }
        }

        return null;
    }

    public GddlElement<?> serialize(Object object)
            throws ReflectiveOperationException
    {
        if (object != null)
        {
            Mapper mapper = findTopCompoundMapperForClass(object.getClass());
            if (mapper != null)
            {
                return mapper.serialize(object, this);
            }
        }

        return generic.serialize(object, this);
    }

    public GddlElement<?> serializeVerbose(Object object)
            throws ReflectiveOperationException
    {
        if (object != null)
        {
            Mapper mapper = findTopCompoundMapperForClass(object.getClass());
            if (mapper != null)
            {
                return mapper.serializeVerbose(object, this);
            }
        }

        return generic.serializeVerbose(object, this);
    }

    public <T> T deserialize(GddlElement<?> element, Class<? extends T> clazz)
            throws ReflectiveOperationException
    {
        Mapper mapper = findTopCompoundMapperForClass(clazz);
        if (mapper != null)
        {
            return (T)mapper.deserialize(element, clazz, this);
        }

        return (T)generic.deserialize(element, clazz, this);
    }

    public <T> T deserializeVerbose(GddlElement<?> element) throws ReflectiveOperationException
    {
        if (element.isNull())
            return null;

        var map = element.asMap();

        var className = map.getString("class");
        var clazz = Class.forName(className);

        Mapper mapper = findTopCompoundMapperForClass(clazz);
        if (mapper != null)
        {
            return (T)mapper.deserializeVerbose(map, clazz, this);
        }

        return (T)generic.deserializeVerbose(map, clazz, this);
    }
}
