package dev.gigaherz.util.gddl2.dynamic;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import dev.gigaherz.util.gddl2.structure.GddlElement;
import dev.gigaherz.util.gddl2.structure.GddlList;
import dev.gigaherz.util.gddl2.structure.GddlMap;
import dev.gigaherz.util.gddl2.structure.GddlValue;

import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Stream;

/**
 * GDDLOps is an implementation of an encode/decode backend for the serialization side of Mojang's DataFixerUpper library.
 * This library allows users to define arbitrary serialization specifications in the form of Codecs which can
 * transform an in-memory data structure into a serialization format and back. *
 * Combined with GDDLOps, this allows using GDDL as a serialization format for arbitrary objects.
 * This is not an automatic serializer, it does not enumerate fields via reflection, and does not automatically generate
 * Codecs from objects.
 */
public final class GDDLOps implements DynamicOps<GddlElement<?>>
{
    public static final GDDLOps INSTANCE = new GDDLOps();

    private GDDLOps()
    {
    }

    @Override
    public GddlElement<?> empty()
    {
        return GddlValue.nullValue();
    }

    @Override
    public GddlElement<?> emptyMap()
    {
        return GddlList.empty();
    }

    @Override
    public GddlElement<?> emptyList()
    {
        return GddlList.empty();
    }

    @Override
    public <U> U convertTo(DynamicOps<U> outOps, GddlElement<?> input)
    {
        input = input.resolvedValue();

        if (input.isNull())
            return outOps.empty();
        if (input.isString())
            return outOps.createString(input.stringValue());
        if (input.isBoolean())
            return outOps.createBoolean(input.booleanValue());
        if (input.isInteger())
        {
            var l = input.intValue();
            if ((byte) l == l)
            {
                return outOps.createByte((byte) l);
            }
            if ((short) l == l)
            {
                return outOps.createShort((short) l);
            }
            if ((int) l == l)
            {
                return outOps.createInt((int) l);
            }
            return outOps.createLong(l);
        }
        if (input.isDouble())
        {
            var d = input.doubleValue();
            if ((float) d == d)
            {
                return outOps.createFloat((float) d);
            }
            return outOps.createDouble(d);
        }
        if (input.isMap())
            return convertMap(outOps, input.asMap());
        if (input.isList())
            return convertList(outOps, input.asList());

        if (input.isReference())
            throw new IllegalStateException("Unsupported conversion of unresolved reference");

        if (input.isValue())
            throw new IllegalStateException("Unimplemented value type " + input);

        throw new IllegalStateException("Unknown input type " + input.getClass());
    }

    @Override
    public DataResult<Number> getNumberValue(GddlElement<?> input)
    {
        if (input.isInteger())
            return DataResult.success(input.intValue());
        else if (input.isDouble())
            return DataResult.success(input.doubleValue());
        return DataResult.error("Not a number");
    }

    @Override
    public GddlElement<?> createByte(byte value)
    {
        return GddlValue.of(value);
    }

    @Override
    public GddlElement<?> createShort(short value)
    {
        return GddlValue.of(value);
    }

    @Override
    public GddlElement<?> createInt(int value)
    {
        return GddlValue.of(value);
    }

    @Override
    public GddlElement<?> createLong(long value)
    {
        return GddlValue.of(value);
    }

    @Override
    public GddlElement<?> createFloat(float value)
    {
        return GddlValue.of(value);
    }

    @Override
    public GddlElement<?> createDouble(double value)
    {
        return GddlValue.of(value);
    }

    @Override
    public DataResult<Boolean> getBooleanValue(GddlElement<?> input)
    {
        if (input.isBoolean())
            return DataResult.success(input.booleanValue());
        return DataResult.error("Not a boolean");
    }

    @Override
    public GddlElement<?> createBoolean(boolean value)
    {
        return GddlValue.of(value);
    }

    @Override
    public GddlElement<?> createNumeric(Number i)
    {
        if (i instanceof Double || i instanceof Float || i instanceof BigDecimal)
        {
            double d = i.doubleValue();
            return GddlValue.of(d);
        }

        long l = i.longValue();
        return GddlValue.of(l);
    }

    @Override
    public DataResult<String> getStringValue(GddlElement<?> input)
    {
        if (input.isString())
            return DataResult.success(input.stringValue());
        return DataResult.error("Not a string");
    }

    @Override
    public GddlElement<?> createString(String value)
    {
        return GddlValue.of(value);
    }

    @Override
    public DataResult<GddlElement<?>> mergeToList(GddlElement<?> list, GddlElement<?> value)
    {
        return asNewList(list).map(c -> {
            c.add(value.copy());
            return c;
        });
    }

    private DataResult<GddlList> asNewList(GddlElement<?> element)
    {
        if (element.isNull())
            return DataResult.success(GddlList.empty());
        else if (element.isList())
            return DataResult.success(element.asList().copy());
        else
            return DataResult.error("Not a list");
    }

    @Override
    public DataResult<GddlElement<?>> mergeToMap(GddlElement<?> map, GddlElement<?> key, GddlElement<?> value)
    {
        if (!key.isString())
            return DataResult.error("Key is not a string");
        return asNewMap(map).map(c -> {
            c.put(key.stringValue(), value);
            return c;
        });
    }

    private DataResult<GddlMap> asNewMap(GddlElement<?> element)
    {
        if (element.isNull())
            return DataResult.success(GddlMap.empty());
        else if (element.isMap())
            return DataResult.success(element.asMap().copy());
        else
            return DataResult.error("Not a map");
    }

    @Override
    public DataResult<Stream<Pair<GddlElement<?>, GddlElement<?>>>> getMapValues(GddlElement<?> input)
    {
        if (!input.isMap())
            return DataResult.error("Not a map");

        return DataResult.success(
                input.asMap().entrySet().stream()
                        .map(
                                e -> Pair.of(GddlValue.of(e.getKey()), e.getValue())
                        )
        );
    }

    @Override
    public GddlElement<?> createMap(Stream<Pair<GddlElement<?>, GddlElement<?>>> map)
    {
        var c = GddlMap.empty();
        map.forEach(kv -> c.put(kv.getFirst().stringValue(), kv.getSecond()));
        return c;
    }

    @Override
    public GddlElement<?> createMap(Map<GddlElement<?>, GddlElement<?>> map)
    {
        var c = GddlMap.empty();
        map.forEach((k, v) -> c.put(k.stringValue(), v));
        return c;
    }

    @Override
    public DataResult<Stream<GddlElement<?>>> getStream(GddlElement<?> input)
    {
        if (input.isList())
            return DataResult.success(input.asList().stream());
        return DataResult.error("Not a list");
    }

    @Override
    public GddlElement<?> createList(Stream<GddlElement<?>> input)
    {
        GddlList c = GddlList.empty();
        input.forEach(c::add);
        return c;
    }

    @Override
    public GddlElement<?> remove(GddlElement<?> input, String key)
    {
        if (input.isMap())
        {
            GddlMap c = input.asMap();
            if (c.containsKey(key))
            {
                c = c.copy();
                c.remove(key);
                return c;
            }
        }
        return input;
    }
}
