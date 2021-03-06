package gigaherz.util.gddl2;

import gigaherz.util.gddl2.exceptions.LexerException;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.Assert.*;

public class LexerTest
{
    @Test
    public void lexesIntegers() throws LexerException, IOException
    {
        assertEquals(tokenInt("1"), lexSingle("1"));
        assertEquals(tokenInt("-1"), lexSingle("-1"));
        assertEquals(tokenHexInt("0x1"), lexSingle("0x1"));
        assertEquals(tokenHexInt("-0x1"), lexSingle("-0x1"));
    }

    @Test
    public void lexesFloats() throws LexerException, IOException
    {
        assertEquals(tokenFloat("1.0"), lexSingle("1.0"));
        assertEquals(tokenFloat("1."), lexSingle("1."));
        assertEquals(tokenFloat(".1"), lexSingle(".1"));
        assertEquals(tokenFloat("1.0e1"), lexSingle("1.0e1"));
        assertEquals(tokenFloat("1.0e+1"), lexSingle("1.0e+1"));
        assertEquals(tokenFloat("1.0e-1"), lexSingle("1.0e-1"));
        assertEquals(tokenFloat("1.e1"), lexSingle("1.e1"));
        assertEquals(tokenFloat(".1e1"), lexSingle(".1e1"));

        assertEquals(tokenFloat("-1.0"), lexSingle("-1.0"));
        assertEquals(tokenFloat("-1."), lexSingle("-1."));
        assertEquals(tokenFloat("-.1"), lexSingle("-.1"));
        assertEquals(tokenFloat("-1.0e1"), lexSingle("-1.0e1"));
        assertEquals(tokenFloat("-1.0e+1"), lexSingle("-1.0e+1"));
        assertEquals(tokenFloat("-1.0e-1"), lexSingle("-1.0e-1"));
        assertEquals(tokenFloat("-1.e1"), lexSingle("-1.e1"));
        assertEquals(tokenFloat("-.1e1"), lexSingle("-.1e1"));

        assertEquals(tokenFloat("+1.0"), lexSingle("+1.0"));
        assertEquals(tokenFloat("+1."), lexSingle("+1."));
        assertEquals(tokenFloat("+.1"), lexSingle("+.1"));
        assertEquals(tokenFloat("+1.0e1"), lexSingle("+1.0e1"));
        assertEquals(tokenFloat("+1.0e+1"), lexSingle("+1.0e+1"));
        assertEquals(tokenFloat("+1.0e-1"), lexSingle("+1.0e-1"));
        assertEquals(tokenFloat("+1.e1"), lexSingle("+1.e1"));
        assertEquals(tokenFloat("+.1e1"), lexSingle("+.1e1"));

        assertEquals(tokenFloat(".Inf"), lexSingle(".Inf"));
        assertEquals(tokenFloat("-.Inf"), lexSingle("-.Inf"));
        assertEquals(tokenFloat("+.Inf"), lexSingle("+.Inf"));
        assertEquals(tokenFloat(".NaN"), lexSingle(".NaN"));
    }

    @Test
    public void lexesStrings() throws LexerException, IOException
    {
        // Ascii text
        assertEquals(tokenString("\"a\""), lexSingle("\"a\""));
        assertEquals(tokenString("\"b\\\"\""), lexSingle("\"b\\\"\""));
        assertEquals(tokenString("\"b'\""), lexSingle("\"b'\""));
        assertEquals(tokenString("'a'"), lexSingle("'a'"));
        assertEquals(tokenString("'b\\''"), lexSingle("'b\\''"));
        assertEquals(tokenString("'b\"'"), lexSingle("'b\"'"));

        // Escapes
        assertEquals(tokenString("'\\x00'"), lexSingle("'\\x00'"));
        assertEquals(tokenString("'\\x0F'"), lexSingle("'\\x0F'"));
        assertEquals(tokenString("'\\xF0'"), lexSingle("'\\xF0'"));
        assertEquals(tokenString("'\\xFF'"), lexSingle("'\\xFF'"));
        assertEquals(tokenString("'\\u0000'"), lexSingle("'\\u0000'"));
        assertEquals(tokenString("'\\u000F'"), lexSingle("'\\u000F'"));
        assertEquals(tokenString("'\\uF000'"), lexSingle("'\\uF000'"));
        assertEquals(tokenString("'\\uF00F'"), lexSingle("'\\uF00F'"));

        // Unicode
        assertEquals(tokenString("'\uD800\uDF3C\uD800\uDF30\uD800\uDF32 \uD800\uDF32\uD800\uDF3B\uD800\uDF34\uD800\uDF43 \uD800\uDF39̈\uD800\uDF44\uD800\uDF30\uD800\uDF3D, \uD800\uDF3D\uD800\uDF39 \uD800\uDF3C\uD800\uDF39\uD800\uDF43 \uD800\uDF45\uD800\uDF3F \uD800\uDF3D\uD800\uDF33\uD800\uDF30\uD800\uDF3D \uD800\uDF31\uD800\uDF42\uD800\uDF39\uD800\uDF32\uD800\uDF32\uD800\uDF39\uD800\uDF38.'"),
                lexSingle("'\uD800\uDF3C\uD800\uDF30\uD800\uDF32 \uD800\uDF32\uD800\uDF3B\uD800\uDF34\uD800\uDF43 \uD800\uDF39̈\uD800\uDF44\uD800\uDF30\uD800\uDF3D, \uD800\uDF3D\uD800\uDF39 \uD800\uDF3C\uD800\uDF39\uD800\uDF43 \uD800\uDF45\uD800\uDF3F \uD800\uDF3D\uD800\uDF33\uD800\uDF30\uD800\uDF3D \uD800\uDF31\uD800\uDF42\uD800\uDF39\uD800\uDF32\uD800\uDF32\uD800\uDF39\uD800\uDF38.'"));
    }

