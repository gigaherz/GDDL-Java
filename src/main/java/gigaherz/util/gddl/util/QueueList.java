package gigaherz.util.gddl.util;

import gigaherz.util.gddl.Utility;

import java.util.Iterator;

public class QueueList<T> implements Iterable<T>
{
    private static final int defaultCapacity = 16;

    private int capacityMask;
    private int start;
    private int count;
    private Object[] buffer;

    public QueueList()
    {
        this(defaultCapacity);
    }

    public QueueList(int capacity)
    {
        if (capacity < 0)
        {
            throw new IndexOutOfBoundsException("capacity is less than 0.");
        }

        growTo(capacity);
    }

    private void growTo(int value)
    {
        value = Utility.upperPower(value);
        if (null != buffer && value == buffer.length)
            return;

        Object[] newBuffer = new Object[value];
        for (int i = 0; i < size(); i++)
        {
            newBuffer[i] = get(i);
        }

        buffer = newBuffer;
        start = 0;
        capacityMask = value - 1;
    }

    private void ensureCapacityFor(int numElements)
    {
        if (size() + numElements > buffer.length)
        {
            growTo(size() + numElements);
        }
    }

    private int toBufferIndex(int index)
    {
        return (index + start) & capacityMask;
    }

    public int size()
    {
        return count;
    }

    public void add(T item)
    {
        ensureCapacityFor(1);
        buffer[toBufferIndex(size())] = item;
        count++;
    }

    public T remove()
    {
        if (size() == 0)
            throw new IllegalStateException("The Deque is empty");

        @SuppressWarnings("unchecked")
        T result = (T) buffer[start];

        buffer[start] = null;
        start = toBufferIndex(1);
        count--;
        return result;
    }

    public T get(int index)
    {
        if (index >= size())
            throw new IndexOutOfBoundsException();

        @SuppressWarnings("unchecked")
        T result = (T) buffer[toBufferIndex(index)];

        return result;
    }

    // VERY unsafe iterator!
    @Override
    public Iterator<T> iterator()
    {
        return new Iterator<T>()
        {
            int current = 0;

            @Override
            public boolean hasNext()
            {
                return current < size();
            }

            @Override
            public T next()
            {
                return get(current++);
            }
        };
    }
}
