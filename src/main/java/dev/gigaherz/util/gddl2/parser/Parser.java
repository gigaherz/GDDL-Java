package dev.gigaherz.util.gddl2.parser;

import dev.gigaherz.util.gddl2.exceptions.LexerException;
import dev.gigaherz.util.gddl2.exceptions.ParserException;
import dev.gigaherz.util.gddl2.structure.*;
import dev.gigaherz.util.gddl2.util.BasicIntStack;
import dev.gigaherz.util.gddl2.util.Utility;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

@SuppressWarnings("unused")
public class Parser implements ContextProvider, AutoCloseable
{
    //region API
    public Parser(TokenProvider lexer)
    {
        this.lexer = lexer;
    }

    /**
     * Gets the currently set whitespace processing mode.
     *
     * @return The current mode
     */
    public WhitespaceMode getWhitespaceMode()
    {
        return lexer.getWhitespaceMode();
    }

    /**
     * Changes the whitespace processing mode.
     *
     * @param whitespaceMode The new mode
     */
    public void setWhitespaceMode(WhitespaceMode whitespaceMode)
    {
        lexer.setWhitespaceMode(whitespaceMode);
    }

    /**
     * @return The lexer used by this parser
     */
    public TokenProvider getLexer()
    {
        return lexer;
    }

    /**
     * renderCuboids
     * Parses the whole file and returns the resulting root element.
     * Equivalent to {@link #parse(boolean)} with simplify=true
     *
     * @return The root element
     * @throws IOException     When accessing the source stream.
     * @throws ParserException When parsing
     */
    public GddlDocument parse() throws IOException, ParserException
    {
        return parse(true);
    }

    /**
     * Parses the whole file and returns the resulting root element.
     *
     * @param simplify If true, the structure
     * @return The root element
     * @throws IOException     When accessing the source stream.
     * @throws ParserException When parsing
     */
    public GddlDocument parse(boolean simplify) throws IOException, ParserException
    {
        var result = root();
        var root = result.getKey();

        if (simplify)
        {
            root.resolve(root);
            root = root.simplify();
        }

        var doc = new GddlDocument(root);
        doc.setDanglingComment(result.getValue());

        return doc;
    }
    //endregion

    //region Implementation
    int prefixPos = -1;
    final BasicIntStack prefixStack = new BasicIntStack();
    @SuppressWarnings("FieldCanBeLocal")
    private boolean finishedWithRBrace = false;
    private final TokenProvider lexer;

    private Token popExpected(TokenType... expected) throws ParserException, IOException
    {
        TokenType current = lexer.peek();
        if (Arrays.stream(expected).anyMatch(t -> current == t))
            return lexer.pop();

        if (expected.length != 1)
            throw new ParserException(this, String.format("Unexpected token %s. Expected one of: %s.", current, Utility.join(", ", expected)));

        throw new ParserException(this, String.format("Unexpected token %s. Expected: %s.", current, expected[0]));
    }

    private void beginPrefixScan()
    {
        prefixStack.push(prefixPos);
    }

    private TokenType nextPrefix() throws LexerException, IOException
    {
        return lexer.peek(++prefixPos);
    }

    private void endPrefixScan()
    {
        prefixPos = prefixStack.pop();
    }

    private boolean hasAny(TokenType... tokens) throws LexerException, IOException
    {
        TokenType prefix = nextPrefix();
        return Arrays.stream(tokens).anyMatch(t -> prefix == t);
    }

    private boolean prefixIdentifier() throws LexerException, IOException
    {
        beginPrefixScan();
        boolean r = hasAny(TokenType.IDENTIFIER);
        endPrefixScan();
        return r;
    }

    private boolean prefixReference() throws LexerException, IOException
    {
        beginPrefixScan();
        boolean r = hasAny(TokenType.COLON, TokenType.SLASH) && hasAny(TokenType.IDENTIFIER);
        endPrefixScan();

        return r || prefixIdentifier();
    }

