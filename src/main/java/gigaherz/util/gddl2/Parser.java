package gigaherz.util.gddl2;

import gigaherz.util.gddl2.exceptions.LexerException;
import gigaherz.util.gddl2.exceptions.ParserException;
import gigaherz.util.gddl2.structure.Collection;
import gigaherz.util.gddl2.structure.Element;
import gigaherz.util.gddl2.structure.Reference;
import gigaherz.util.gddl2.structure.Value;
import gigaherz.util.gddl2.util.BasicIntStack;
import gigaherz.util.gddl2.util.Utility;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

@SuppressWarnings("unused")
public class Parser implements ContextProvider, AutoCloseable
{
    // Factory Methods

    /**
     * Constructs a Parser instance that reads from the given filename.
     * @param filename The filename to read from.
     * @return A parser ready to process the file.
     * @throws IOException When accessing the file.
     */
    public static Parser fromFile(String filename) throws IOException
    {
        return fromFile(filename, StandardCharsets.UTF_8);
    }

    /**
     * Constructs a Parser instance that reads from the given filename.
     * @param filename The filename to read from.
     * @param charset The charset.
     * @return A parser ready to process the file.
     * @throws IOException When accessing the file.
     */
    public static Parser fromFile(String filename, Charset charset) throws IOException
    {
        return fromReader(new FileReader(filename, charset), filename);
    }

    /**
     * Constructs a Parser instance that reads from the given file.
     * @param file The file to read from.
     * @return A parser ready to process the file.
     * @throws IOException When accessing the file.
     */
    public static Parser fromFile(File file) throws IOException
    {
        return fromFile(file, StandardCharsets.UTF_8);
    }

    /**
     * Constructs a Parser instance that reads from the given file.
     * @param file The file to read from.
     * @param charset The charset.
     * @return A parser ready to process the file.
     * @throws IOException When accessing the file.
     */
    public static Parser fromFile(File file, Charset charset) throws IOException
    {
        return fromReader(new FileReader(file, charset), file.getAbsolutePath());
    }

    /**
     * Constructs a Parser instance that reads from the given file.
     * @param path The file to read from.
     * @return A parser ready to process the file.
     * @throws IOException When accessing the file.
     */
    public static Parser fromFile(Path path) throws IOException
    {
        return fromFile(path, StandardCharsets.UTF_8);
    }

    /**
     * Constructs a Parser instance that reads from the given file.
     * @param path The file to read from.
     * @param charset The charset.
     * @return A parser ready to process the file.
     * @throws IOException When accessing the file.
     */
    public static Parser fromFile(Path path, Charset charset) throws IOException
    {
        return fromReader(Files.newBufferedReader(path, charset), path.toString());
    }

    /**
     * Constructs a Parser instance that reads from the given file.
     * @param stream The file to read from.
     * @return A parser ready to process the file.
     */
    public static Parser fromStream(InputStream stream)
    {
        return fromStream(stream, StandardCharsets.UTF_8);
    }

    /**
     * Constructs a Parser instance that reads from the given file.
     * @param stream The file to read from.
     * @return A parser ready to process the file.
     */
    public static Parser fromStream(InputStream stream, String sourceName)
    {
        return fromStream(stream, StandardCharsets.UTF_8, sourceName);
    }

    /**
     * Constructs a Parser instance that reads from the given file.
     * @param stream The file to read from.
     * @param charset The charset.
     * @return A parser ready to process the file.
     */
    public static Parser fromStream(InputStream stream, Charset charset)
    {
        return fromStream(stream, charset, "UNKNOWN");
    }

    /**
     * Constructs a Parser instance that reads from the given file.
     * @param stream The file to read from.
     * @param charset The charset.
     * @return A parser ready to process the file.
     */
    public static Parser fromStream(InputStream stream, Charset charset, String sourceName)
    {
        return fromReader(new InputStreamReader(stream, charset), sourceName);
    }

    /**
     * Constructs a Parser instance that reads from the given string.
     * @param text The text to parse.
     * @return A parser ready to process the file.
     */
    public static Parser fromString(String text)
    {
        return fromString(text, "UNKNOWN");
    }

    /**
     * Constructs a Parser instance that reads from the given string.
     * @param text The text to parse.
     * @param sourceName The filename to display in parse errors.
     * @return A parser ready to process the file.
     */
    public static Parser fromString(String text, String sourceName)
    {
        return fromReader(new StringReader(text), sourceName);
    }

    /**
     * Constructs a Parser instance that reads from the given reader.
     * @param reader The stream to read from.
     * @return A parser ready to process the file.
     */
    public static Parser fromReader(java.io.Reader reader)
    {
        return new Parser(new Lexer(new Reader(reader, "UNKNOWN")));
    }

    /**
     * Constructs a Parser instance that reads from the given file.
     * @param reader The stream to read from.
     * @param sourceName The filename to display in parse errors.
     * @return A parser ready to process the file.
     */
    public static Parser fromReader(java.io.Reader reader, String sourceName)
    {
        return new Parser(new Lexer(new Reader(reader, sourceName)));
    }

    // For unit test purposes
    static Parser fromProvider(TokenProvider lexer)
    {
        return new Parser(lexer);
    }

    // Implementation
    int prefixPos = -1;
    final BasicIntStack prefixStack = new BasicIntStack();
    @SuppressWarnings("FieldCanBeLocal")
    private boolean finishedWithRBrace = false;
    private final TokenProvider lex;

