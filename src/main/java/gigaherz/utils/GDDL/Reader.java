package gigaherz.utils.GDDL;

import gigaherz.utils.GDDL.deque.IndexedDeque;
import gigaherz.utils.GDDL.exceptions.ReaderException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;

public class Reader implements FileContext
{
    boolean endQueued = false;
    final IndexedDeque<Integer> unreadBuffer = new IndexedDeque<>();

    FileReader dataSource;
    String sourceName;
    int line = 1;
    int column = 1;

    int lastEol;

    public Reader(String source) throws FileNotFoundException
    {
        sourceName = source;
        dataSource = new FileReader(source);
    }

    void Require(int number) throws ReaderException, IOException
    {
        int needed = number - unreadBuffer.size();
        if (needed > 0)
        {
            NeedChars(needed);
        }
    }

    private void NeedChars(int needed) throws ReaderException, IOException
    {
        while (needed-- > 0)
        {
            if (endQueued)
            {
                throw new ReaderException(this, "Tried to read beyond the end of the file.");
            }

            int ch = dataSource.read();
            unreadBuffer.addLast(ch);
            if (ch < 0)
                endQueued = true;
        }
    }

    public int Peek() throws ReaderException, IOException
    {
        return Peek(0);
    }

    public int Peek(int index) throws ReaderException, IOException
    {
        Require(index + 1);

        return unreadBuffer.get(index);
    }

    public int Pop() throws ReaderException
    {
        int ch = unreadBuffer.removeFirst();

        column++;
        if (ch == '\n')
        {
            if (lastEol != '\r')
            {
                column = 1;
                line++;
            }
            lastEol = ch;
        }
        else if (ch == '\r')
        {
            lastEol = ch;
        }
        else if (lastEol > 0)
        {
            lastEol = 0;
            column = 1;
            line++;
        }

        return ch;
    }

    public String Read(int count) throws ReaderException, IOException
    {
        Require(count);
        StringBuilder b = new StringBuilder();
        while (count-- > 0)
        {
            int ch = Pop();
            if (ch < 0)
                throw new ReaderException(this, "Tried to read beyond the end of the file.");
            b.append((char)ch);
        }
        return b.toString();
    }

    public void Drop(int count) throws ReaderException, IOException
    {
        Require(count);
        while (count-- > 0)
            Pop();
    }

    public String toString()
    {
        StringBuilder b = new StringBuilder();
        for (int ch : unreadBuffer)
        {
            b.append((char)ch);
        }
        return String.format("{Reader ahead=%s}", b.toString());
    }

    public ParseContext getFileContext()
    {
        return new ParseContext(sourceName, line, column);
    }
}