    @Test
    public void lexesKeywords() throws LexerException, IOException
    {
        assertEquals(tokenBooleanTrue(), lexSingle("true"));
        assertEquals(tokenBooleanFalse(), lexSingle("false"));
        assertEquals(tokenNull(), lexSingle("null"));
        assertEquals(tokenNil(), lexSingle("nil"));
    }

    @Test
    public void lexesSymbols() throws LexerException, IOException
    {
        assertEquals(tokenLBrace(), lexSingle("{"));
        assertEquals(tokenRBrace(), lexSingle("}"));
        assertEquals(tokenComma(), lexSingle(","));
        assertEquals(tokenColon(), lexSingle(":"));
        assertEquals(tokenEquals(), lexSingle("="));
    }

    @Test
    public void keepsComments() throws LexerException, IOException
    {
        Token expected = token(TokenType.LBRACE, "{", new ParsingContext("TEST", 1, 1), "this is a comment\n");
        Token actual = lexSingle("#this is a comment\n{");
        assertEquals(expected, actual);
    }

    @Test
    public void keepsMultipleCommentLines() throws LexerException, IOException
    {
        Token expected = token(TokenType.LBRACE, "{", new ParsingContext("TEST", 1, 1), "this\nis\na\ncomment\n");
        Token actual = lexSingle("#this\n#is\n#a\n#comment\n{");
        assertEquals(expected, actual);
    }

    @Test
    public void ignoresWhitespace() throws LexerException, IOException
    {
        assertEquals(tokenLBrace(), lexSingle(" \t\n{ \t\n"));
    }

    // HARNESS BELOW
    // -------------

    public static Token lexSingle(String text) throws LexerException, IOException
    {
        Lexer lexer = new Lexer(makeReader(text));
        Token token = lexer.pop();
        assertEquals(String.format("Should find END after reading token %s", token), TokenType.END, lexer.peek());
        return token;
    }


    public static Reader makeReader(String text)
    {
        return new Reader(new StringReader(text), "TEST");
    }

    public static Token tokenEnd()
    {
        return token(TokenType.END, "");
    }

    public static Token tokenInt(String number)
    {
        return token(TokenType.INTEGER, number);
    }

    public static Token tokenFloat(String number)
    {
        return token(TokenType.DOUBLE, number);
    }

    public static Token tokenHexInt(String number)
    {
        return token(TokenType.HEXINT, number);
    }

    public static Token tokenString(String text)
    {
        return token(TokenType.STRING, text);
    }

    public static Token tokenBooleanTrue()
    {
        return token(TokenType.TRUE, "true");
    }

    public static Token tokenBooleanFalse()
    {
        return token(TokenType.FALSE, "false");
    }

    public static Token tokenNil()
    {
        return token(TokenType.NIL, "nil");
    }

    public static Token tokenNull()
    {
        return token(TokenType.NULL, "null");
    }

    public static Token tokenLBrace()
    {
        return token(TokenType.LBRACE, "{");
    }

    public static Token tokenRBrace()
    {
        return token(TokenType.RBRACE, "}");
    }

    public static Token tokenColon()
    {
        return token(TokenType.COLON, ":");
    }

    public static Token tokenComma()
    {
        return token(TokenType.COMMA, ",");
    }

    public static Token tokenEquals()
    {
        return token(TokenType.EQUALS, "=");
    }

    public static Token tokenIdentifier(String text)
    {
        return token(TokenType.IDENT, text);
    }

    public static Token token(TokenType name, String text)
    {
        return token(name, text, new ParsingContext("TEST", 1, 1));
    }

    public static Token token(TokenType name, String text, ParsingContext context)
    {
        return token(name, text, context, "");
    }

    public static Token token(TokenType name, String text, ParsingContext context, String comment)
    {
        return new Token(name, text, context, comment);
    }

}