    private Parser(TokenProvider lexer)
    {
        lex = lexer;
    }

    /**
     * @return The lexer used by this parser
     */
    public TokenProvider getLexer()
    {
        return lex;
    }

    /**
     * Parses the whole file and returns the resulting root element.
     * Equivalent to {@link #parse(boolean)} with simplify=true
     * @return The root element
     * @throws IOException When accessing the source stream.
     * @throws ParserException When parsing
     */
    public Element parse() throws IOException, ParserException
    {
        return parse(true);
    }

    /**
     * Parses the whole file and returns the resulting root element.
     * @param simplify If true, the structure
     * @return The root element
     * @throws IOException When accessing the source stream.
     * @throws ParserException When parsing
     */
    public Element parse(boolean simplify) throws IOException, ParserException
    {
        Element ret = root();

        if (simplify)
        {
            ret.resolve(ret, null);
            ret = ret.simplify();
        }

        return ret;
    }

    private Token popExpected(TokenType... expected) throws ParserException, IOException
    {
        TokenType current = lex.peek();
        if (Arrays.stream(expected).anyMatch(t -> current == t))
            return lex.pop();

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
        return lex.peek(++prefixPos);
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

        return r || prefixReference() || prefixSet() || prefixTypedSet();
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

    private boolean prefixSet() throws LexerException, IOException
    {
        beginPrefixScan();
        boolean r = hasAny(TokenType.L_BRACE);
        endPrefixScan();
        return r;
    }

    private boolean prefixTypedSet() throws LexerException, IOException
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

    private Element root() throws IOException, ParserException
    {
        Element E = element();
        popExpected(TokenType.END);
        return E;
    }

    private Element element() throws ParserException, IOException
    {
        if (prefixNamedElement()) return namedElement();
        if (prefixBasicElement()) return basicElement();

        throw new ParserException(this, "Internal Error");
    }

    private Element basicElement() throws ParserException, IOException
    {
        if (lex.peek() == TokenType.NIL) return nullValue(popExpected(TokenType.NIL));
        if (lex.peek() == TokenType.NULL) return nullValue(popExpected(TokenType.NULL));
        if (lex.peek() == TokenType.TRUE) return booleanValue(popExpected(TokenType.TRUE));
        if (lex.peek() == TokenType.FALSE) return booleanValue(popExpected(TokenType.FALSE));
        if (lex.peek() == TokenType.INTEGER) return intValue(popExpected(TokenType.INTEGER));
        if (lex.peek() == TokenType.HEX_INT) return intValue(popExpected(TokenType.HEX_INT), 16);
        if (lex.peek() == TokenType.INTEGER) return intValue(popExpected(TokenType.INTEGER));
        if (lex.peek() == TokenType.DOUBLE) return floatValue(popExpected(TokenType.DOUBLE));
        if (lex.peek() == TokenType.STRING) return stringValue(popExpected(TokenType.STRING));
        if (prefixSet()) return set();
        if (prefixTypedSet()) return typedSet();
        if (prefixReference()) return reference();

        throw new ParserException(this, "Internal Error");
    }

    private Element namedElement() throws IOException, ParserException
    {
        Token name = popExpected(TokenType.IDENTIFIER, TokenType.STRING);

        String n = name.type == TokenType.IDENTIFIER ? name.text : unescapeString(name);

        popExpected(TokenType.EQUALS);

        if (!prefixBasicElement())
            throw new ParserException(this, String.format("Expected a basic element after EQUALS, found %s instead", lex.peek()));

        Element b = basicElement();

        b.setName(n);
        b.setComment(name.comment);

        return b;
    }

    private Reference reference() throws IOException, ParserException
    {
        boolean rooted = false;

        if (lex.peek() == TokenType.COLON)
        {
            popExpected(TokenType.COLON);
            rooted = true;
        }
        if (!prefixIdentifier())
            throw new ParserException(this, String.format("Expected identifier, found %s instead", lex.peek()));

        Token name = identifier();
        Reference b = rooted ? Reference.absolute(name.text) : Reference.relative(name.text);
        b.setComment(name.comment);

        while (lex.peek() == TokenType.COLON)
        {
            popExpected(TokenType.COLON);

            name = identifier();

            b.add(name.text);
        }

        return b;
    }

    private Collection set() throws ParserException, IOException
    {
        Token openBrace = popExpected(TokenType.L_BRACE);

        Collection s = Collection.empty();
        s.setComment(openBrace.comment);

        while (lex.peek() != TokenType.R_BRACE)
        {
            finishedWithRBrace = false;

            if (!prefixElement())
                throw new ParserException(this, String.format("Expected element after LBRACE, found %s instead", lex.peek()));

            s.add(element());

            if (lex.peek() != TokenType.R_BRACE)
            {
                if (!finishedWithRBrace || (lex.peek() == TokenType.COMMA))
                {
                    popExpected(TokenType.COMMA);
                }
            }
        }

        popExpected(TokenType.R_BRACE);

        finishedWithRBrace = true;

        return s;
    }

    private Collection typedSet() throws IOException, ParserException
    {
        Token type = identifier();

        if (!prefixSet())
            throw new ParserException(this, "Internal error");
        Collection s = set()
                .withTypeName(type.text);

        s.setComment(type.comment);

        return s;
    }

    private Token identifier() throws ParserException, IOException
    {
        if (lex.peek() == TokenType.IDENTIFIER) return popExpected(TokenType.IDENTIFIER);

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

    public static String unescapeString(Token t) throws ParserException
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
