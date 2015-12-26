package gigaherz.utils.GDDL;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;

public class Utility
{
    // Ooooh... I just got how this works! Clever!
    // It's causing all the bits to spread downward
    // until all the bits below the most-significant 1
    // are also 1, then adds 1 to fill the power of two.
    public static int upperPower(int x)
    {
        x--;
        x |= (x >> 1);
        x |= (x >> 2);
        x |= (x >> 4);
        x |= (x >> 8);
        x |= (x >> 16);
        return (x + 1);
    }

    // TODO: improve this?
    public static <T> String joinStream(CharSequence separator, Stream<T> stream)
    {
        return joinCollection(separator, Arrays.asList(stream.toArray()));
    }

    public static <T> String joinCollection(CharSequence separator, Iterable<T> iterable)
    {
        StringBuilder b = new StringBuilder();
        boolean first = true;
        for (Object e : iterable)
        {
            if (!first) b.append(separator);
            b.append(e.toString());
            first = false;
        }
        return b.toString();
    }

    public static <TSource> int count(Iterable<TSource> source)
    {
        if (source == null)
        {
            throw new NullPointerException("source");
        }

        // Optimization for Collection<T>
        if (source instanceof Collection)
        {
            return ((Collection<TSource>) source).size();
        }

        int count = 0;
        for (TSource ignored : source)
        {
            count++;
        }
        return count;
    }
}
