package gigaherz.util.gddl2;

import com.google.common.collect.ImmutableList;
import gigaherz.util.gddl2.exceptions.LexerException;
import gigaherz.util.gddl2.exceptions.ParserException;
import gigaherz.util.gddl2.structure.Collection;
import gigaherz.util.gddl2.structure.Element;
import gigaherz.util.gddl2.structure.Reference;
import gigaherz.util.gddl2.structure.Value;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

public class ParserTest
{
    @Test
    public void mockLexerWorks() throws LexerException, IOException
    {
        TokenProvider provider = lexerBuilder()
                .addInt()
                .addFloat()
                .addHexInt()
                .addString()
                .addBooleanTrue()
                .addBooleanFalse()
                .addLBrace()
                .addRBrace()
                .addEquals()
                .addColon()
                .build();

        assertEquals(new Token(TokenType.INTEGER, "1", new ParsingContext("TEST", 1, 1), ""), provider.pop());
        assertEquals(new Token(TokenType.DOUBLE, "1.0", new ParsingContext("TEST", 1, 1), ""), provider.pop());
        assertEquals(new Token(TokenType.HEXINT, "0x1", new ParsingContext("TEST", 1, 1), ""), provider.pop());
        assertEquals(new Token(TokenType.STRING, "\"1\"", new ParsingContext("TEST", 1, 1), ""), provider.pop());
        assertEquals(new Token(TokenType.TRUE, "true", new ParsingContext("TEST", 1, 1), ""), provider.pop());
        assertEquals(new Token(TokenType.FALSE, "false", new ParsingContext("TEST", 1, 1), ""), provider.pop());
        assertEquals(new Token(TokenType.LBRACE, "{", new ParsingContext("TEST", 1, 1), ""), provider.pop());
        assertEquals(new Token(TokenType.RBRACE, "}", new ParsingContext("TEST", 1, 1), ""), provider.pop());
        assertEquals(new Token(TokenType.EQUALS, "=", new ParsingContext("TEST", 1, 1), ""), provider.pop());
        assertEquals(new Token(TokenType.COLON, ":", new ParsingContext("TEST", 1, 1), ""), provider.pop());

        // Test that it automatically returns an END token at the end.
        assertEquals(new Token(TokenType.END, "", new ParsingContext("TEST", 1, 1), ""), provider.pop());

        // Test that you can continue popping END tokens even after the end.
        assertEquals(new Token(TokenType.END, "", new ParsingContext("TEST", 1, 1), ""), provider.pop());
        assertEquals(new Token(TokenType.END, "", new ParsingContext("TEST", 1, 1), ""), provider.pop());
    }

    @Test
    public void parsesNullAsValue() throws IOException, ParserException
    {
        TokenProvider provider = lexerBuilder().addInt().build();
        Parser parser = new Parser(provider);
        assertEquals(Value.of(1), parser.parse(false));
    }

    @Test
    public void parsesIntegerAsValue() throws IOException, ParserException
    {
        TokenProvider provider = lexerBuilder().addInt().build();
        Parser parser = new Parser(provider);
        assertEquals(Value.of(1), parser.parse(false));
    }

    @Test
    public void parsesHexIntegerAsValue() throws IOException, ParserException
    {
        TokenProvider provider = lexerBuilder().addHexInt().build();
        Parser parser = new Parser(provider);
        assertEquals(Value.of(1), parser.parse(false));
    }

    @Test
    public void parsesDoubleAsValue() throws IOException, ParserException
    {
        TokenProvider provider = lexerBuilder().addFloat().build();
        Parser parser = new Parser(provider);
        assertEquals(Value.of(1.0), parser.parse(false));
    }

    @Test
    public void parsesStringAsValue() throws IOException, ParserException
    {
        TokenProvider provider = lexerBuilder().addString().build();
        Parser parser = new Parser(provider);
        assertEquals(Value.of("1"), parser.parse(false));
    }

    @Test
    public void parsesBooleanTrueAsValue() throws IOException, ParserException
    {
        TokenProvider provider = lexerBuilder().addBooleanTrue().build();
        Parser parser = new Parser(provider);
        assertEquals(Value.of(true), parser.parse(false));
    }

