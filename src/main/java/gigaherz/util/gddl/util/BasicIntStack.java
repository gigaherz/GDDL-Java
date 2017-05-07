package gigaherz.util.gddl.util;

import java.util.Arrays;

public class BasicIntStack
{
    int[] buffer;
    int count;

    public BasicIntStack()
    {
        buffer = new int[16];
        count = 0;
    }

    public void push(int i)
    {
        if (count == buffer.length)
        {
            buffer = Arrays.copyOf(buffer, buffer.length * 2);
        }

        buffer[count++] = i;
    }

    public int pop()
    {
        return buffer[--count];
    }
}

