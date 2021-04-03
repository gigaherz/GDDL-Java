package gigaherz.util.gddl2.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.StringJoiner;
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

    private static final int NON_PRINTABLE =
            (1<<Character.LINE_SEPARATOR) |
                    (1<<Character.PARAGRAPH_SEPARATOR) |
                    (1<<Character.CONTROL) |
                    (1<<Character.PRIVATE_USE) |
                    (1<<Character.SURROGATE);

    public static boolean isPrintable(char c)
    {
        return ((NON_PRINTABLE>>Character.getType(c))&1) == 0;
    }

    public static <T> String join(CharSequence separator, T[] elements)
    {
        return join(separator, Arrays.stream(elements));
    }

    public static <T> String join(CharSequence separator, Stream<T> stream)
    {
        return join(separator, stream.iterator());
    }

    public static <T> String join(CharSequence separator, Iterable<T> iterable)
    {
        return join(separator, iterable.iterator());
    }

    public static <T> String join(CharSequence separator, Iterator<T> iterator)
    {
        StringJoiner joiner = new StringJoiner(separator);
        while (iterator.hasNext())
        {
            T e = iterator.next();
            joiner.add(e.toString());
        }
        return joiner.toString();
    }

    public static boolean isNullOrEmpty(String comment)
    {
        return comment == null || comment.equals("");
    }
}
