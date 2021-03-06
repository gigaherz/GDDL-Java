package gigaherz.util.gddl2;

import gigaherz.util.gddl2.exceptions.ReaderException;
import gigaherz.util.gddl2.util.QueueList;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Reader implements ContextProvider, AutoCloseable
{
    final QueueList<Integer> unreadBuffer = new QueueList<>();

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

    public int peek() throws ReaderException, IOException
    {
        return peek(0);
    }

    public int peek(int index) throws ReaderException, IOException
    {
        require(index + 1);

        return unreadBuffer.get(index);
    }

    public int next() throws ReaderException
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

    public void skip(int count) throws ReaderException, IOException
    {
        require(count);
        while (count-- > 0)
        { next(); }
    }

    public String toString()
    {
        StringBuilder b = new StringBuilder();
        for (int ch : unreadBuffer)
        {
            b.append((char) ch);
        }
        return String.format("{Reader ahead=%s}", b.toString());
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
