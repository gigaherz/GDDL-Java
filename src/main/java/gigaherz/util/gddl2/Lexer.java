package gigaherz.util.gddl2;

import gigaherz.util.gddl2.exceptions.LexerException;
import gigaherz.util.gddl2.exceptions.ParserException;
import gigaherz.util.gddl2.util.QueueList;
import gigaherz.util.gddl2.util.Utility;

import java.io.IOException;

public class Lexer implements TokenProvider, AutoCloseable
{
    final QueueList<Token> lookAhead = new QueueList<>();

    final Reader reader;

    boolean seenEnd = false;

    public Lexer(Reader r)
    {
        reader = r;
    }

    private void require(int count) throws LexerException, IOException
    {
        int needed = count - lookAhead.size();
        if (needed > 0)
        {
            readAhead(needed);
        }
    }

    public TokenType peek(int pos) throws LexerException, IOException
    {
        require(pos + 1);

        return lookAhead.get(pos).name;
    }

    public TokenType peek() throws LexerException, IOException
    {
        require(1);

        return lookAhead.get(0).name;
    }

    public Token pop() throws LexerException, IOException
    {
        require(2);

        return lookAhead.remove();
    }

    private void readAhead(int needed) throws LexerException, IOException
    {
        while (needed-- > 0)
        {
            lookAhead.add(parseOne());
        }
    }

    private Token parseOne() throws LexerException, IOException
    {
        if (seenEnd)
            return new Token(TokenType.END, "", reader, "");

        StringBuilder commentLines = null;
        int ich = reader.peek();
        blah:
        while (true)
        {
            if (ich < 0) return new Token(TokenType.END, "", reader, "");

            switch (ich)
            {
                case ' ':
                case '\t':
                    reader.skip(1);
                    ich = reader.peek();
                    break;
                case '\r':
                case '\n':
                    if (commentLines != null)
                    {
                        commentLines.append(reader.read(1));
                    }
                    else
                    {
                        reader.skip(1);
                    }
                    ich = reader.peek();
                    break;
                case '#':
                {
                    // comment
                    if (commentLines == null)
                    {
                        commentLines = new StringBuilder();
                    }

                    reader.skip(1);
                    ich = reader.peek();

                    int number = 0;
                    while (ich > 0 && ich != '\n' && ich != '\r')
                    {
                        number++;
                        ich = reader.peek(number);
                    }

                    if (number > 0)
                    {
                        commentLines.append(reader.read(number));
                    }
                    ich = reader.peek();

                    break;
                }
                default:
                    break blah;
            }
        }

        String comment = commentLines != null ? commentLines.toString() : "";

        switch (ich)
        {
            case '{': return new Token(TokenType.LBRACE, reader.read(1), reader, comment);
            case '}': return new Token(TokenType.RBRACE, reader.read(1), reader, comment);
            case ',': return new Token(TokenType.COMMA, reader.read(1), reader, comment);
            case ':': return new Token(TokenType.COLON, reader.read(1), reader, comment);
            case '=': return new Token(TokenType.EQUALS, reader.read(1), reader, comment);
        }

        if (Character.isLetter((char) ich) || ich == '_')
        {
            int number = 1;
            while (true)
            {
                ich = reader.peek(number);
                if (ich < 0)
                    break;

                if (Character.isLetter((char) ich) || Character.isDigit((char) ich) || ich == '_')
                {
                    number++;
                }
                else
                {
                    break;
                }
            }

            Token id = new Token(TokenType.IDENT, reader.read(number), reader, comment);

            if (id.text.compareToIgnoreCase("nil") == 0) return new Token(TokenType.NIL, id.text, id, comment);
            if (id.text.compareToIgnoreCase("null") == 0) return new Token(TokenType.NULL, id.text, id, comment);
            if (id.text.compareToIgnoreCase("true") == 0) return new Token(TokenType.TRUE, id.text, id, comment);
            if (id.text.compareToIgnoreCase("false") == 0) return new Token(TokenType.FALSE, id.text, id, comment);

            return id;
        }

        if (ich == '"' || ich == '\'')
        {
            int startedWith = ich;
            int number = 1;

            ich = reader.peek(number);
            while (ich != startedWith && ich >= 0)
            {
                if (ich == '\\')
                {
                    number = countEscapeSeq(number);
                }
                else
                {
                    if (ich == '\r')
                    {
                        throw new LexerException(this, String.format("Expected '\\r', found %s", debugChar(ich)));
                    }
                    number++;
                }

                ich = reader.peek(number);
            }

            if (ich != startedWith)
            {
                throw new LexerException(this, String.format("Expected '%c', found %s", startedWith, debugChar(ich)));
            }

            number++;

            return new Token(TokenType.STRING, reader.read(number), reader, comment);
        }

        if (Character.isDigit((char) ich) || ich == '.')
        {
            // numbers
            int number = 0;
            boolean fractional = false;

            if (ich == '-')
            {
                number++;

                ich = reader.peek(number);
            }

            if (Character.isDigit((char) ich))
            {
                if (reader.peek(0) == '0' && reader.peek(1) == 'x')
                {
                    number = 2;

                    ich = reader.peek(number);
                    while (Character.isDigit((char) ich) || (ich >= 'a' && ich <= 'f') || (ich >= 'A' && ich <= 'F'))
                    {
                        number++;

                        ich = reader.peek(number);
                    }

                    return new Token(TokenType.HEXINT, reader.read(number), reader, comment);
                }

                number = 1;
                ich = reader.peek(number);
                while (Character.isDigit((char) ich))
                {
                    number++;

                    ich = reader.peek(number);
                }
            }

            if (ich == '.')
            {
                fractional = true;

                // skip the '.'
                number++;

                ich = reader.peek(number);
                if (!Character.isDigit((char) ich))
                    throw new LexerException(this, String.format("Expected DIGIT, found %c", (char) ich));

                while (Character.isDigit((char) ich))
                {
                    number++;

                    ich = reader.peek(number);
                }
            }

            if (ich == 'e' || ich == 'E')
            {
                fractional = true;

                // letter
                number++;

                ich = reader.peek(number);
                if (ich == '+' || ich == '-')
                {
                    number++;

                    ich = reader.peek(number);
                }

                if (!Character.isDigit((char) ich))
                    throw new LexerException(this, String.format("Expected DIGIT, found %c", ich));

                while (Character.isDigit((char) ich))
                {
                    number++;

                    ich = reader.peek(number);
                }
            }

            if (fractional)
                return new Token(TokenType.DOUBLE, reader.read(number), reader, comment);

            return new Token(TokenType.INTEGER, reader.read(number), reader, comment);
        }

        throw new LexerException(this, String.format("Unexpected character: %c", reader.peek()));
    }