    @Test
    public void parsesBooleanFalseAsValue() throws IOException, ParserException
    {
        TokenProvider provider = lexerBuilder().addBooleanFalse().build();
        Parser parser = new Parser(provider);
        assertEquals(Value.of(false), parser.parse(false));
    }

    @Test
    public void parsesBracesAsCollection() throws IOException, ParserException
    {
        TokenProvider provider = lexerBuilder().addLBrace().addRBrace().build();
        Parser parser = new Parser(provider);
        assertEquals(Collection.empty(), parser.parse(false));
    }

    @Test
    public void parsesTypedCollection() throws IOException, ParserException
    {
        TokenProvider provider = lexerBuilder().addIdentifier("test").addLBrace().addRBrace().build();
        Parser parser = new Parser(provider);
        assertEquals(Collection.empty().withTypeName("test"), parser.parse(false));
    }

    @Test
    public void parsesValueInsideCollection() throws IOException, ParserException
    {
        TokenProvider provider = lexerBuilder().addLBrace().addInt().addRBrace().build();
        Parser parser = new Parser(provider);
        assertEquals(Collection.of(Value.of(1)), parser.parse(false));
    }

    @Test
    public void parsesMultipleValuesInsideCollection() throws IOException, ParserException
    {
        TokenProvider provider = lexerBuilder().addLBrace().addInt().addComma().addInt().addRBrace().build();
        Parser parser = new Parser(provider);
        assertEquals(Collection.of(Value.of(1), Value.of(1)), parser.parse(false));
    }

    @Test
    public void parsesNestedCollection() throws IOException, ParserException
    {
        TokenProvider provider = lexerBuilder().addLBrace().addLBrace().addRBrace().addRBrace().build();
        Parser parser = new Parser(provider);
        Collection expected = Collection.of(Collection.empty());
        Element actual = parser.parse(false);
        assertEquals(expected, actual);
    }

    @Test
    public void commaIsOptionalAfterNestedCollection() throws IOException, ParserException
    {
        TokenProvider provider = lexerBuilder().addLBrace().addLBrace().addRBrace().addInt().addRBrace().build();
        Parser parser = new Parser(provider);
        Collection expected = Collection.of(Collection.empty(), Value.of(1));
        Element actual = parser.parse(false);
        assertEquals(expected, actual);
    }

    @Test
    public void acceptsTrailingCommaInCollection() throws IOException, ParserException
    {
        TokenProvider provider = lexerBuilder().addLBrace().addInt().addComma().addRBrace().build();
        Parser parser = new Parser(provider);
        assertEquals(Collection.of(Value.of(1)), parser.parse(false));
    }

    @Test
    public void parsesNamedValueInsideCollection() throws IOException, ParserException
    {
        TokenProvider provider = lexerBuilder().addLBrace().addString("\"a\"").addEquals().addInt().addRBrace().build();
        Parser parser = new Parser(provider);
        Collection expected = Collection.of(Value.of(1).withName("a"));
        Element actual = parser.parse(false);
        assertEquals(expected, actual);
    }

    @Test
    public void parsesNamedTypedNestedCollection() throws IOException, ParserException
    {
        TokenProvider provider = lexerBuilder().addLBrace().addString("\"n\"").addEquals().addIdentifier("a").addLBrace().addRBrace().addRBrace().build();
        Parser parser = new Parser(provider);
        Collection expected = Collection.of(Collection.empty().withTypeName("a").withName("n"));
        Element actual = parser.parse(false);
        assertEquals(expected, actual);
    }

    @Test
    public void parsesBackreference() throws IOException, ParserException
    {
        TokenProvider provider = lexerBuilder().addLBrace().addIdentifier("a").addColon().addIdentifier("b").addRBrace().build();
        Parser parser = new Parser(provider);
        Collection expected = Collection.of(Reference.relative("a", "b"));
        Element actual = parser.parse(false);
        assertEquals(expected, actual);
    }

