package gigaherz.util.gddl2;

import gigaherz.util.gddl2.exceptions.LexerException;
import gigaherz.util.gddl2.exceptions.ParserException;
import gigaherz.util.gddl2.structure.Reference;
import gigaherz.util.gddl2.structure.Element;
import gigaherz.util.gddl2.structure.Collection;
import gigaherz.util.gddl2.structure.Value;
import gigaherz.util.gddl2.util.BasicIntStack;
import gigaherz.util.gddl2.util.Utility;

import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;

@SuppressWarnings("unused")
public class Parser implements ContextProvider, AutoCloseable
{
    int prefixPos = -1;
    final BasicIntStack prefixStack = new BasicIntStack();
    private final TokenProvider lex;
    private boolean finished_with_rbrace = false;

    Parser(TokenProvider lexer)
    {
        lex = lexer;
    }

    public static Parser fromFile(String filename) throws IOException
    {
        return new Parser(new Lexer(new Reader(new FileReader(filename), filename)));
    }

    public static Parser fromString(String text)
    {
        return new Parser(new Lexer(new Reader(new StringReader(text), "UNKNOWN")));
    }

    public TokenProvider getLexer()
    {
        return lex;
    }

    public Element parse() throws IOException, ParserException
    {
        return parse(true);
    }

    public Element parse(boolean simplify) throws IOException, ParserException
    {
        Element ret = root();

        if (simplify)
        {
            ret.resolve(ret, ret);
            ret = ret.simplify();
        }

        return ret;
    }

    private Token popExpected(TokenType... expected) throws ParserException, IOException
    {
        TokenType current = lex.peek();
        for (TokenType expectedToken : expected)
        {
            if (current == expectedToken)
                return lex.pop();
        }

        if (expected.length != 1)
            throw new ParserException(this, String.format("Unexpected token %s. Expected one of: %s.", current, Utility.join(", ", expected)));

        throw new ParserException(this, String.format("Unexpected token %s. Expected: %s.", current, expected[0]));
    }

    public void beginPrefixScan()
    {
        prefixStack.push(prefixPos);
    }

    public TokenType nextPrefix() throws LexerException, IOException
    {
        return lex.peek(++prefixPos);
    }

    public void endPrefixScan()
    {
        prefixPos = prefixStack.pop();
    }

    private boolean hasAny(TokenType... tokens) throws LexerException, IOException
    {
        TokenType prefix = nextPrefix();
        for (TokenType t : tokens)
        {
            if (prefix == t)
            {
                return true;
            }
        }
        return false;
    }

    private boolean prefix_element() throws LexerException, IOException
    {
        return prefix_basicElement() || prefix_namedElement();
    }

    private boolean prefix_basicElement() throws LexerException, IOException
    {
        beginPrefixScan();
        boolean r = hasAny(TokenType.NIL, TokenType.NULL, TokenType.TRUE, TokenType.FALSE, TokenType.HEXINT, TokenType.INTEGER, TokenType.DOUBLE, TokenType.STRING);
        endPrefixScan();

        return r || prefix_backreference() || prefix_set() || prefix_typedSet();
    }

    private boolean prefix_namedElement() throws LexerException, IOException
    {
        beginPrefixScan();
        boolean r = hasAny(TokenType.IDENT, TokenType.STRING) && hasAny(TokenType.EQUALS);
        endPrefixScan();
        return r;
    }

    private boolean prefix_backreference() throws LexerException, IOException
    {
        beginPrefixScan();
        boolean r = hasAny(TokenType.COLON) && hasAny(TokenType.IDENT);
        endPrefixScan();

        return r || prefix_identifier();
    }

    private boolean prefix_set() throws LexerException, IOException
    {
        beginPrefixScan();
        boolean r = hasAny(TokenType.LBRACE);
        endPrefixScan();
        return r;
    }

    private boolean prefix_typedSet() throws LexerException, IOException
    {
        beginPrefixScan();
        boolean r = hasAny(TokenType.IDENT) && hasAny(TokenType.LBRACE);
        endPrefixScan();
        return r;
    }

    private boolean prefix_identifier() throws LexerException, IOException
    {
        beginPrefixScan();
        boolean r = hasAny(TokenType.IDENT);
        endPrefixScan();
        return r;
    }

    private Element root() throws IOException, ParserException
    {
        Element E = element();
        popExpected(TokenType.END);
        return E;
    }

    private Element element() throws ParserException, IOException
    {
        if (prefix_namedElement()) return namedElement();
        if (prefix_basicElement()) return basicElement();

        throw new ParserException(this, "Internal Error");
    }

