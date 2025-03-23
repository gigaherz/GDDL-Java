package dev.gigaherz.util.gddl2.parsing;

import dev.gigaherz.util.gddl2.exceptions.LexerException;
import dev.gigaherz.util.gddl2.exceptions.ParserException;
import dev.gigaherz.util.gddl2.internal.BasicIntStack;
import dev.gigaherz.util.gddl2.internal.Utility;
import dev.gigaherz.util.gddl2.queries.Index;
import dev.gigaherz.util.gddl2.queries.Query;
import dev.gigaherz.util.gddl2.queries.Range;
import dev.gigaherz.util.gddl2.structure.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@SuppressWarnings("unused")
public class Parser implements ContextProvider, AutoCloseable
{
    //region API
    public Parser(TokenProvider lexer)
    {
        this.lexer = lexer;
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
        return parse(false);
    }

    /**
     * Parses the whole file and returns the resulting root element.
     *
     * @param simplify If true, queries are resolved and values are simplified.
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

        var doc = GddlDocument.create(root);
        doc.setDanglingComment(result.getValue());

        return doc;
    }

    public Query parseQuery() throws IOException, ParserException
    {
        var result = queryPath();
        popExpected(TokenType.END);
        return result.path();
    }
    //endregion

    //region Implementation
    private int prefixPos = -1;
    private final BasicIntStack prefixStack = new BasicIntStack();
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

    private Token popExpectedWithParent(TokenType... expected) throws ParserException, IOException
    {
        Token current = lexer.peekFull();
        if (Arrays.stream(expected).anyMatch(current::is))
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
    //endregion

    //region Parse Rules
    private Map.Entry<GddlElement<?>, String> root() throws IOException, ParserException
    {
        GddlElement<?> E = element();
        var end = popExpected(TokenType.END);
        return Map.entry(E, end.comment);
    }

    private boolean prefixIdentifier() throws LexerException, IOException
    {
        beginPrefixScan();
        boolean r = hasAny(TokenType.IDENTIFIER);
        endPrefixScan();
        return r;
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

        throw new ParserException(this, String.format("Internal Error: Token %s did not correspond to any code path.", lexer.peek()));
    }

    private Token name() throws ParserException, IOException
    {
        return popExpected(TokenType.IDENTIFIER, TokenType.STRING_LITERAL);
    }

    private boolean prefixReference() throws LexerException, IOException
    {
        beginPrefixScan();
        boolean r = hasAny(TokenType.COLON, TokenType.SLASH) && hasAny(TokenType.IDENTIFIER, TokenType.STRING_LITERAL, TokenType.L_BRACKET);
        endPrefixScan();

        return r || prefixIdentifier();
    }

    private GddlReference reference() throws IOException, ParserException
    {
        var queryParse = queryPath();

        GddlReference b = GddlReference.of(queryParse.path);
        b.setComment(queryParse.token.comment);

        return b;
    }

    private QueryParse queryPath() throws IOException, ParserException
    {
        AtomicReference<Query> pathRef = new AtomicReference<>(new Query());

        Token firstToken = null;

        boolean rooted = false;

        TokenType firstDelimiter = null;
        if (lexer.peek() == TokenType.COLON || lexer.peek() == TokenType.SLASH)
        {
            firstToken = popExpected(TokenType.COLON, TokenType.SLASH);
            firstDelimiter = firstToken.type;
            pathRef.set(pathRef.get().absolute());
        }

        Token pathToken = pathComponent(pathRef);
        if (firstToken == null) firstToken = pathToken;

        while (lexer.peek() == TokenType.COLON || lexer.peek() == TokenType.SLASH || lexer.peek() == TokenType.L_BRACKET)
        {
            if (lexer.peek() != TokenType.L_BRACKET)
            {
                if (firstDelimiter != null && lexer.peek() != firstDelimiter)
                    throw new ParserException(this, String.format("Query must use consistent delimiters, expected %s, found %s instead", firstDelimiter, lexer.peek()));

                firstDelimiter = popExpected(TokenType.COLON, TokenType.SLASH).type;
            }

            pathComponent(pathRef);
        }

        return new QueryParse(firstToken, pathRef.get());
    }

    private Token pathComponent(AtomicReference<Query> pathRef) throws ParserException, IOException
    {
        var token = popExpected(TokenType.IDENTIFIER, TokenType.STRING_LITERAL, TokenType.DOT, TokenType.DOUBLE_DOT, TokenType.L_BRACKET);
        var path = pathRef.get();
        switch (token.type)
        {
            case IDENTIFIER:
                path = path.byKey(token.text);
                break;
            case STRING_LITERAL:
                path = path.byKey(unescapeString(token));
                break;
            case DOT:
                path = path.self();
                break;
            case DOUBLE_DOT:
                path = path.parent();
                break;
            case L_BRACKET:
            {
                boolean hasStart = false;
                var start = Index.fromStart(0);

                if (lexer.peek() == TokenType.CARET)
                {
                    popExpected(TokenType.CARET);
                    start = Index.fromEnd(intValue(popExpected(TokenType.INTEGER_LITERAL)).intValue());
                    hasStart = true;
                }
                else if (lexer.peek() == TokenType.INTEGER_LITERAL)
                {
                    start = Index.fromStart(intValue(popExpected(TokenType.INTEGER_LITERAL)).intValue());
                    hasStart = true;
                }

                if (hasStart && lexer.peek() == TokenType.R_BRACKET)
                {
                    popExpected(TokenType.R_BRACKET);
                    path = path.byRange(new Range(start, start.fromEnd() ? Index.fromEnd(start.value() - 1) : Index.fromStart(start.value() + 1)));
                    break;
                }

                var inclusive = popExpected(TokenType.DOUBLE_DOT, TokenType.TRIPLE_DOT);

                var end = Index.fromEnd(0);

                if (lexer.peek() == TokenType.CARET)
                {
                    popExpected(TokenType.CARET);
                    end = Index.fromEnd(intValue(popExpected(TokenType.INTEGER_LITERAL)).intValue());
                }
                else if (lexer.peek() == TokenType.INTEGER_LITERAL)
                {
                    end = Index.fromStart(intValue(popExpected(TokenType.INTEGER_LITERAL)).intValue());
                    if (inclusive.type == TokenType.TRIPLE_DOT)
                        end = end.fromEnd() ? Index.fromEnd(end.value() - 1) : Index.fromStart(end.value() + 1);
                }

                popExpected(TokenType.R_BRACKET);

                path = path.byRange(new Range(start, end));
                break;
            }
            default:
                throw new ParserException(lexer, String.format("Internal Error: Unexpected token %s found when parsing Reference path component", token));
        }
        pathRef.set(path);
        return token;
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

            var name = popExpectedWithParent(TokenType.IDENTIFIER, TokenType.STRING_LITERAL);

            String n = name.type == TokenType.STRING_LITERAL ? unescapeString(name) : name.text;

            popExpected(TokenType.EQUAL_SIGN, TokenType.COLON);

            GddlElement<?> b = element();
            b.setComment(name.comment);
            b.setWhitespace(name.whitespace);
            s.put(n, b);

            if (lexer.peek() != TokenType.R_BRACE)
            {
                if (!finishedWithRBrace || lexer.peek() == TokenType.COMMA)
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

    private static GddlValue nullValue(Token token)
    {
        var e = GddlValue.nullValue();
        e.setComment(token.comment);
        return e;
    }

    private static GddlValue booleanValue(Token token)
    {
        var e = GddlValue.of(token.type == TokenType.TRUE);
        e.setComment(token.comment);
        return e;
    }

    private static GddlValue intValue(Token token)
    {
        var e = GddlValue.of(Long.parseLong(token.text));
        e.setComment(token.comment);
        return e;
    }

    private static GddlValue hexIntValue(Token token)
    {
        long num = Long.parseLong(token.text.substring(2), 16);
        var e = GddlValue.of(num);
        e.setComment(token.comment);
        return e;
    }

    private static GddlValue floatValue(Token token)
    {
        double value = switch (token.text)
        {
            case ".NaN" -> Double.NaN;
            case ".Inf", "+.Inf" -> Double.POSITIVE_INFINITY;
            case "-.Inf" -> Double.NEGATIVE_INFINITY;
            default -> Double.parseDouble(token.text);
        };
        var e = GddlValue.of(value);
        e.setComment(token.comment);
        return e;
    }

    private static GddlValue stringValue(Token token) throws ParserException
    {
        var e = GddlValue.of(unescapeString(token));
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
    public void close() throws Exception
    {
        lexer.close();
    }

    private record QueryParse(Token token, Query path)
    {
    }
    //endregion
}
