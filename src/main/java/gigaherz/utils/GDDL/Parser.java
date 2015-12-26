package gigaherz.utils.GDDL;

import gigaherz.utils.GDDL.exceptions.LexerException;
import gigaherz.utils.GDDL.exceptions.ParserException;
import gigaherz.utils.GDDL.structure.Backreference;
import gigaherz.utils.GDDL.structure.Element;
import gigaherz.utils.GDDL.structure.Set;

import java.io.IOException;

@SuppressWarnings("unused")
public class Parser implements ContextProvider
{
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

    public Element parse(boolean resolveReferences) throws IOException, ParserException
    {
        Element ret = root();

        if (resolveReferences)
            ret.resolve(ret);

        return ret;
    }

    private Token popExpected(Tokens expectedToken) throws ParserException, IOException
    {
        if (lex.peek() != expectedToken)
            throw new ParserException(this, String.format("Unexpected token %s: Expected %s.", lex.peek(), expectedToken));
        return lex.pop();
    }

    private boolean hasAny(Tokens... tokens) throws LexerException, IOException
    {
        lex.nextPrefix();
        for (Tokens t : tokens)
        {
            if (lex.prefix() == t)
            {
                return true;
            }
        }
        return false;
    }

    private boolean hasPrefix(Tokens... tokens) throws LexerException, IOException
    {
        lex.beginPrefixScan();
        boolean r = hasAny(tokens);
        lex.endPrefixScan();
        return r;
    }

    private boolean prefix_element() throws LexerException, IOException
    {
        return prefix_basicElement() || prefix_namedElement();
    }

    private boolean prefix_basicElement() throws LexerException, IOException
    {
        return hasPrefix(Tokens.NIL, Tokens.NULL, Tokens.TRUE, Tokens.FALSE,
                Tokens.HEXINT, Tokens.INTEGER, Tokens.DOUBLE, Tokens.STRING)
                || prefix_backreference() || prefix_set() || prefix_typedSet();
    }

    private boolean prefix_namedElement() throws LexerException, IOException
    {
        lex.beginPrefixScan();
        boolean r = hasAny(Tokens.IDENT) && hasAny(Tokens.EQUALS);
        lex.endPrefixScan();
        return r;
    }

    private boolean prefix_backreference() throws LexerException, IOException
    {
        lex.beginPrefixScan();
        boolean r = hasAny(Tokens.COLON) && hasAny(Tokens.IDENT);
        lex.endPrefixScan();

        return r || prefix_identifier();
    }

    private boolean prefix_set() throws LexerException, IOException
    {
        return hasPrefix(Tokens.LBRACE);
    }

    private boolean prefix_typedSet() throws LexerException, IOException
    {
        lex.beginPrefixScan();
        boolean r = hasAny(Tokens.IDENT) && hasAny(Tokens.LBRACE);
        lex.endPrefixScan();
        return r;
    }

    private boolean prefix_identifier() throws LexerException, IOException
    {
        return hasPrefix(Tokens.IDENT);
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
        if (lex.peek() == Tokens.NIL)
        {
            popExpected(Tokens.NIL);
            return Element.Null();
        }
        if (lex.peek() == Tokens.NULL)
        {
            popExpected(Tokens.NULL);
            return Element.Null();
        }
        if (lex.peek() == Tokens.TRUE)
        {
            popExpected(Tokens.TRUE);
            return Element.booleanValue(true);
        }
        if (lex.peek() == Tokens.FALSE)
        {
            popExpected(Tokens.FALSE);
            return Element.booleanValue(false);
        }
        if (lex.peek() == Tokens.INTEGER) return Element.intValue(popExpected(Tokens.INTEGER).Text);
        if (lex.peek() == Tokens.HEXINT) return Element.intValue(popExpected(Tokens.HEXINT).Text, 16);
        if (lex.peek() == Tokens.INTEGER) return Element.intValue(popExpected(Tokens.INTEGER).Text);
        if (lex.peek() == Tokens.DOUBLE) return Element.floatValue(popExpected(Tokens.DOUBLE).Text);
        if (lex.peek() == Tokens.STRING) return Element.stringValue(lex, popExpected(Tokens.STRING).Text);
        if (prefix_set()) return set();
        if (prefix_typedSet()) return typedSet();
        if (prefix_backreference()) return backreference();

        throw new ParserException(this, "Internal Error");
    }

    private Element namedElement() throws IOException, ParserException
    {
        String I = identifier();

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

        while (hasPrefix(Tokens.COLON))
        {
            popExpected(Tokens.COLON);

            String O = identifier();

            B.append(O);
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

        S.setName(I);

        return S;
    }

    private String identifier() throws ParserException, IOException
    {
        if (lex.peek() == Tokens.IDENT) return popExpected(Tokens.IDENT).Text;

        throw new ParserException(this, "Internal error");
    }

    @Override
    public ParsingContext getParsingContext()
    {
        return lex.getParsingContext();
    }
}
