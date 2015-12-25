package gigaherz.utils.GDDL;

import gigaherz.utils.GDDL.deque.IndexedDeque;
import gigaherz.utils.GDDL.exceptions.LexerException;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Stack;

public class Lexer implements FileContext
{
    final IndexedDeque<Token> lookAhead = new IndexedDeque<>();
    final Stack<Integer> prefixStack = new Stack<>();

    final Reader reader;

    boolean seenEnd = false;

    int prefixPos = 0;
    Token prefix;

    public Tokens prefix() { return prefix.Name; }

    public Lexer(Reader r)
    {
        reader = r;
    }

    public void BeginPrefixScan()
    {
        prefixStack.push(prefixPos);
    }

    public void NextPrefix() throws LexerException, IOException
    {
        Require(prefixPos + 1);

        prefix = lookAhead.get(prefixPos++);
    }

    public void EndPrefixScan()
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

    private void Require(int count) throws LexerException, IOException
    {
        int needed = count - lookAhead.size();
        if (needed > 0)
        {
            ReadAhead(needed);
        }
    }

    public Tokens Peek() throws LexerException, IOException
    {
        Require(1);

        return lookAhead.get(0).Name;
    }

    public Token Pop() throws LexerException, IOException
    {
        Require(2);

        return lookAhead.removeFirst();
    }

    private void ReadAhead(int needed) throws LexerException, IOException
    {
        while (needed-- > 0)
        {
            lookAhead.addLast(ParseOne());
        }
    }

    private Token ParseOne() throws LexerException, IOException
    {
        if (seenEnd)
            return new Token(Tokens.END, reader.getFileContext(), "");

        int ich = reader.Peek();
        blah: while (true)
        {
            if (ich < 0) return new Token(Tokens.END, reader.getFileContext(), "");

            switch (ich)
            {
                case ' ':
                case '\t':
                case '\r':
                case '\n':
                    reader.Drop(1);

                    ich = reader.Peek();
                    break;
                case '#':
                    // comment, skip until \r or \n
                    do
                    {
                        reader.Drop(1);

                        ich = reader.Peek();
                    }
                    while (ich > 0 && ich != '\n' && ich != '\r');
                    break;
                default:
                    break blah;
            }
        }

        switch (ich)
        {
            case '{': return new Token(Tokens.LBRACE, reader.getFileContext(), reader.Read(1));
            case '}': return new Token(Tokens.RBRACE, reader.getFileContext(), reader.Read(1));
            case ',': return new Token(Tokens.COMMA, reader.getFileContext(), reader.Read(1));
            case ':': return new Token(Tokens.COLON, reader.getFileContext(), reader.Read(1));
            case '=': return new Token(Tokens.EQUALS, reader.getFileContext(), reader.Read(1));
        }

        if (Character.isLetter((char)ich) || ich == '_')
        {
            int number = 1;
            while (true)
            {
                ich = reader.Peek(number);
                if (ich < 0)
                    break;

                if (Character.isLetter((char)ich) || Character.isDigit((char)ich) || ich == '_')
                {
                    number++;
                }
                else
                {
                    break;
                }
            }

            Token id = new Token(Tokens.IDENT, reader.getFileContext(), reader.Read(number));

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

            ich = reader.Peek(number);
            while (ich != startedWith && ich >= 0)
            {
                if (ich == '\\')
                {
                    number = count_escape_seq(number);
                }
                else
                {
                    if (ich == '\r')
                    {
                        throw new LexerException(this, String.format("Expected '\\r', found %s", DebugChar(ich)));
                    }
                    number++;
                }

                ich = reader.Peek(number);
            }

            if (ich != startedWith)
            {
                throw new LexerException(this, String.format("Expected '%c', found %s", startedWith, DebugChar(ich)));
            }

            number++;

            return new Token(Tokens.STRING, reader.getFileContext(), reader.Read(number));
        }

        if (Character.isDigit((char)ich) || ich == '.')
        {
            // numbers
            int number = 0;
            boolean fractional = false;

            if (Character.isDigit((char)ich))
            {
                if (reader.Peek(0) == '0' && reader.Peek(1) == 'x')
                {
                    number = 2;

                    ich = reader.Peek(number);
                    while (Character.isDigit((char)ich) || (ich >= 'a' && ich <= 'f') || (ich >= 'A' && ich <= 'F'))
                    {
                        number++;

                        ich = reader.Peek(number);
                    }

                    return new Token(Tokens.HEXINT, reader.getFileContext(), reader.Read(number));
                }

                number = 1;
                ich = reader.Peek(number);
                while (Character.isDigit((char)ich))
                {
                    number++;

                    ich = reader.Peek(number);
                }
            }

            if (ich == '.')
            {
                fractional = true;

                // skip the '.'
                number++;

                ich = reader.Peek(number);
                if (!Character.isDigit((char)ich))
                    throw new LexerException(this, String.format("Expected DIGIT, found %c", (char)ich));

                while (Character.isDigit((char)ich))
                {
                    number++;

                    ich = reader.Peek(number);
                }
            }

            if (ich == 'e' || ich == 'E')
            {
                fractional = true;

                // letter
                number++;

                ich = reader.Peek(number);
                if (ich == '+' || ich == '-')
                {
                    number++;

                    ich = reader.Peek(number);
                }

                if (!Character.isDigit((char)ich))
                    throw new LexerException(this, String.format("Expected DIGIT, found %c", ich));

                while (Character.isDigit((char)ich))
                {
                    number++;

                    ich = reader.Peek(number);
                }
            }

            if (fractional)
                return new Token(Tokens.DOUBLE, reader.getFileContext(), reader.Read(number));

            return new Token(Tokens.INTEGER, reader.getFileContext(), reader.Read(number));
        }

        throw new LexerException(this, String.format("Unexpected character: %c", reader.Peek()));
    }

    private String DebugChar(int ich)
    {
        if (ich < 0)
            return "EOF";

        switch (ich)
        {
            case 0: return "'\\0'";
            case 8: return "'\\b'";
            case 9: return "'\\t'";
            case 10: return "'\\n'";
            case 13: return "'\\r'";
            default:
                if(Character.isISOControl(ich))
                    return String.format("'\\u%04x'", ich);
                return String.format("'%c'", ich);
        }
    }

    private int count_escape_seq(int number) throws LexerException, IOException
    {
        int ich = reader.Peek(number);
        if (ich != '\\')
            throw new LexerException(this, "Internal Error");

        number++;

        ich = reader.Peek(number);
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

            ich = reader.Peek(number);
            if (Character.isDigit((char)ich) || (ich >= 'a' && ich <= 'f') || (ich >= 'A' && ich <= 'F'))
            {
                number++;

                ich = reader.Peek(number);
                if (Character.isDigit((char)ich) || (ich >= 'a' && ich <= 'f') || (ich >= 'A' && ich <= 'F'))
                {
                    number++;

                    ich = reader.Peek(number);
                    if (Character.isDigit((char)ich) || (ich >= 'a' && ich <= 'f') || (ich >= 'A' && ich <= 'F'))
                    {
                        number++;

                        ich = reader.Peek(number);
                        if (Character.isDigit((char)ich) || (ich >= 'a' && ich <= 'f') || (ich >= 'A' && ich <= 'F'))
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

    public ParseContext getFileContext()
    {
        if(lookAhead.size() > 0)
            return lookAhead.get(0).Context;
        return reader.getFileContext();
    }
}
