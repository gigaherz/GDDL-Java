package dev.gigaherz.util.gddl2.internal;

import org.jetbrains.annotations.NotNull;

import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Minimal implementation of an array-backed queue.
 *
 * @param <T>
 */
public class ArrayQueue<T>
{
    private static final int defaultCapacity = 16;

    private int capacityMask;
    private int start;
    private int count;
    private T[] buffer;

    public ArrayQueue()
    {
        this(defaultCapacity);
    }

    public ArrayQueue(int capacity)
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

        @SuppressWarnings("unchecked")
        T[] newBuffer = (T[]) new Object[value];
        for (int i = 0; i < size(); i++)
        {
            newBuffer[i] = get(i);
        }

        buffer = newBuffer;
        start = 0;
        capacityMask = value - 1;
    }

    private void ensureCapacityFor(@SuppressWarnings("SameParameterValue") int numElements)
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

        T result = buffer[start];

        buffer[start] = null;
        start = toBufferIndex(1);
        count--;
        return result;
    }

    public T get(int index)
    {
        if (index >= size())
            throw new IndexOutOfBoundsException();

        return buffer[toBufferIndex(index)];
    }

    @NotNull
    public Stream<T> elements()
    {
        return IntStream.range(0, count).mapToObj(i -> get(i % count));
    }
}
