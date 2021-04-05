package gigaherz.util.gddl2;

import gigaherz.util.gddl2.exceptions.ReaderException;
import gigaherz.util.gddl2.util.ArrayQueue;
import gigaherz.util.gddl2.util.Utility;

import java.io.IOException;

public class Reader implements ContextProvider, AutoCloseable
{
    final ArrayQueue<Integer> unreadBuffer = new ArrayQueue<>();

    private final java.io.Reader dataSource;
    private final String sourceName;

    private boolean endQueued = false;
    private int line = 1;
    private int column = 1;
    private int lastEol;

    public Reader(java.io.Reader reader, String sourceName)
    {
        this.sourceName = sourceName;
        dataSource = reader;
    }

    private void require(int number) throws ReaderException, IOException
    {
        int needed = number - unreadBuffer.size();
        if (needed > 0)
        {
            needChars(needed);
        }
    }

    private void needChars(int needed) throws ReaderException, IOException
    {
        while (needed-- > 0)
        {
            if (endQueued)
            {
                throw new ReaderException(this, "Tried to read beyond the end of the file.");
            }

            int ch = dataSource.read();
            unreadBuffer.add(ch);
            if (ch < 0)
                endQueued = true;
        }
    }

    /**
     * Returns the first character in the lookahead buffer, reading characters from the input reader as needed.
     * @return The character, or -1 if end of file
     * @throws ReaderException When trying to read beyond the end of the input
     * @throws IOException When reading the input
     */
    public int peek() throws ReaderException, IOException
    {
        return peek(0);
    }

    /**
     * Returns the Nth character in the lookahead buffer, reading characters from the input reader as needed.
     * @param index The position in the lookahead buffer, starting at 0.
     * @return The character, or -1 if end of file
     * @throws ReaderException When trying to read beyond the end of the input
     * @throws IOException When reading the input
     */
    public int peek(int index) throws ReaderException, IOException
    {
        require(index + 1);

        return unreadBuffer.get(index);
    }

    /**
     * Removes the first character in the lookahead buffer, and returns it.
     * @return The character, or -1 if end of file
     */
    public int next()
    {
        int ch = unreadBuffer.remove();

        column++;
        if (ch == '\n')
        {
            column = 1;
            if (lastEol != '\r')
                line++;
            lastEol = ch;
        }
        else if (ch == '\r')
        {
            column = 1;
            line++;
            lastEol = ch;
        }
        else if (lastEol > 0)
        {
            lastEol = 0;
        }

        return ch;
    }

    /**
     * Removes N characters from the lookahead buffer, and returns them as a string.
     * @param count The number of characters to return
     * @return A string with the character sequence
     * @throws ReaderException If there are not enough characters reading between the buffer and the input
     * @throws IOException When accessing the input
     */
    public String read(int count) throws ReaderException, IOException
    {
        require(count);
        StringBuilder b = new StringBuilder();
        while (count-- > 0)
        {
            int ch = next();
            if (ch < 0)
                throw new ReaderException(this, "Tried to read beyond the end of the file.");
            b.append((char) ch);
        }
        return b.toString();
    }

    /**
     * Removes N characters from the lookahead buffer, advancing the input stream as necessary.
     * @param count The number of characters to drop
     * @throws ReaderException If there are not enough characters reading between the buffer and the input
     * @throws IOException When accessing the input
     */
    public void skip(int count) throws ReaderException, IOException
    {
        require(count);
        while (count-- > 0)
        { next(); }
    }

    public String toString()
    {
        return String.format("{Reader ahead=%s}", Utility.join("", unreadBuffer.elements()));
    }

    public ParsingContext getParsingContext()
    {
        return new ParsingContext(sourceName, line, column);
    }

    @Override
    public void close() throws IOException
    {
        dataSource.close();
    }
}