    private GddlElement<?> element() throws ParserException, IOException
    {
        if (lexer.peek() == TokenType.NIL) return nullValue(popExpected(TokenType.NIL));
        if (lexer.peek() == TokenType.NULL) return nullValue(popExpected(TokenType.NULL));
        if (lexer.peek() == TokenType.TRUE) return booleanValue(popExpected(TokenType.TRUE));
        if (lexer.peek() == TokenType.FALSE) return booleanValue(popExpected(TokenType.FALSE));
        if (lexer.peek() == TokenType.INTEGER_LITERAL) return intValue(popExpected(TokenType.INTEGER_LITERAL));
        if (lexer.peek() == TokenType.HEX_INT_LITERAL) return hexIntValue(popExpected(TokenType.HEX_INT_LITERAL));
        if (lexer.peek() == TokenType.INTEGER_LITERAL) return intValue(popExpected(TokenType.INTEGER_LITERAL));
        if (lexer.peek() == TokenType.DECIMAL_LITERAL) return floatValue(popExpected(TokenType.DECIMAL_LITERAL));
        if (lexer.peek() == TokenType.STRING_LITERAL) return stringValue(popExpected(TokenType.STRING_LITERAL));
        if (prefixMap()) return map();
        if (prefixObject()) return object();
        if (prefixList()) return list();
        if (prefixReference()) return reference();

        throw new ParserException(this, "Internal Error");
    }

    private Map.Entry<GddlElement<?>, String> root() throws IOException, ParserException
    {
        GddlElement<?> E = element();
        var end = popExpected(TokenType.END);
        return Map.entry(E, end.comment);
    }

    private GddlReference reference() throws IOException, ParserException
    {
        boolean rooted = false;

        TokenType firstDelimiter = null;
        if (lexer.peek() == TokenType.COLON || lexer.peek() == TokenType.SLASH)
        {
            firstDelimiter = popExpected(TokenType.COLON, TokenType.SLASH).type;
            rooted = true;
        }

        Token component = pathComponent();
        GddlReference b = rooted ? GddlReference.absolute(component.text) : GddlReference.relative(component.text);
        b.setComment(component.comment);

        while (lexer.peek() == TokenType.COLON || lexer.peek() == TokenType.SLASH)
        {
            if (firstDelimiter != null && lexer.peek() != firstDelimiter)
                throw new ParserException(this, String.format("References must use consistent delimiters, expected %s, found %s instead", firstDelimiter, lexer.peek()));

            firstDelimiter = popExpected(TokenType.COLON, TokenType.SLASH).type;

            component = pathComponent();

            b.add(component.text);
        }

        return b;
    }

    private boolean prefixPathComponent() throws LexerException, IOException
    {
        beginPrefixScan();
        boolean r = hasAny(TokenType.IDENTIFIER, TokenType.STRING_LITERAL, TokenType.DOT, TokenType.DOUBLE_DOT);
        endPrefixScan();
        return r;
    }

    private Token pathComponent() throws ParserException, IOException
    {
        return popExpected(TokenType.IDENTIFIER, TokenType.STRING_LITERAL, TokenType.DOT, TokenType.DOUBLE_DOT);
    }

    private boolean prefixMap() throws LexerException, IOException
    {
        beginPrefixScan();
        boolean r = hasAny(TokenType.L_BRACE);
        endPrefixScan();
        return r;
    }

    private GddlMap map() throws ParserException, IOException
    {
        Token openBrace = popExpected(TokenType.L_BRACE);

        GddlMap s = GddlMap.empty();
        s.setComment(openBrace.comment);

        while (lexer.peek() != TokenType.R_BRACE)
        {
            finishedWithRBrace = false;


            Token name = popExpected(TokenType.IDENTIFIER, TokenType.STRING_LITERAL);

            String n = name.type == TokenType.IDENTIFIER ? name.text : unescapeString(name);

            popExpected(TokenType.EQUALS, TokenType.COLON);

            GddlElement<?> b = element();

            b.setComment(name.comment);
            b.setWhitespace(name.comment);
            s.put(n, b);

            if (lexer.peek() != TokenType.R_BRACE)
            {
                if (!finishedWithRBrace || (lexer.peek() == TokenType.COMMA))
                {
                    popExpected(TokenType.COMMA);
                }
            }
        }

        var end = popExpected(TokenType.R_BRACE);
        s.setTrailingComment(end.comment);

        finishedWithRBrace = true;

        return s;
    }

