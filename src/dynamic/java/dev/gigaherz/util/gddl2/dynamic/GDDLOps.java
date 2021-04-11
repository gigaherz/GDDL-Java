package dev.gigaherz.util.gddl2.dynamic;

import com.google.common.base.Objects;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import dev.gigaherz.util.gddl2.structure.GddlList;
import dev.gigaherz.util.gddl2.structure.GddlElement;
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
        return input.resolvedValue().<U>when()
                .mapMap(c -> convertMap(outOps, c))
                .mapList(c -> convertList(outOps, c))
                .mapNull(outOps::empty)
                .mapString(outOps::createString)
                .mapBoolean(outOps::createBoolean)
                .mapInteger(l -> {
                    if ((byte) l == l)
                    {
                        return outOps.createByte((byte)l);
                    }
                    if ((short) l == l)
                    {
                        return outOps.createShort((short)l);
                    }
                    if ((int) l == l)
                    {
                        return outOps.createInt((int)l);
                    }
                    return outOps.createLong(l);
                })
                .mapDouble(d -> {
                    if ((float) d == d)
                    {
                        return outOps.createFloat((float)d);
                    }
                    return outOps.createDouble(d);
                })
                .orElseThrow(() -> new IllegalStateException("Unresolved reference not supported."));
    }

    @Override
    public DataResult<Number> getNumberValue(GddlElement<?> input)
    {
        if (input.isInteger())
            return DataResult.success(input.asInteger());
        else if (input.isDouble())
            return DataResult.success(input.asDouble());
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
            return DataResult.success(input.asBoolean());
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
            return DataResult.success(input.asString());
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
        GddlList c;
        if (list.isNull())
            c = GddlList.empty();
        else if (list.isList())
            c = list.asList().copy();
        else return DataResult.error("Not a list");
        c.add(value.copy());
        return DataResult.success(c);
    }

    @Override
    public DataResult<GddlElement<?>> mergeToMap(GddlElement<?> map, GddlElement<?> key, GddlElement<?> value)
    {
        GddlMap c;
        if (map.isNull())
            c = GddlMap.empty();
        else if (map.isMap())
            c = map.asMap().copy();
        else return DataResult.error("Not a map");
        if (!key.isString())
            return DataResult.error("Key is not a string");
        c.put(key.asString(), value);
        return DataResult.success(c);
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
        map.forEach(kv -> c.put(kv.getFirst().asString(), kv.getSecond()));
        return c;
    }

    @Override
    public GddlElement<?> createMap(Map<GddlElement<?>, GddlElement<?>> map)
    {
        var c = GddlMap.empty();
        map.forEach((k,v) -> c.put(k.asString(), v));
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
