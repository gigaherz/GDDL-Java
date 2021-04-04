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

    public static boolean isValidIdentifier(String ident)
    {
        boolean first = true;

        for (char c : ident.toCharArray())
        {
            if (!Utility.isLetter(c) && c != '_')
            {
                if (first || !Utility.isDigit(c))
                {
                    return false;
                }
            }

            first = false;
        }

        return true;
    }

    public static String escapeString(String p)
    {
        return escapeString(p, '"');
    }

    public static String escapeString(String p, char delimiter)
    {
        StringBuilder sb = new StringBuilder();

        sb.append(delimiter);
        for (char c : p.toCharArray())
        {
            if (isValidStringCharacter(c, delimiter))
            {
                sb.append(c);
                continue;
            }

            sb.append('\\');
            switch (c)
            {
                case '\b':
                    sb.append('b');
                    break;
                case '\t':
                    sb.append('t');
                    break;
                case '\n':
                    sb.append('n');
                    break;
                case '\f':
                    sb.append('f');
                    break;
                case '\r':
                    sb.append('r');
                    break;
                case '\"':
                    sb.append('\"');
                    break;
                case '\\':
                    sb.append('\\');
                    break;
                default:
                    if (c > 0xFF)
                        sb.append(String.format("u%04x", (int) c));
                    else
                        sb.append(String.format("x%02x", (int) c));
                    break;
            }
        }
        sb.append(delimiter);

        return sb.toString();
    }

    public static String unescapeString(String text)
    {
        StringBuilder sb = new StringBuilder();

        char startQuote = (char) 0;

        boolean inEscape = false;

        boolean inHexEscape = false;
        int escapeAcc = 0;
        int escapeDigits = 0;
        int escapeMax = 0;

        for (char c : text.toCharArray())
        {
            if (startQuote != 0)
            {
                if (inHexEscape)
                {
                    if (escapeDigits == escapeMax)
                    {
                        sb.append((char) escapeAcc);
                        inHexEscape = false;
                    }
                    else if (Utility.isDigit(c))
                    {
                        escapeAcc = (escapeAcc << 4) + (c - '0');
                    }
                    else if ((escapeDigits < escapeMax) && ((c >= 'a') && (c <= 'f')))
                    {
                        escapeAcc = (escapeAcc << 4) + 10 + (c - 'a');
                    }
                    else if ((escapeDigits < escapeMax) && ((c >= 'A') && (c <= 'F')))
                    {
                        escapeAcc = (escapeAcc << 4) + 10 + (c - 'A');
                    }
                    else
                    {
                        sb.append((char) escapeAcc);
                        inHexEscape = false;
                    }
                    escapeDigits++;
                }

                if (inEscape)
                {
                    switch (c)
                    {
                        case '"':
                            sb.append('"');
                            break;
                        case '\'':
                            sb.append('\'');
                            break;
                        case '\\':
                            sb.append('\\');
                            break;
                        case '0':
                            sb.append('\0');
                            break;
                        case 'b':
                            sb.append('\b');
                            break;
                        case 't':
                            sb.append('\t');
                            break;
                        case 'n':
                            sb.append('\n');
                            break;
                        case 'f':
                            sb.append('\f');
                            break;
                        case 'r':
                            sb.append('\r');
                            break;
                        case 'x':
                            inHexEscape = true;
                            escapeAcc = 0;
                            escapeDigits = 0;
                            escapeMax = 2;
                            break;
                        case 'u':
                            inHexEscape = true;
                            escapeAcc = 0;
                            escapeDigits = 0;
                            escapeMax = 4;
                            break;
                    }
                    inEscape = false;
                }
                else if (!inHexEscape)
                {
                    if (c == startQuote)
                        return sb.toString();
                    switch (c)
                    {
                        case '\\':
                            inEscape = true;
                            break;
                        default:
                            sb.append(c);
                            break;
                    }
                }
            }
            else
            {
                switch (c)
                {
                    case '"':
                        startQuote = '"';
                        break;
                    case '\'':
                        startQuote = '\'';
                        break;
                    default:
                        sb.append(c);
                        break;
                }
            }
        }

        throw new IllegalArgumentException("Invalid string literal");
    }

    public static boolean isValidStringCharacter(char c, char delimiter)
    {
        return Utility.isPrintable(c) && !Utility.isControl(c) && c != delimiter && c != '\\';
    }

    private static final int NON_PRINTABLE =
            (1 << Character.LINE_SEPARATOR) |
                    (1 << Character.PARAGRAPH_SEPARATOR) |
                    (1 << Character.CONTROL) |
                    (1 << Character.PRIVATE_USE) |
                    (1 << Character.SURROGATE);

    public static boolean isPrintable(char c)
    {
        return ((NON_PRINTABLE >> Character.getType(c)) & 1) == 0;
    }

    public static boolean isLetter(int c)
    {
        return Character.isLetter(c);
    }

    public static boolean isDigit(int c)
    {
        return Character.isDigit(c);
    }

    public static boolean isControl(int c)
    {
        return Character.isISOControl(c);
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
