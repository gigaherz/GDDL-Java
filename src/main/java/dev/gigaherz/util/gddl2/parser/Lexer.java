package dev.gigaherz.util.gddl2.parser;

import dev.gigaherz.util.gddl2.exceptions.LexerException;
import dev.gigaherz.util.gddl2.util.ArrayQueue;
import dev.gigaherz.util.gddl2.util.Utility;

import java.io.IOException;

public class Lexer implements TokenProvider, AutoCloseable
{
    //region API
    public Lexer(Reader r)
    {
        reader = r;
    }

    @Override
    public WhitespaceMode getWhitespaceMode()
    {
        return whitespaceMode;
    }

    @Override
    public void setWhitespaceMode(WhitespaceMode whitespaceMode)
    {
        this.whitespaceMode = whitespaceMode;
    }

    @Override
    public TokenType peek(int pos) throws LexerException, IOException
    {
        require(pos + 1);

        return lookAhead.get(pos).type;
    }

    @Override
    public TokenType peek() throws LexerException, IOException
    {
        require(1);

        return lookAhead.get(0).type;
    }

    @Override
    public Token pop() throws LexerException, IOException
    {
        require(2);

        return lookAhead.remove();
    }
    //endregion

    //region Implementation
    private final ArrayQueue<Token> lookAhead = new ArrayQueue<>();
    private final StringBuilder whitespaceBuilder = new StringBuilder();
    private final StringBuilder commentBuilder = new StringBuilder();

    private final Reader reader;

    private WhitespaceMode whitespaceMode;

    private boolean seenEnd = false;

