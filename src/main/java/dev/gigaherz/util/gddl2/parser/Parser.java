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
    public Document parse() throws IOException, ParserException
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
    public Document parse(boolean simplify) throws IOException, ParserException
    {
        var result = root();
        var root = result.getKey();

        if (simplify)
        {
            root.resolve(root, null);
            root = root.simplify();
        }

        var doc = new Document(root);
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

    private boolean prefixElement() throws LexerException, IOException
    {
        return prefixBasicElement() || prefixNamedElement();
    }

    private boolean prefixBasicElement() throws LexerException, IOException
    {
        beginPrefixScan();
        boolean r = hasAny(TokenType.NIL, TokenType.NULL, TokenType.TRUE, TokenType.FALSE,
                TokenType.HEX_INT, TokenType.INTEGER, TokenType.DOUBLE, TokenType.STRING);
        endPrefixScan();

        return r || prefixReference() || prefixCollection() || prefixTypedCollection();
    }

    private boolean prefixNamedElement() throws LexerException, IOException
    {
        beginPrefixScan();
        boolean r = hasAny(TokenType.IDENTIFIER, TokenType.STRING) && hasAny(TokenType.EQUALS);
        endPrefixScan();
        return r;
    }

    private boolean prefixReference() throws LexerException, IOException
    {
        beginPrefixScan();
        boolean r = hasAny(TokenType.COLON) && hasAny(TokenType.IDENTIFIER);
        endPrefixScan();

        return r || prefixIdentifier();
    }

    private boolean prefixCollection() throws LexerException, IOException
    {
        beginPrefixScan();
        boolean r = hasAny(TokenType.L_BRACE);
        endPrefixScan();
        return r;
    }

    private boolean prefixTypedCollection() throws LexerException, IOException
    {
        beginPrefixScan();
        boolean r = hasAny(TokenType.IDENTIFIER) && hasAny(TokenType.L_BRACE);
        endPrefixScan();
        return r;
    }

    private boolean prefixIdentifier() throws LexerException, IOException
    {
        beginPrefixScan();
        boolean r = hasAny(TokenType.IDENTIFIER);
        endPrefixScan();
        return r;
    }

    private Map.Entry<Element<?>, String> root() throws IOException, ParserException
    {
        Element<?> E = element();
        var end = popExpected(TokenType.END);
        return Map.entry(E, end.comment);
    }

    private Element<?> element() throws ParserException, IOException
    {
        if (prefixNamedElement()) return namedElement();
        if (prefixBasicElement()) return basicElement();

        throw new ParserException(this, "Internal Error");
    }

    private Element<?> basicElement() throws ParserException, IOException
    {
        if (lexer.peek() == TokenType.NIL) return nullValue(popExpected(TokenType.NIL));
        if (lexer.peek() == TokenType.NULL) return nullValue(popExpected(TokenType.NULL));
        if (lexer.peek() == TokenType.TRUE) return booleanValue(popExpected(TokenType.TRUE));
        if (lexer.peek() == TokenType.FALSE) return booleanValue(popExpected(TokenType.FALSE));
        if (lexer.peek() == TokenType.INTEGER) return intValue(popExpected(TokenType.INTEGER));
        if (lexer.peek() == TokenType.HEX_INT) return intValue(popExpected(TokenType.HEX_INT), 16);
        if (lexer.peek() == TokenType.INTEGER) return intValue(popExpected(TokenType.INTEGER));
        if (lexer.peek() == TokenType.DOUBLE) return floatValue(popExpected(TokenType.DOUBLE));
        if (lexer.peek() == TokenType.STRING) return stringValue(popExpected(TokenType.STRING));
        if (prefixCollection()) return collection();
        if (prefixTypedCollection()) return typedCollection();
        if (prefixReference()) return reference();

        throw new ParserException(this, "Internal Error");
    }

    private Element<?> namedElement() throws IOException, ParserException
    {
        Token name = popExpected(TokenType.IDENTIFIER, TokenType.STRING);

        String n = name.type == TokenType.IDENTIFIER ? name.text : unescapeString(name);

        popExpected(TokenType.EQUALS);

        if (!prefixBasicElement())
            throw new ParserException(this, String.format("Expected a basic element after EQUALS, found %s instead", lexer.peek()));

        Element<?> b = basicElement();

        b.setName(n);
        b.setComment(name.comment);
        b.setWhitespace(name.comment);

        return b;
    }

    private Reference reference() throws IOException, ParserException
    {
        boolean rooted = false;

        if (lexer.peek() == TokenType.COLON)
        {
            popExpected(TokenType.COLON);
            rooted = true;
        }
        if (!prefixIdentifier())
            throw new ParserException(this, String.format("Expected identifier, found %s instead", lexer.peek()));

        Token name = identifier();
        Reference b = rooted ? Reference.absolute(name.text) : Reference.relative(name.text);
        b.setComment(name.comment);

        while (lexer.peek() == TokenType.COLON)
        {
            popExpected(TokenType.COLON);

            name = identifier();

            b.add(name.text);
        }

        return b;
    }

    private Collection collection() throws ParserException, IOException
    {
        Token openBrace = popExpected(TokenType.L_BRACE);

        Collection s = Collection.empty();
        s.setComment(openBrace.comment);

        while (lexer.peek() != TokenType.R_BRACE)
        {
            finishedWithRBrace = false;

            if (!prefixElement())
                throw new ParserException(this, String.format("Expected element after LBRACE, found %s instead", lexer.peek()));

            s.add(element());

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

    private Collection typedCollection() throws IOException, ParserException
    {
        Token type = identifier();

        if (!prefixCollection())
            throw new ParserException(this, "Internal error");
        Collection s = collection()
                .withTypeName(type.text);

        s.setComment(type.comment);

        return s;
    }

    private Token identifier() throws ParserException, IOException
    {
        if (lexer.peek() == TokenType.IDENTIFIER) return popExpected(TokenType.IDENTIFIER);

        throw new ParserException(this, "Internal error");
    }

    private static Value nullValue(Token token)
    {
        Value e = Value.nullValue();
        e.setComment(token.comment);
        return e;
    }

    private static Value booleanValue(Token token)
    {
        Value e = Value.of(token.type == TokenType.TRUE);
        e.setComment(token.comment);
        return e;
    }

    private static Value intValue(Token token)
    {
        Value e = Value.of(Long.parseLong(token.text));
        e.setComment(token.comment);
        return e;
    }

    @SuppressWarnings("SameParameterValue")
    private static Value intValue(Token token, int _base)
    {
        Value e = Value.of(Long.parseLong(token.text.substring(2), _base));
        e.setComment(token.comment);
        return e;
    }

    private static Value floatValue(Token token)
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
        Value e = Value.of(value);
        e.setComment(token.comment);
        return e;
    }

    private static Value stringValue(Token token) throws ParserException
    {
        Value e = Value.of(unescapeString(token));
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