    private Element basicElement() throws ParserException, IOException
    {
        if (lex.peek() == TokenType.NIL) return nullValue(popExpected(TokenType.NIL));
        if (lex.peek() == TokenType.NULL) return nullValue(popExpected(TokenType.NULL));
        if (lex.peek() == TokenType.TRUE) return booleanValue(popExpected(TokenType.TRUE));
        if (lex.peek() == TokenType.FALSE) return booleanValue(popExpected(TokenType.FALSE));
        if (lex.peek() == TokenType.INTEGER) return intValue(popExpected(TokenType.INTEGER));
        if (lex.peek() == TokenType.HEXINT) return intValue(popExpected(TokenType.HEXINT), 16);
        if (lex.peek() == TokenType.INTEGER) return intValue(popExpected(TokenType.INTEGER));
        if (lex.peek() == TokenType.DOUBLE) return floatValue(popExpected(TokenType.DOUBLE));
        if (lex.peek() == TokenType.STRING) return stringValue(popExpected(TokenType.STRING));
        if (prefix_set()) return set();
        if (prefix_typedSet()) return typedSet();
        if (prefix_backreference()) return backreference();

        throw new ParserException(this, "Internal Error");
    }

    private Element namedElement() throws IOException, ParserException
    {
        Token N = popExpected(TokenType.IDENT, TokenType.STRING);

        String I;
        if (N.name == TokenType.IDENT) I = N.text;
        else I = Lexer.unescapeString(N);

        popExpected(TokenType.EQUALS);

        if (!prefix_basicElement())
            throw new ParserException(this, String.format("Expected a basic element after EQUALS, found %s instead", lex.peek()));

        Element B = basicElement();

        B.setName(I);
        B.setComment(N.comment);

        return B;
    }

    private Reference backreference() throws IOException, ParserException
    {
        boolean rooted = false;

        if (lex.peek() == TokenType.COLON)
        {
            popExpected(TokenType.COLON);
            rooted = true;
        }
        if (!prefix_identifier())
            throw new ParserException(this, String.format("Expected identifier, found %s instead", lex.peek()));

        Token I = identifier();
        Reference B = Element.backreference(rooted, I.text);
        B.setComment(I.comment);

        while (lex.peek() == TokenType.COLON)
        {
            popExpected(TokenType.COLON);

            Token O = identifier();

            B.add(O.text);
        }

        return B;
    }

    private Collection set() throws ParserException, IOException
    {
        Token openBrace = popExpected(TokenType.LBRACE);

        Collection S = Collection.empty();
        S.setComment(openBrace.comment);

        while (lex.peek() != TokenType.RBRACE)
        {
            finished_with_rbrace = false;

            if (!prefix_element())
                throw new ParserException(this, String.format("Expected element after LBRACE, found %s instead", lex.peek()));

            S.add(element());

            if (lex.peek() != TokenType.RBRACE)
            {
                if (!finished_with_rbrace || (lex.peek() == TokenType.COMMA))
                {
                    popExpected(TokenType.COMMA);
                }
            }
        }

        popExpected(TokenType.RBRACE);

        finished_with_rbrace = true;

        return S;
    }

    private Collection typedSet() throws IOException, ParserException
    {
        Token I = identifier();

        if (!prefix_set())
            throw new ParserException(this, "Internal error");
        Collection S = set().withTypeName(I.text);

        S.setComment(I.comment);

        return S;
    }

    private Token identifier() throws ParserException, IOException
    {
        if (lex.peek() == TokenType.IDENT) return popExpected(TokenType.IDENT);

        throw new ParserException(this, "Internal error");
    }

    public static Value nullValue(Token token)
    {
        Value e = Element.nullValue();
        e.setComment(token.comment);
        return e;
    }

    public static Value booleanValue(Token token)
    {
        Value e = Element.booleanValue(token.name == TokenType.TRUE);
        e.setComment(token.comment);
        return e;
    }

    public static Value intValue(Token token)
    {
        Value e = Element.intValue(Long.parseLong(token.text));
        e.setComment(token.comment);
        return e;
    }

    public static Value intValue(Token token, int _base)
    {
        Value e = Element.intValue(Long.parseLong(token.text.substring(2), 16));
        e.setComment(token.comment);
        return e;
    }

    public static Value floatValue(Token token)
    {
        Value e = Element.floatValue(Double.parseDouble(token.text));
        e.setComment(token.comment);
        return e;
    }

    public static Value stringValue(Token token) throws ParserException
    {
        Value e = Element.stringValue(Lexer.unescapeString(token));
        e.setComment(token.comment);
        return e;
    }

    @Override
    public ParsingContext getParsingContext()
    {
        return lex.getParsingContext();
    }

    @Override
    public void close() throws IOException
    {
        lex.close();
    }
}
