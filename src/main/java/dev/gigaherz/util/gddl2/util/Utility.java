package dev.gigaherz.util.gddl2.util;

import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Stream;

public final class Utility
{
    private Utility()
    {
        throw new IllegalStateException("This class is not instantiable.");
    }

    /**
     * Calculates the next power of two bigger than the given number
     *
     * @param n The number to calculate the magnitude of
     * @return The power of two number
     */
    public static int upperPower(int n)
    {
        // Ooooh... I just got how this works! Clever!
        // It's causing all the bits to spread downward
        // until all the bits below the most-significant 1
        // are also 1, then adds 1 to fill the power of two.

        n--;
        n |= (n >> 1);
        n |= (n >> 2);
        n |= (n >> 4);
        n |= (n >> 8);
        n |= (n >> 16);
        return (n + 1);
    }

    /**
     * Validates if the given string contains a sequence of characters that is a valid identifier in GDDL.
     *
     * @param text The string to validate
     * @return True if the string is a valid identifier
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isValidIdentifier(String text)
    {
        boolean first = true;

        for (char c : text.toCharArray())
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

    /**
     * Replaces any disallowed characters with escape codes, assuming a `"` delimiter.
     *
     * @param text The string to escape
     * @return The escaped string
     */
    public static String escapeString(String text)
    {
        return escapeString(text, '"');
    }

