package gigaherz.util.gddl2;

import gigaherz.util.gddl2.exceptions.ReaderException;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.Assert.*;

public class ReaderTest
{
    public static final String SOURCE_NAME = "TEST";

    @Test
    public void parsingContextEqualsWorks()
    {
        ParsingContext base = new ParsingContext("A", 2, 3);
        ParsingContext same = new ParsingContext("A", 2, 3);
        ParsingContext differentFile = new ParsingContext("B", 2, 3);
        ParsingContext differentLine = new ParsingContext("A", 1, 3);
        ParsingContext differentColumn = new ParsingContext("A", 2, 5);
        assertEquals(base, same);
        assertNotEquals(base, differentFile);
        assertNotEquals(base, differentLine);
        assertNotEquals(base, differentColumn);
    }

    @Test
    public void readsOneCharacter() throws IOException, ReaderException
    {
        String testString = "a";
        Reader reader = new Reader(new StringReader(testString), SOURCE_NAME);
        assertEquals(testString, reader.read(testString.length()));
    }

    @Test
    public void readsMultipleCharacters() throws IOException, ReaderException
    {
        String testString = "qwerty";
        Reader reader = new Reader(new StringReader(testString), SOURCE_NAME);
        assertEquals(testString, reader.read(testString.length()));
    }

    @Test
    public void readsOnlyAsMuchAsRequested() throws IOException, ReaderException
    {
        Reader reader = new Reader(new StringReader("qwerty"), SOURCE_NAME);
        assertEquals("qwe", reader.read(3));
    }

    @Test
    public void peekWorks() throws IOException, ReaderException
    {
        Reader reader = new Reader(new StringReader("abc"), SOURCE_NAME);

        assertEquals('a', reader.peek());
        assertEquals('a', reader.peek(0));
        assertEquals('b', reader.peek(1));
        assertEquals('c', reader.peek(2));

        assertEquals("a", reader.read(1));

        assertEquals('b', reader.peek());
        assertEquals('b', reader.peek(0));
        assertEquals('c', reader.peek(1));

        assertEquals("b", reader.read(1));
    }

    @Test
    public void peeksAfterRead() throws IOException, ReaderException
    {
        Reader reader = new Reader(new StringReader("zxcvbnm"), SOURCE_NAME);
        assertEquals("zxc", reader.read(3));
        assertEquals('v', reader.peek());
    }

    @Test
    public void peeksEndOfFile() throws IOException, ReaderException
    {
        Reader reader = new Reader(new StringReader(""), SOURCE_NAME);
        assertEquals(-1, reader.peek());
    }

    @Test
    public void keepsTrackOfLocation() throws IOException, ReaderException
    {
        Reader reader = new Reader(new StringReader("qwerty\nuiop\rasdf\r\n1234"), SOURCE_NAME);
        assertEquals(new ParsingContext(SOURCE_NAME, 1, 1), reader.getParsingContext());
        assertEquals("qw", reader.read(2));
        assertEquals(new ParsingContext(SOURCE_NAME, 1, 3), reader.getParsingContext());
        assertEquals("erty\nuio", reader.read(8));
        assertEquals(new ParsingContext(SOURCE_NAME, 2, 4), reader.getParsingContext());
        assertEquals("p\rasdf\r\n", reader.read(8));
        assertEquals(new ParsingContext(SOURCE_NAME, 4, 1), reader.getParsingContext());
        assertEquals("1234", reader.read(4));
        assertEquals(-1, reader.peek());
    }
}
