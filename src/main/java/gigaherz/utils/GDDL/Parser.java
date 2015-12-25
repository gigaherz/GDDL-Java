package gigaherz.utils.GDDL;

import gigaherz.utils.GDDL.exceptions.LexerException;
import gigaherz.utils.GDDL.exceptions.ParserException;
import gigaherz.utils.GDDL.structure.Backreference;
import gigaherz.utils.GDDL.structure.Element;
import gigaherz.utils.GDDL.structure.Set;

import java.io.IOException;

public class Parser implements FileContext
{
    public static Parser fromFile(String filename) throws IOException, ParserException
    {
        return new Parser(new Lexer(new Reader(filename)));
    }

    final Lexer lex;

    Parser(Lexer lexer)
    {
        lex = lexer;
    }

    boolean finished_with_rbrace = false;

    public Lexer getLexer() { return lex; }

    public Element Parse() throws IOException, ParserException
    {
        return Parse(true);
    }

    public Element Parse(boolean resolveReferences) throws IOException, ParserException
    {
        Element ret = root();

        if (resolveReferences)
            ret.resolve(ret);

        return ret;
    }

    private Token pop_expected(Tokens expectedToken) throws ParserException, IOException
    {
        if (lex.Peek() != expectedToken)
            throw new ParserException(this, String.format("Unexpected token %s: Expected %s.", lex.Peek(), expectedToken));
        return lex.Pop();
    }

    private boolean has_any(Tokens... tokens) throws LexerException, IOException
    {
        lex.NextPrefix();
        for(Tokens t : tokens)
        {
            if (lex.prefix() == t)
            {
                return true;
            }
        }
        return false;
    }

    private boolean has_prefix(Tokens... tokens) throws LexerException, IOException
    {
        lex.BeginPrefixScan();

        boolean r = has_any(tokens);

        lex.EndPrefixScan();
        return r;
    }

    Element root() throws IOException, ParserException
    /*#if DEBUG_RULES
    {
        Debug.WriteLine("Entering rule_root()");
        var ret = rule_root();
        Debug.WriteLine(String.format("Finished rule_root(), returned: {0}", ret));
        return ret;
    }
    Element rule_root()
    #endif*/
    {
        Element E = element();

        pop_expected(Tokens.END);

        return E;
    }

    boolean prefix_element() throws LexerException, IOException
    { return prefix_basicElement() || prefix_namedElement(); }
    Element element() throws ParserException, IOException
    /*#if DEBUG_RULES
    {
        Debug.WriteLine("Entering rule_element()");
        var ret = rule_element();
        Debug.WriteLine(String.format("Finished rule_element(), returned: {0}", ret));
        return ret;
    }
    Element rule_element()
    #endif*/
    {
        if (prefix_namedElement()) return namedElement();
        if (prefix_basicElement()) return basicElement();

        throw new ParserException(this, "Internal Error");
    }

    boolean prefix_basicElement() throws LexerException, IOException
    {
        return has_prefix(Tokens.NIL, Tokens.NULL, Tokens.TRUE, Tokens.FALSE,
                Tokens.HEXINT, Tokens.INTEGER, Tokens.DOUBLE, Tokens.STRING)
                || prefix_backreference() || prefix_set() || prefix_typedSet();
    }
    Element basicElement() throws ParserException, IOException
    /*#if DEBUG_RULES
    {
        Debug.WriteLine("Entering rule_basicElement()");
        var ret = rule_basicElement();
        Debug.WriteLine(String.format("Finished rule_basicElement(), returned: {0}", ret));
        return ret;
    }
    Element rule_basicElement()
    #endif*/
    {
        if (lex.Peek() == Tokens.NIL) { pop_expected(Tokens.NIL); return Element.Null(); }
        if (lex.Peek() == Tokens.NULL) { pop_expected(Tokens.NULL); return Element.Null(); }
        if (lex.Peek() == Tokens.TRUE) { pop_expected(Tokens.TRUE); return Element.booleanValue(true); }
        if (lex.Peek() == Tokens.FALSE) { pop_expected(Tokens.FALSE); return Element.booleanValue(false); }
        if (lex.Peek() == Tokens.INTEGER) return Element.intValue(pop_expected(Tokens.INTEGER).Text);
        if (lex.Peek() == Tokens.HEXINT) return Element.intValue(pop_expected(Tokens.HEXINT).Text, 16);
        if (lex.Peek() == Tokens.INTEGER) return Element.intValue(pop_expected(Tokens.INTEGER).Text);
        if (lex.Peek() == Tokens.DOUBLE) return Element.floatValue(pop_expected(Tokens.DOUBLE).Text);
        if (lex.Peek() == Tokens.STRING) return Element.stringValue(lex, pop_expected(Tokens.STRING).Text);
        if (prefix_set()) return set();
        if (prefix_typedSet()) return typedSet();
        if (prefix_backreference()) return backreference();

        throw new ParserException(this, "Internal Error");
    }

