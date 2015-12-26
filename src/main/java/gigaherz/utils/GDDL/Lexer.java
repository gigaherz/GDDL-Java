package gigaherz.utils.GDDL;

import gigaherz.utils.GDDL.deque.IndexedDeque;
import gigaherz.utils.GDDL.exceptions.LexerException;

import java.io.IOException;
import java.util.Stack;

public class Lexer implements ContextProvider
{
    final IndexedDeque<Token> lookAhead = new IndexedDeque<>();
    final Stack<Integer> prefixStack = new Stack<>();

    final Reader reader;

    boolean seenEnd = false;

    int prefixPos = 0;
    Token prefix;

    public Lexer(Reader r)
    {
        reader = r;
    }

    public Tokens prefix()
    {
        return prefix.Name;
    }

    public void beginPrefixScan()
    {
        prefixStack.push(prefixPos);
    }

    public void nextPrefix() throws LexerException, IOException
    {
        require(prefixPos + 1);

        prefix = lookAhead.get(prefixPos++);
    }

    public void endPrefixScan()
    {
        prefixPos = prefixStack.pop();

        if (prefixPos > 0)
        {
            prefix = lookAhead.get(prefixPos - 1);
        }
        else
        {
            prefix = null;
        }
    }

    private void require(int count) throws LexerException, IOException
    {
        int needed = count - lookAhead.size();
        if (needed > 0)
        {
            readAhead(needed);
        }
    }

    public Tokens peek() throws LexerException, IOException
    {
        require(1);

        return lookAhead.get(0).Name;
    }

    public Token pop() throws LexerException, IOException
    {
        require(2);

        return lookAhead.removeFirst();
    }

    private void readAhead(int needed) throws LexerException, IOException
    {
        while (needed-- > 0)
        {
            lookAhead.addLast(parseOne());
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

    public ParsingContext getParsingContext()
    {
        if (lookAhead.size() > 0)
            return lookAhead.get(0).Context;
        return reader.getParsingContext();
    }
}
