package gigaherz.utils.GDDL;

import gigaherz.utils.GDDL.exceptions.ParserException;
import gigaherz.utils.GDDL.util.QueueList;
import gigaherz.utils.GDDL.exceptions.LexerException;

import java.io.IOException;
import java.util.Stack;

public class Lexer implements ContextProvider
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

    public Tokens peek(int pos) throws LexerException, IOException
    {
        require(pos+1);

        return lookAhead.get(pos).Name;
    }

    public Tokens peek() throws LexerException, IOException
    {
        require(1);

        return lookAhead.get(0).Name;
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
            return new Token(Tokens.END, reader.getParsingContext(), "");

        int ich = reader.peek();
        blah:
        while (true)
        {
            if (ich < 0) return new Token(Tokens.END, reader.getParsingContext(), "");

            switch (ich)
            {
                case ' ':
                case '\t':
                case '\r':
                case '\n':
                    reader.skip(1);

                    ich = reader.peek();
                    break;
                case '#':
                    // comment, skip until \r or \n
                    do
                    {
                        reader.skip(1);

                        ich = reader.peek();
                    }
                    while (ich > 0 && ich != '\n' && ich != '\r');
                    break;
                default:
                    break blah;
            }
        }

        switch (ich)
        {
            case '{':
                return new Token(Tokens.LBRACE, reader.getParsingContext(), reader.read(1));
            case '}':
                return new Token(Tokens.RBRACE, reader.getParsingContext(), reader.read(1));
            case ',':
                return new Token(Tokens.COMMA, reader.getParsingContext(), reader.read(1));
            case ':':
                return new Token(Tokens.COLON, reader.getParsingContext(), reader.read(1));
            case '=':
                return new Token(Tokens.EQUALS, reader.getParsingContext(), reader.read(1));
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

            Token id = new Token(Tokens.IDENT, reader.getParsingContext(), reader.read(number));

            if (id.Text.compareToIgnoreCase("nil") == 0) return new Token(Tokens.NIL, id.Context, id.Text);
            if (id.Text.compareToIgnoreCase("null") == 0) return new Token(Tokens.NULL, id.Context, id.Text);
            if (id.Text.compareToIgnoreCase("true") == 0) return new Token(Tokens.TRUE, id.Context, id.Text);
            if (id.Text.compareToIgnoreCase("false") == 0) return new Token(Tokens.FALSE, id.Context, id.Text);

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

            return new Token(Tokens.STRING, reader.getParsingContext(), reader.read(number));
        }

        if (Character.isDigit((char) ich) || ich == '.')
        {
            // numbers
            int number = 0;
            boolean fractional = false;

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

                    return new Token(Tokens.HEXINT, reader.getParsingContext(), reader.read(number));
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
                return new Token(Tokens.DOUBLE, reader.getParsingContext(), reader.read(number));

            return new Token(Tokens.INTEGER, reader.getParsingContext(), reader.read(number));
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
        return String.format("{Lexer ahead=%s, reader=%s}", Utility.joinCollection(", ", lookAhead), reader);
    }

    public static boolean isValidIdentifier(String ident)
    {
        boolean first = true;

        for(char c : ident.toCharArray())
        {
            if(!Character.isLetter(c) && c != '_')
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

        for (char c : t.Text.toCharArray())
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
                else if(!inHexEscape)
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
            if (!Character.isISOControl(c) && printable  && c != '"' && c != '\\')
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
                    if(c > 0xFF)
                        sb.append(String.format("u%04x", (int) c));
                    else
                        sb.append(String.format("x%02x", (int) c));
                    break;
            }
        }
        sb.append('"');

        return sb.toString();
    }

    public ParsingContext getParsingContext()
    {
        if (lookAhead.size() > 0)
            return lookAhead.get(0).Context;
        return reader.getParsingContext();
    }
}