    boolean prefix_namedElement() throws LexerException, IOException
    {
        lex.BeginPrefixScan();
        boolean r = has_any(Tokens.IDENT) && has_any(Tokens.EQUALS);
        lex.EndPrefixScan();
        return r;
    }
    Element namedElement() throws IOException, ParserException
    /*#if DEBUG_RULES
    {
        Debug.WriteLine("Entering rule_namedElement()");
        var ret = rule_namedElement();
        Debug.WriteLine(String.format("Finished rule_namedElement(), returned: {0}", ret));
        return ret;
    }
    NamedElement rule_namedElement()
    #endif*/
    {
        String I = identifier();

        pop_expected(Tokens.EQUALS);

        if (!prefix_basicElement())
            throw new ParserException(this, String.format("Expected a basic element after EQUALS, found %s instead", lex.Peek()));

        Element B = basicElement();

        B.setName(I);

        return B;
    }

    boolean prefix_backreference() throws LexerException, IOException
    {
        lex.BeginPrefixScan();
        boolean r = has_any(Tokens.COLON) && has_any(Tokens.IDENT);
        lex.EndPrefixScan();

        return r || prefix_identifier();
    }
    Backreference backreference() throws IOException, ParserException
    /*#if DEBUG_RULES
    {
        Debug.WriteLine("Entering rule_backreference()");
        var ret = rule_backreference();
        Debug.WriteLine(String.format("Finished rule_backreference(), returned: {0}", ret));
        return ret;
    }
    Backreference rule_backreference()
    #endif*/
    {
        boolean rooted = false;

        if (lex.Peek() == Tokens.COLON)
        {
            pop_expected(Tokens.COLON);
            rooted = true;
        }
        if (!prefix_identifier())
            throw new ParserException(this, String.format("Expected identifier, found %s instead", lex.Peek()));

        String I = identifier();
        Backreference B = Element.backreference(rooted, I);

        while (has_prefix(Tokens.COLON))
        {
            pop_expected(Tokens.COLON);

            String O = identifier();

            B.append(O);
        }

        return B;
    }

    boolean prefix_set() throws LexerException, IOException
    {
        return has_prefix(Tokens.LBRACE);
    }
    Set set() throws ParserException, IOException
    /*#if DEBUG_RULES
    {
        Debug.WriteLine("Entering rule_set()");
        var ret = rule_set();
        Debug.WriteLine(String.format("Finished rule_set(), returned: {0}", ret));
        return ret;
    }
    Set rule_set()
    #endif*/
    {
        pop_expected(Tokens.LBRACE);

        Set S = Element.set();

        while (lex.Peek() != Tokens.RBRACE)
        {
            finished_with_rbrace = false;

            if (!prefix_element())
                throw new ParserException(this, String.format("Expected element after LBRACE, found %s instead", lex.Peek()));

            S.add(element());

            if (lex.Peek() != Tokens.RBRACE)
            {
                if (!finished_with_rbrace || (lex.Peek() == Tokens.COMMA))
                {
                    pop_expected(Tokens.COMMA);
                }
            }
        }

        pop_expected(Tokens.RBRACE);

        finished_with_rbrace = true;

        return S;
    }

    boolean prefix_typedSet() throws LexerException, IOException
    {
        lex.BeginPrefixScan();
        boolean r = has_any(Tokens.IDENT) && has_any(Tokens.LBRACE);
        lex.EndPrefixScan();
        return r;
    }
    Set typedSet() throws IOException, ParserException
    /*#if DEBUG_RULES
    {
        Debug.WriteLine("Entering rule_typedSet()");
        var ret = rule_typedSet();
        Debug.WriteLine(String.format("Finished rule_typedSet(), returned: {0}", ret));
        return ret;
    }
    TypedSet rule_typedSet()
    #endif*/
    {
        String I = identifier();

        if (!prefix_set())
            throw new ParserException(this, "Internal error");
        Set S = set();

        S.setName(I);

        return S;
    }

    boolean prefix_identifier() throws LexerException, IOException
    {
        return has_prefix(Tokens.IDENT);
    }
    String identifier() throws ParserException, IOException
    /*#if DEBUG_RULES
    {
        Debug.WriteLine("Entering rule_identifier()");
        var ret = rule_identifier();
        Debug.WriteLine(String.format("Finished rule_identifier(), returned: {0}", ret));
        return ret;
    }
    String rule_identifier()
    #endif*/
    {
        if (lex.Peek() == Tokens.IDENT) return pop_expected(Tokens.IDENT).Text;

        throw new ParserException(this, "Internal error");
    }

    @Override
    public ParseContext getFileContext()
    {
        return lex.getFileContext();
    }
}
