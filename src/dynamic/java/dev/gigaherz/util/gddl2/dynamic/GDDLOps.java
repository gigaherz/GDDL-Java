package dev.gigaherz.util.gddl2.dynamic;

import com.google.common.base.Objects;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import dev.gigaherz.util.gddl2.structure.Collection;
import dev.gigaherz.util.gddl2.structure.Element;
import dev.gigaherz.util.gddl2.structure.Value;

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
public final class GDDLOps implements DynamicOps<Element<?>>
{
    public static final GDDLOps INSTANCE = new GDDLOps();

    private GDDLOps()
    {
    }

    @Override
    public Element<?> empty()
    {
        return Value.nullValue();
    }

    @Override
    public Element<?> emptyMap()
    {
        return Collection.empty();
    }

    @Override
    public Element<?> emptyList()
    {
        return Collection.empty();
    }

    @Override
    public <U> U convertTo(DynamicOps<U> outOps, Element<?> input)
    {
        return input.resolvedValue().<U>when()
                .mapCollection(c -> {
                    if (c.hasNames())
                    {
                        return convertMap(outOps, c);
                    }
                    else
                    {
                        return convertList(outOps, c);
                    }
                })
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
    public DataResult<Number> getNumberValue(Element<?> input)
    {
        if (input.isInteger())
            return DataResult.success(input.asInteger());
        else if (input.isDouble())
            return DataResult.success(input.asDouble());
        return DataResult.error("Not a number");
    }

    @Override
    public Element<?> createByte(byte value)
    {
        return Value.of(value);
    }

    @Override
    public Element<?> createShort(short value)
    {
        return Value.of(value);
    }

    @Override
    public Element<?> createInt(int value)
    {
        return Value.of(value);
    }

    @Override
    public Element<?> createLong(long value)
    {
        return Value.of(value);
    }

    @Override
    public Element<?> createFloat(float value)
    {
        return Value.of(value);
    }

    @Override
    public Element<?> createDouble(double value)
    {
        return Value.of(value);
    }

    @Override
    public DataResult<Boolean> getBooleanValue(Element<?> input)
    {
        if (input.isBoolean())
            return DataResult.success(input.asBoolean());
        return DataResult.error("Not a boolean");
    }

    @Override
    public Element<?> createBoolean(boolean value)
    {
        return Value.of(value);
    }

    @Override
    public Element<?> createNumeric(Number i)
    {
        if (i instanceof Double || i instanceof Float || i instanceof BigDecimal)
        {
            double d = i.doubleValue();
            return Value.of(d);
        }

        long l = i.longValue();
        return Value.of(l);
    }

    @Override
    public DataResult<String> getStringValue(Element<?> input)
    {
        if (input.isString())
            return DataResult.success(input.asString());
        return DataResult.error("Not a string");
    }

    @Override
    public Element<?> createString(String value)
    {
        return Value.of(value);
    }

    @Override
    public DataResult<Element<?>> mergeToList(Element<?> list, Element<?> value)
    {
        Collection c;
        if (list.isNull())
        {
            c = Collection.empty();
        }
        else if (list.isCollection())
        {
            c = list.asCollection().copy();
        }
        else
        {
            return DataResult.error("Not a list");
        }
        c.add(value.copy());
        return DataResult.success(c);
    }

    @Override
    public DataResult<Element<?>> mergeToMap(Element<?> map, Element<?> key, Element<?> value)
    {
        Collection c;
        if (map.isNull())
        {
            c = Collection.empty();
        }
        else if (map.isCollection())
        {
            c = map.asCollection().copy();
        }
        else
        {
            return DataResult.error("Not a list");
        }
        if (!key.isString())
            return DataResult.error("Key is not a string");
        c.add(value.withName(key.asString()));
        return DataResult.success(c);
    }

    @Override
    public DataResult<Stream<Pair<Element<?>, Element<?>>>> getMapValues(Element<?> input)
    {
        if (!input.isCollection())
            return DataResult.error("Not a map");

        return DataResult.success(
                input.asCollection().stream()
                        .filter(Element::hasName)
                        .map(
                            e -> Pair.of(Value.of(e.getName()), e)
                        )
        );
    }

    @Override
    public Element<?> createMap(Stream<Pair<Element<?>, Element<?>>> map)
    {
        Collection c = Collection.empty();
        map.forEach(kv -> {
            String value = kv.getFirst().asString();
            c.add(kv.getSecond().withName(value));
        });
        return c;
    }

    @Override
    public Element<?> createMap(Map<Element<?>, Element<?>> map)
    {
        Collection c = Collection.empty();
        for (Map.Entry<Element<?>, Element<?>> entry : map.entrySet())
        {
            String value = entry.getKey().asString();
            c.add(entry.getValue().withName(value));
        }
        return c;
    }

    @Override
    public DataResult<Stream<Element<?>>> getStream(Element<?> input)
    {
        if (input.isCollection())
            return DataResult.success(input.asCollection().stream());
        return DataResult.error("Not a list");
    }

    @Override
    public Element<?> createList(Stream<Element<?>> input)
    {
        Collection c = Collection.empty();
        input.forEach(c::add);
        return c;
    }

    @Override
    public Element<?> remove(Element<?> input, String key)
    {
        if (input.isCollection())
        {
            Collection c = input.asCollection();
            if (c.byName(key).count() > 0)
            {
                c = c.copy();
                c.removeIf(e -> Objects.equal(e.getName(), key));
                return c;
            }
        }
        return input;
    }
}