    /**
     * Replaces any disallowed characters with escape codes, using the given delimiter as a disallowed character.
     *
     * @param text      The string to escape
     * @param delimiter The delimiter that will surround the string
     * @return The escaped string
     */
    public static String escapeString(String text, char delimiter)
    {
        StringBuilder sb = new StringBuilder();

        sb.append(delimiter);
        for (char c : text.toCharArray())
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

    /**
     * Validates if a character is valid within a quoted string.
     *
     * @param c         The character
     * @param delimiter The delimiter used for the string
     * @return True if the character is valid
     */
    public static boolean isValidStringCharacter(char c, char delimiter)
    {
        return Utility.isPrintable(c) && !Utility.isControl(c) && c != delimiter && c != '\\';
    }

    private static final int UE_QUOTE = 1;
    private static final int UE_ESCAPE = 2;
    private static final int UE_ESCAPE_LB = 3;
    private static final int UE_ESCAPE_HEX = 5;
    private static final int UE_ESCAPE_END = 6;

    /**
     * Processes any escape sequences in the string, replacing them with the codepoints those sequences represent.
     *
     * @param text The text to unescape
     * @return The unescaped string
     */
    public static String unescapeString(String text)
    {
        StringBuilder sb = new StringBuilder();

        char startQuote = (char) 0;

        int state = UE_QUOTE;
        int escapeAcc = 0;
        int escapeDigits = 0;
        int escapeMax = 0;

        for (char c : text.toCharArray())
        {
            if (startQuote != 0)
            {
                if (state == UE_ESCAPE_HEX)
                {
                    if (escapeDigits == escapeMax)
                    {
                        sb.append((char) escapeAcc);
                        state = UE_QUOTE;
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
                        state = UE_QUOTE;
                    }
                    escapeDigits++;
                }

                if (state == UE_ESCAPE_LB)
                {
                    if (c == '\n')
                    {
                        sb.append('\n');
                        state = UE_ESCAPE_END;
                    }
                    else
                        state = UE_QUOTE;
                }
                else if (state == UE_ESCAPE_END)
                {
                    state = UE_QUOTE;
                }

                if (state == UE_ESCAPE)
                {
                    switch (c)
                    {
                        case '"':
                            sb.append('"');
                            state = UE_ESCAPE_END;
                            break;
                        case '\'':
                            sb.append('\'');
                            state = UE_ESCAPE_END;
                            break;
                        case '\\':
                            sb.append('\\');
                            state = UE_ESCAPE_END;
                            break;
                        case '0':
                            sb.append('\0');
                            state = UE_ESCAPE_END;
                            break;
                        case 'b':
                            sb.append('\b');
                            state = UE_ESCAPE_END;
                            break;
                        case 't':
                            sb.append('\t');
                            state = UE_ESCAPE_END;
                            break;
                        case 'f':
                            sb.append('\f');
                            state = UE_ESCAPE_END;
                            break;
                        case 'r':
                            sb.append('\r');
                            state = UE_ESCAPE_END;
                            break;
                        case 'n':
                        case '\n':
                            sb.append('\n');
                            state = UE_ESCAPE_END;
                            break;
                        case '\r':
                            sb.append('\r');
                            state = UE_ESCAPE_LB;
                            break;
                        case 'x':
                            state = UE_ESCAPE_HEX;
                            escapeAcc = 0;
                            escapeDigits = 0;
                            escapeMax = 2;
                            break;
                        case 'u':
                            state = UE_ESCAPE_HEX;
                            escapeAcc = 0;
                            escapeDigits = 0;
                            escapeMax = 4;
                            break;
                    }
                }

                if (state == UE_QUOTE)
                {
                    if (c == startQuote)
                        return sb.toString();
                    if (c == '\\')
                    {
                        state = UE_ESCAPE;
                    }
                    else
                    {
                        sb.append(c);
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

    private static final int NON_PRINTABLE =
            (1 << Character.LINE_SEPARATOR) |
                    (1 << Character.PARAGRAPH_SEPARATOR) |
                    (1 << Character.CONTROL) |
                    (1 << Character.PRIVATE_USE) |
                    (1 << Character.FORMAT) |
                    (1 << Character.SURROGATE);

    /**
     * Determines if a character is printable.
     * A printable character is a character that can be used for display.
     * Non-printable characters are line separators, paragraph separators, other control characters,
     * codepoints representing the (unmatched) halves of a surrogate pair, and private use characters.
     *
     * @param c The character
     * @return True if the character is deemed printable
     */
    public static boolean isPrintable(char c)
    {
        return ((NON_PRINTABLE >> Character.getType(c)) & 1) == 0;
    }

    /**
     * Determines if a character is a letter, as per the unicode rules.
     * See {@link Character#isLetter(char)}
     *
     * @param c The character
     * @return True if the character is a letter
     */
    public static boolean isLetter(int c)
    {
        return Character.isLetter(c);
    }

    /**
     * Determines if a character is a numeric digit, as per the unicode rules.
     * See {@link Character#isDigit(char)}
     *
     * @param c The character
     * @return True if the character is a digit
     */
    public static boolean isDigit(int c)
    {
        return Character.isDigit(c);
    }

    /**
     * Determines if a character is a control character, as per the unicode rules.
     * See {@link Character#isISOControl(char)}
     *
     * @param c The character
     * @return True if the character is a control character
     */
    public static boolean isControl(int c)
    {
        return Character.isISOControl(c);
    }

    /**
     * Joins the array of objects with the given separator in between elements.
     *
     * @param separator The text to use between elements
     * @param elements  The array of objects to join
     * @param <T>       The type of object
     * @return A string with the joined elements
     */
    public static <T> String join(CharSequence separator, T[] elements)
    {
        return join(separator, Arrays.stream(elements));
    }

    /**
     * Joins the stream of objects with the given separator in between elements.
     *
     * @param separator The text to use between elements
     * @param stream    The objects to join
     * @param <T>       The type of object
     * @return A string with the joined elements
     */
    public static <T> String join(CharSequence separator, Stream<T> stream)
    {
        return join(separator, stream.iterator());
    }

    /**
     * Joins the sequence of objects with the given separator in between elements.
     *
     * @param separator The text to use between elements
     * @param iterator  The objects to join
     * @param <T>       The type of object
     * @return A string with the joined elements
     */
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

    /**
     * Determines whether the given string is null or the empty string
     *
     * @param string The string
     * @return True if the string is either null or the empty string
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isNullOrEmpty(@Nullable String string)
    {
        return string == null || string.length() == 0;
    }

    public static <T> boolean listEquals(List<T> a, List<T> b)
    {
        if (a.size() != b.size())
            return false;

        for (int i = 0; i < a.size(); i++)
        {
            if (!Objects.equals(a.get(i), b.get(i)))
                return false;
        }

        return true;
    }
}