    @Test
    public void parsesRootedBackreference() throws IOException, ParserException
    {
        TokenProvider provider = lexerBuilder().addLBrace().addColon().addIdentifier("a").addColon().addIdentifier("b").addRBrace().build();
        Parser parser = new Parser(provider);
        Collection expected = Collection.of(Reference.absolute("a", "b"));
        Element actual = parser.parse(false);
        assertEquals(expected, actual);
    }

    // HARNESS BELOW
    // -------------

    public static MockLexerBuilder lexerBuilder()
    {
        return new MockLexerBuilder();
    }

    public static class MockLexerBuilder
    {
        private final ImmutableList.Builder<Token> builder = ImmutableList.builder();

        public MockLexerBuilder addEnd()
        {
            return add(TokenType.END, "");
        }

        public MockLexerBuilder addInt()
        {
            return add(TokenType.INTEGER, "1");
        }

        public MockLexerBuilder addFloat()
        {
            return add(TokenType.DOUBLE, "1.0");
        }

        public MockLexerBuilder addHexInt()
        {
            return add(TokenType.HEXINT, "0x1");
        }

        public MockLexerBuilder addString()
        {
            return add(TokenType.STRING, "\"1\"");
        }

        public MockLexerBuilder addString(String text)
        {
            return add(TokenType.STRING, text);
        }

        public MockLexerBuilder addBooleanTrue()
        {
            return add(TokenType.TRUE, "true");
        }

        public MockLexerBuilder addBooleanFalse()
        {
            return add(TokenType.FALSE, "false");
        }

        public MockLexerBuilder addLBrace()
        {
            return add(TokenType.LBRACE, "{");
        }

        public MockLexerBuilder addRBrace()
        {
            return add(TokenType.RBRACE, "}");
        }

        public MockLexerBuilder addColon()
        {
            return add(TokenType.COLON, ":");
        }

        public MockLexerBuilder addComma()
        {
            return add(TokenType.COMMA, ",");
        }

        public MockLexerBuilder addEquals()
        {
            return add(TokenType.EQUALS, "=");
        }

        public MockLexerBuilder addIdentifier()
        {
            return add(TokenType.IDENT, "test");
        }

        public MockLexerBuilder addIdentifier(String text)
        {
            return add(TokenType.IDENT, text);
        }

        public MockLexerBuilder add(TokenType name, String text)
        {
            return add(name, text, new ParsingContext("TEST", 1, 1));
        }

        public MockLexerBuilder add(TokenType name, String text, ParsingContext context)
        {
            return add(name, text, context, "");
        }

        public MockLexerBuilder add(TokenType name, String text, ParsingContext context, String comment)
        {
            builder.add(new Token(name, text, context, comment));
            return this;
        }

        public MockLexer build()
        {
            addEnd();
            return new MockLexer(builder.build());
        }
    }

    public static class MockLexer implements TokenProvider
    {
        public final List<Token> preparedTokens;
        public int index = 0;
        public boolean closed = false;

        public MockLexer(List<Token> preparedTokens)
        {
            this.preparedTokens = preparedTokens;
        }

        private Token get(int index) throws IOException
        {
            if (closed)
                throw new IllegalStateException("The TokenProvider is closed.");
            int idx = this.index + index;
            if (idx >= preparedTokens.size())
                idx = preparedTokens.size()-1;
            return preparedTokens.get(idx);
        }

        @Override
        public TokenType peek() throws IOException
        {
            return peek(0);
        }

        @Override
        public TokenType peek(int index) throws IOException
        {
            return get(index).type;
        }

        @Override
        public Token pop() throws IOException
        {
            Token t = get(0);
            index++;
            return t;
        }

        @Override
        public ParsingContext getParsingContext()
        {
            if (closed)
                throw new IllegalStateException("The TokenProvider is closed.");
            return preparedTokens.get(Math.min(index, preparedTokens.size()-1)).context;
        }

        @Override
        public void close()
        {
            if (closed)
                throw new IllegalStateException("The TokenProvider has already been closed before.");
            closed = true;
        }
    }
}