    private String debugChar(int ich)
    {
        if (ich < 0)
            return "EOF";

        switch (ich)
        {
            case 0:
                return "'\\0'";
            case 8:
                return "'\\b'";
            case 9:
                return "'\\t'";
            case 10:
                return "'\\n'";
            case 13:
                return "'\\r'";
            default:
                if (Character.isISOControl(ich))
                    return String.format("'\\u%04x'", ich);
                return String.format("'%c'", ich);
        }
    }

    private int countEscapeSeq(int number) throws LexerException, IOException
    {
        int ich = reader.peek(number);
        if (ich != '\\')
            throw new LexerException(this, "Internal Error");

        number++;

        ich = reader.peek(number);
        switch (ich)
        {
            case '0':
            case 'b':
            case 'f':
            case 'n':
            case 'r':
            case 't':
            case '"':
            case '\'':
            case '\\':
                return ++number;
        }

        if (ich == 'x' || ich == 'u')
        {
            number++;

            ich = reader.peek(number);
            if (Character.isDigit((char) ich) || (ich >= 'a' && ich <= 'f') || (ich >= 'A' && ich <= 'F'))
            {
                number++;

                ich = reader.peek(number);
                if (Character.isDigit((char) ich) || (ich >= 'a' && ich <= 'f') || (ich >= 'A' && ich <= 'F'))
                {
                    number++;

                    ich = reader.peek(number);
                    if (Character.isDigit((char) ich) || (ich >= 'a' && ich <= 'f') || (ich >= 'A' && ich <= 'F'))
                    {
                        number++;

                        ich = reader.peek(number);
                        if (Character.isDigit((char) ich) || (ich >= 'a' && ich <= 'f') || (ich >= 'A' && ich <= 'F'))
                        {
                            number++;
                        }
                    }
                }
            }
            return number;
        }

        throw new LexerException(this, String.format("Unknown escape sequence \\%d", ich));
    }

    public String toString()
    {
        return String.format("{Lexer ahead=%s, reader=%s}", Utility.join(", ", lookAhead), reader);
    }

    public ParsingContext getParsingContext()
    {
        if (lookAhead.size() > 0)
            return lookAhead.get(0).context;
        return reader.getParsingContext();
    }

    @Override
    public void close() throws IOException
    {
        reader.close();
    }

    public static boolean isValidIdentifier(String ident)
    {
        boolean first = true;

        for (char c : ident.toCharArray())
        {
            if (!Character.isLetter(c) && c != '_')
            {
                if (first || !Character.isDigit(c))
                {
                    return false;
                }
            }

            first = false;
        }

        return true;
    }

    public static String unescapeString(Token t) throws ParserException
    {
        StringBuilder sb = new StringBuilder();

        char startQuote = (char) 0;

        boolean inEscape = false;

        boolean inHexEscape = false;
        int escapeAcc = 0;
        int escapeDigits = 0;
        int escapeMax = 0;

        for (char c : t.text.toCharArray())
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
                    else if (Character.isDigit(c))
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

        throw new ParserException(t, "Invalid string literal");
    }

    public static String escapeString(String p)
    {
        StringBuilder sb = new StringBuilder();

        sb.append('"');
        for (char c : p.toCharArray())
        {
            boolean printable = (c >= 32 && c < 127)
                    || Character.isWhitespace(c)
                    || Character.isAlphabetic(c)
                    || Character.isDigit(c)
                    || Character.isIdeographic(c);
            if (!Character.isISOControl(c) && printable && c != '"' && c != '\\')
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
        sb.append('"');

        return sb.toString();
    }
}
