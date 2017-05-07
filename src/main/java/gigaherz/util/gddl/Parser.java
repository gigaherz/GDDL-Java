package gigaherz.util.gddl;

import gigaherz.util.gddl.exceptions.LexerException;
import gigaherz.util.gddl.exceptions.ParserException;
import gigaherz.util.gddl.structure.Backreference;
import gigaherz.util.gddl.structure.Element;
import gigaherz.util.gddl.structure.Set;
import gigaherz.util.gddl.structure.Value;
import gigaherz.util.gddl.util.BasicIntStack;

import java.io.IOException;

@SuppressWarnings("unused")
public class Parser implements ContextProvider
{
    int prefixPos = -1;
    final BasicIntStack prefixStack = new BasicIntStack();
    private final Lexer lex;
    private boolean finished_with_rbrace = false;

    Parser(Lexer lexer)
    {
        lex = lexer;
    }

    public static Parser fromFile(String filename) throws IOException, ParserException
    {
        return new Parser(new Lexer(new Reader(filename)));
    }

    public Lexer getLexer()
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

    private Token popExpected(Tokens... expected) throws ParserException, IOException
    {
        Tokens current = lex.peek();
        for (Tokens expectedToken : expected)
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

    public Tokens nextPrefix() throws LexerException, IOException
    {
        return lex.peek(++prefixPos);
    }

    public void endPrefixScan()
    {
        prefixPos = prefixStack.pop();
    }

    private boolean hasAny(Tokens... tokens) throws LexerException, IOException
    {
        Tokens prefix = nextPrefix();
        for (Tokens t : tokens)
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
        boolean r = hasAny(Tokens.NIL, Tokens.NULL, Tokens.TRUE, Tokens.FALSE, Tokens.HEXINT, Tokens.INTEGER, Tokens.DOUBLE, Tokens.STRING);
        endPrefixScan();

        return r || prefix_backreference() || prefix_set() || prefix_typedSet();
    }

    private boolean prefix_namedElement() throws LexerException, IOException
    {
        beginPrefixScan();
        boolean r = hasAny(Tokens.IDENT, Tokens.STRING) && hasAny(Tokens.EQUALS);
        endPrefixScan();
        return r;
    }

    private boolean prefix_backreference() throws LexerException, IOException
    {
        beginPrefixScan();
        boolean r = hasAny(Tokens.COLON) && hasAny(Tokens.IDENT);
        endPrefixScan();

        return r || prefix_identifier();
    }

    private boolean prefix_set() throws LexerException, IOException
    {
        beginPrefixScan();
        boolean r = hasAny(Tokens.LBRACE);
        endPrefixScan();
        return r;
    }

    private boolean prefix_typedSet() throws LexerException, IOException
    {
        beginPrefixScan();
        boolean r = hasAny(Tokens.IDENT) && hasAny(Tokens.LBRACE);
        endPrefixScan();
        return r;
    }

    private boolean prefix_identifier() throws LexerException, IOException
    {
        beginPrefixScan();
        boolean r = hasAny(Tokens.IDENT);
        endPrefixScan();
        return r;
    }

    private Element root() throws IOException, ParserException
    {
        Element E = element();
        popExpected(Tokens.END);
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
        if (lex.peek() == Tokens.NIL) return nullValue(popExpected(Tokens.NIL));
        if (lex.peek() == Tokens.NULL) return nullValue(popExpected(Tokens.NULL));
        if (lex.peek() == Tokens.TRUE) return booleanValue(popExpected(Tokens.TRUE));
        if (lex.peek() == Tokens.FALSE) return booleanValue(popExpected(Tokens.FALSE));
        if (lex.peek() == Tokens.INTEGER) return intValue(popExpected(Tokens.INTEGER));
        if (lex.peek() == Tokens.HEXINT) return intValue(popExpected(Tokens.HEXINT), 16);
        if (lex.peek() == Tokens.INTEGER) return intValue(popExpected(Tokens.INTEGER));
        if (lex.peek() == Tokens.DOUBLE) return floatValue(popExpected(Tokens.DOUBLE));
        if (lex.peek() == Tokens.STRING) return stringValue(popExpected(Tokens.STRING));
        if (prefix_set()) return set();
        if (prefix_typedSet()) return typedSet();
        if (prefix_backreference()) return backreference();

        throw new ParserException(this, "Internal Error");
    }

    private Element namedElement() throws IOException, ParserException
    {
        Token N = popExpected(Tokens.IDENT, Tokens.STRING);

        String I;
        if (N.Name == Tokens.IDENT) I = N.Text;
        else I = Lexer.unescapeString(N);

        popExpected(Tokens.EQUALS);

        if (!prefix_basicElement())
            throw new ParserException(this, String.format("Expected a basic element after EQUALS, found %s instead", lex.peek()));

        Element B = basicElement();

        B.setName(I);

        return B;
    }

    private Backreference backreference() throws IOException, ParserException
    {
        boolean rooted = false;

        if (lex.peek() == Tokens.COLON)
        {
            popExpected(Tokens.COLON);
            rooted = true;
        }
        if (!prefix_identifier())
            throw new ParserException(this, String.format("Expected identifier, found %s instead", lex.peek()));

        String I = identifier();
        Backreference B = Element.backreference(rooted, I);

        while (lex.peek() == Tokens.COLON)
        {
            popExpected(Tokens.COLON);

            String O = identifier();

            B.add(O);
        }

        return B;
    }

    private Set set() throws ParserException, IOException
    {
        popExpected(Tokens.LBRACE);

        Set S = Element.set();

        while (lex.peek() != Tokens.RBRACE)
        {
            finished_with_rbrace = false;

            if (!prefix_element())
                throw new ParserException(this, String.format("Expected element after LBRACE, found %s instead", lex.peek()));

            S.add(element());

            if (lex.peek() != Tokens.RBRACE)
            {
                if (!finished_with_rbrace || (lex.peek() == Tokens.COMMA))
                {
                    popExpected(Tokens.COMMA);
                }
            }
        }

        popExpected(Tokens.RBRACE);

        finished_with_rbrace = true;

        return S;
    }

    private Set typedSet() throws IOException, ParserException
    {
        String I = identifier();

        if (!prefix_set())
            throw new ParserException(this, "Internal error");
        Set S = set();

        S.setTypeName(I);

        return S;
    }

    private String identifier() throws ParserException, IOException
    {
        if (lex.peek() == Tokens.IDENT) return popExpected(Tokens.IDENT).Text;

        throw new ParserException(this, "Internal error");
    }

    public static Value nullValue(Token token)
    {
        return Element.nullValue();
    }

    public static Value booleanValue(Token token)
    {
        return Element.booleanValue(token.Name == Tokens.TRUE);
    }

    public static Value intValue(Token token)
    {
        return Element.intValue(Long.parseLong(token.Text));
    }

    public static Value intValue(Token token, int _base)
    {
        return Element.intValue(Long.parseLong(token.Text.substring(2), 16));
    }

    public static Value floatValue(Token token)
    {
        return Element.floatValue(Double.parseDouble(token.Text));
    }

    public static Value stringValue(Token token) throws ParserException
    {
        return Element.stringValue(Lexer.unescapeString(token));
    }

    @Override
    public ParsingContext getParsingContext()
    {
        return lex.getParsingContext();
    }
}