    private void require(int count) throws LexerException, IOException
    {
        int needed = count - lookAhead.size();
        if (needed > 0)
        {
            readAhead(needed);
        }
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
        ParsingContext startContext = reader.getParsingContext();

        if (seenEnd)
        {
            return makeEndToken(startContext, "", "");
        }

        int ich = reader.peek();

        whitespaceBuilder.setLength(0);
        commentBuilder.setLength(0);
        boolean commentStarted = false;
        commentLoop:
        while (true)
        {
            if (ich < 0) break;

            switch (ich)
            {
                case ' ':
                case '\t':
                {
                    char cch = (char) ich;
                    whitespaceBuilder.append(cch);
                    if (commentStarted)
                        commentBuilder.append(cch);
                    reader.skip(1);
                    ich = reader.peek();
                    break;
                }
                case '\r':
                case '\n':
                {
                    char cch = (char) ich;
                    whitespaceBuilder.append(cch);
                    if (commentStarted)
                        commentBuilder.append(cch);
                    reader.skip(1);
                    ich = reader.peek();
                    if (cch == '\r' && ich == '\n')
                    {
                        cch = (char) ich;
                        whitespaceBuilder.append(cch);
                        if (commentStarted)
                            commentBuilder.append(cch);
                        reader.skip(1);
                        ich = reader.peek();
                    }
                    commentStarted = false;
                    break;
                }
                case '#':
                {
                    char cch = (char) ich;
                    whitespaceBuilder.append(cch);
                    if (!commentStarted)
                    {
                        commentStarted = true;
                    }
                    else
                    {
                        commentBuilder.append(cch);
                    }
                    reader.skip(1);
                    ich = reader.peek();
                    break;
                }
                default:
                {
                    if (!commentStarted)
                    {
                        break commentLoop;
                    }
                    else
                    {
                        char cch = (char) ich;
                        whitespaceBuilder.append(cch);
                        commentBuilder.append(cch);
                        reader.skip(1);
                        ich = reader.peek();
                    }
                    break;
                }
            }
        }

        String comment = commentBuilder.length() > 0 ? commentBuilder.toString() : "";
        String whitespace = whitespaceBuilder.length() > 0 ? whitespaceBuilder.toString() : "";

        if (ich < 0)
            return makeEndToken(startContext, comment, whitespace);

        switch (ich)
        {
            case '{':
                return new Token(TokenType.L_BRACE, reader.read(1), startContext, comment, whitespace);
            case '}':
                return new Token(TokenType.R_BRACE, reader.read(1), startContext, comment, whitespace);
            case '[':
                return new Token(TokenType.L_BRACKET, reader.read(1), startContext, comment, whitespace);
            case ']':
                return new Token(TokenType.R_BRACKET, reader.read(1), startContext, comment, whitespace);
            case ',':
                return new Token(TokenType.COMMA, reader.read(1), startContext, comment, whitespace);
            case ':':
                return new Token(TokenType.COLON, reader.read(1), startContext, comment, whitespace);
            case '=':
                return new Token(TokenType.EQUALS, reader.read(1), startContext, comment, whitespace);
        }

        if (Utility.isLetter(ich) || ich == '_')
        {
            int number = 1;
            while (true)
            {
                ich = reader.peek(number);
                if (ich < 0)
                    break;

                if (Utility.isLetter(ich) || Utility.isDigit(ich) || ich == '_')
                {
                    number++;
                }
                else
                {
                    break;
                }
            }

            Token id = new Token(TokenType.IDENTIFIER, reader.read(number), startContext, comment, whitespace);

            if (id.text.compareToIgnoreCase("nil") == 0)
                return new Token(TokenType.NIL, id.text, id, comment, whitespace);
            if (id.text.compareToIgnoreCase("null") == 0)
                return new Token(TokenType.NULL, id.text, id, comment, whitespace);
            if (id.text.compareToIgnoreCase("true") == 0)
                return new Token(TokenType.TRUE, id.text, id, comment, whitespace);
            if (id.text.compareToIgnoreCase("false") == 0)
                return new Token(TokenType.FALSE, id.text, id, comment, whitespace);

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

            return new Token(TokenType.STRING, reader.read(number), startContext, comment, whitespace);
        }

        if (Utility.isDigit(ich) || ich == '.' || ich == '+' || ich == '-')
        {
            // numbers
            int number = 0;
            boolean fractional = false;

            if (ich == '.' && reader.peek(number + 1) == 'N' && reader.peek(number + 2) == 'a' && reader.peek(number + 3) == 'N')
            {
                return new Token(TokenType.DOUBLE, reader.read(number + 4), startContext, comment, whitespace);
            }

            if (ich == '-' || ich == '+')
            {
                number++;

                ich = reader.peek(number);
            }

            if (ich == '.' && reader.peek(number + 1) == 'I' && reader.peek(number + 2) == 'n' && reader.peek(number + 3) == 'f')
            {
                return new Token(TokenType.DOUBLE, reader.read(number + 4), startContext, comment, whitespace);
            }

            if (Utility.isDigit(ich))
            {
                if (reader.peek(number) == '0' && reader.peek(number + 1) == 'x')
                {
                    number += 2;

                    ich = reader.peek(number);
                    while (Utility.isDigit(ich) || (ich >= 'a' && ich <= 'f') || (ich >= 'A' && ich <= 'F'))
                    {
                        number++;

                        ich = reader.peek(number);
                    }

                    return new Token(TokenType.HEX_INT, reader.read(number), startContext, comment, whitespace);
                }

                number = 1;
                ich = reader.peek(number);
                while (Utility.isDigit(ich))
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

                while (Utility.isDigit(ich))
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

                if (!Utility.isDigit(ich))
                    throw new LexerException(this, String.format("Expected DIGIT, found %c", ich));

                while (Utility.isDigit(ich))
                {
                    number++;

                    ich = reader.peek(number);
                }
            }

            if (fractional)
                return new Token(TokenType.DOUBLE, reader.read(number), startContext, comment, whitespace);

            return new Token(TokenType.INTEGER, reader.read(number), startContext, comment, whitespace);
        }

        throw new LexerException(this, String.format("Unexpected character: %c", reader.peek()));
    }

    private Token makeEndToken(ParsingContext startContext, String comment, String whitespace)
    {
        seenEnd = true;
        return new Token(TokenType.END, "", startContext, comment, whitespace);
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
                if (Utility.isControl(ich))
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
            if (Utility.isDigit(ich) || (ich >= 'a' && ich <= 'f') || (ich >= 'A' && ich <= 'F'))
            {
                number++;

                ich = reader.peek(number);
                if (Utility.isDigit(ich) || (ich >= 'a' && ich <= 'f') || (ich >= 'A' && ich <= 'F'))
                {
                    number++;

                    ich = reader.peek(number);
                    if (Utility.isDigit(ich) || (ich >= 'a' && ich <= 'f') || (ich >= 'A' && ich <= 'F'))
                    {
                        number++;

                        ich = reader.peek(number);
                        if (Utility.isDigit(ich) || (ich >= 'a' && ich <= 'f') || (ich >= 'A' && ich <= 'F'))
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
    //endregion

    //region toString
    @Override
    public String toString()
    {
        return String.format("{Lexer ahead=%s, reader=%s}", Utility.join(", ", lookAhead.elements()), reader);
    }
    //endregion

    //region ContextProvider
    @Override
    public ParsingContext getParsingContext()
    {
        if (lookAhead.size() > 0)
            return lookAhead.get(0).context;
        return reader.getParsingContext();
    }
    //endregion

    //region AutoCloseable
    @Override
    public void close() throws IOException
    {
        reader.close();
    }
    //endregion
}