    private boolean prefixObject() throws LexerException, IOException
    {
        beginPrefixScan();
        boolean r = hasAny(TokenType.IDENTIFIER, TokenType.STRING_LITERAL) && hasAny(TokenType.L_BRACE);
        endPrefixScan();
        return r;
    }

    private GddlMap object() throws IOException, ParserException
    {
        Token type = name();

        if (!prefixMap())
            throw new ParserException(this, "Internal error");

        GddlMap s = map().withTypeName(type.text);

        s.setComment(type.comment);

        return s;
    }

    private boolean prefixList() throws LexerException, IOException
    {
        beginPrefixScan();
        boolean r = hasAny(TokenType.L_BRACKET);
        endPrefixScan();
        return r;
    }

    private GddlList list() throws ParserException, IOException
    {
        Token openBrace = popExpected(TokenType.L_BRACKET);

        GddlList s = GddlList.empty();
        s.setComment(openBrace.comment);

        while (lexer.peek() != TokenType.R_BRACKET)
        {
            finishedWithRBrace = false;

            s.add(element());

            if (lexer.peek() != TokenType.R_BRACKET)
            {
                if (!finishedWithRBrace || (lexer.peek() == TokenType.COMMA))
                {
                    popExpected(TokenType.COMMA);
                }
            }
        }

        var end = popExpected(TokenType.R_BRACKET);
        s.setTrailingComment(end.comment);

        finishedWithRBrace = true;

        return s;
    }

    private boolean prefixName() throws LexerException, IOException
    {
        beginPrefixScan();
        boolean r = hasAny(TokenType.IDENTIFIER, TokenType.STRING_LITERAL);
        endPrefixScan();
        return r;
    }

    private Token name() throws ParserException, IOException
    {
        return popExpected(TokenType.IDENTIFIER, TokenType.STRING_LITERAL);
    }

    private static GddlValue nullValue(Token token)
    {
        GddlValue e = GddlValue.nullValue();
        e.setComment(token.comment);
        return e;
    }

    private static GddlValue booleanValue(Token token)
    {
        GddlValue e = GddlValue.of(token.type == TokenType.TRUE);
        e.setComment(token.comment);
        return e;
    }

    private static GddlValue intValue(Token token)
    {
        GddlValue e = GddlValue.of(Long.parseLong(token.text));
        e.setComment(token.comment);
        return e;
    }

    private static GddlValue hexIntValue(Token token)
    {
        GddlValue e = GddlValue.of(Long.parseLong(token.text.substring(2), 16));
        e.setComment(token.comment);
        return e;
    }

    private static GddlValue floatValue(Token token)
    {
        double value;
        switch (token.text)
        {
            case ".NaN":
                value = Double.NaN;
                break;
            case ".Inf":
            case "+.Inf":
                value = Double.POSITIVE_INFINITY;
                break;
            case "-.Inf":
                value = Double.NEGATIVE_INFINITY;
                break;
            default:
                value = Double.parseDouble(token.text);
                break;
        }
        GddlValue e = GddlValue.of(value);
        e.setComment(token.comment);
        return e;
    }

    private static GddlValue stringValue(Token token) throws ParserException
    {
        GddlValue e = GddlValue.of(unescapeString(token));
        e.setComment(token.comment);
        return e;
    }

    private static String unescapeString(Token t) throws ParserException
    {
        try
        {
            return Utility.unescapeString(t.text);
        }
        catch (IllegalArgumentException ex)
        {
            throw new ParserException(t, "Unescaping string", ex);
        }
    }
    //endregion

    //region toString
    @Override
    public String toString()
    {
        return String.format("{Parser lexer=%s}", lexer);
    }
    //endregion

    //region ContextProvider
    @Override
    public ParsingContext getParsingContext()
    {
        return lexer.getParsingContext();
    }
    //endregion

    //region AutoCloseable
    @Override
    public void close() throws IOException
    {
        lexer.close();
    }
    //endregion
}
