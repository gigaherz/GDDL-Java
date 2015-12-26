package gigaherz.utils.GDDL.deque;

import gigaherz.utils.GDDL.Utility;
import sun.plugin.dom.exception.InvalidStateException;

import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicLong;

@SuppressWarnings("unchecked,unused")
public class IndexedDeque<T> implements Collection<T>
{
    private static final int defaultCapacity = 16;

    private final AtomicLong changeNumber = new AtomicLong(0);

    private int capacityClosestPowerOfTwoMinusOne;
    private int start;
    private int count;
    private Object[] buffer;

    public IndexedDeque()
    {
        this(defaultCapacity);
    }

    public IndexedDeque(int capacity)
    {
        if (capacity < 0)
        {
            throw new IndexOutOfBoundsException("capacity is less than 0.");
        }

        this.setCapacity(capacity);
    }

    public IndexedDeque(Collection<T> collection)
    {
        this(Utility.count(collection));
        insertAll(0, collection);
    }

    public int capacity()
    {
        return buffer.length;
    }

    private void setCapacity(int value)
    {
        if (value < 0)
        {
            throw new IndexOutOfBoundsException(
                    "Capacity is less than 0.");
        }
        else if (value < this.size())
        {
            throw new IndexOutOfBoundsException(
                    "Capacity cannot be set to a value less than Count");
        }
        else if (null != buffer && value == buffer.length)
        {
            return;
        }

        // Create a new array and copy the old values.
        int powOfTwo = Utility.closestPowerOfTwoGreaterThan(value);

        value = powOfTwo;

        Object[] newBuffer = new Object[value];
        buffer = this.toArray(newBuffer);

        // Set up to use the new buffer.
        buffer = newBuffer;
        start = 0;
        this.capacityClosestPowerOfTwoMinusOne = powOfTwo - 1;

        changeNumber.getAndIncrement();
    }

    @Override
    public <A> A[] toArray(A[] newBuffer)
    {
        for (int i = 0; i < Math.min(newBuffer.length, size()); i++)
        {
            newBuffer[i] = (A) get(i);
        }
        return newBuffer;
    }

    public boolean isFull()
    {
        return this.size() == this.capacity();
    }

    @Override
    public boolean isEmpty()
    {
        return 0 == this.size();
    }

    private void ensureCapacityFor(int numElements)
    {
        if (this.size() + numElements > this.capacity())
        {
            this.setCapacity(this.size() + numElements);
        }
    }

    private int toBufferIndex(int index)
    {
        int bufferIndex;

        bufferIndex = (index + this.start)
                & this.capacityClosestPowerOfTwoMinusOne;

        return bufferIndex;
    }

    private void checkIndexOutOfRange(int index)
    {
        if (index >= this.size())
        {
            throw new IndexOutOfBoundsException(
                    "The supplied index is greater than the Count");
        }
    }

    private int shiftStartOffset(int value)
    {
        this.start = toBufferIndex(value);

        return this.start;
    }

    private int preShiftStartOffset(int value)
    {
        int offset = this.start;
        this.shiftStartOffset(value);
        return offset;
    }

    private int postShiftStartOffset(int value)
    {
        return shiftStartOffset(value);
    }

    @Override
    public int size()
    {
        return count;
    }

    private void incrementCount(int value)
    {
        this.count += value;
    }

    private void decrementCount(int value)
    {
        this.count = Math.max(this.size() - value, 0);
    }

    @Override
    public boolean add(T item)
    {
        addLast(item);
        return true;
    }

    @Override
    public boolean containsAll(Collection<?> c)
    {
        for (Object e : c)
        {
            if (contains(e))
                return false;
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends T> c)
    {
        boolean any = false;
        for (Object e : c)
            any = any || add((T) e);
        return any;
    }

    @Override
    public boolean removeAll(Collection<?> c)
    {
        boolean any = false;
        for (Object e : c)
            any = any || remove(e);
        return any;
    }

    @Override
    public boolean retainAll(Collection<?> c)
    {
        throw new InvalidStateException("Operation not implemented");
    }

    private void wipeBuffer(int logicalIndex, int length)
    {
        int offset = toBufferIndex(logicalIndex);
        if (offset + length > this.capacity())
        {
            int len = this.capacity() - offset;
            Arrays.fill(this.buffer, offset, offset + len, null);

            len = toBufferIndex(logicalIndex + length);
            Arrays.fill(this.buffer, 0, len, null);
        }
        else
        {
            Arrays.fill(this.buffer, offset, offset + length, null);
        }
    }

    @Override
    public void clear()
    {
        if (size() > 0)
            changeNumber.getAndIncrement();
        if (this.size() > 0)
        {
            wipeBuffer(0, this.size());
        }
        this.count = 0;
        this.start = 0;
    }

    @Override
    public boolean contains(Object item)
    {
        return this.indexOf(item) != -1;
    }

    @Override
    public boolean remove(Object item)
    {
        boolean result = true;
        int index = indexOf(item);

        if (-1 == index)
        {
            result = false;
        }
        else
        {
            remove(index);
        }

        return result;
    }

    public int indexOf(Object item)
    {
        int index = 0;
        for (T myItem : this)
        {
            if (myItem.equals(item))
            {
                break;
            }
            ++index;
        }

        if (index == this.size())
        {
            index = -1;
        }

        return index;
    }

    public void remove(int index)
    {
        if (index == 0)
        {
            removeFirst();
            return;
        }
        else if (index == size() - 1)
        {
            removeLast();
            return;
        }

        removeRange(index, 1);
    }

    public void addFirst(T item)
    {
        changeNumber.getAndIncrement();

        ensureCapacityFor(1);
        buffer[postShiftStartOffset(-1)] = item;
        incrementCount(1);
    }

    public void addLast(T item)
    {
        changeNumber.getAndIncrement();

        ensureCapacityFor(1);
        buffer[toBufferIndex(this.size())] = item;
        incrementCount(1);
    }

    public T removeFirst()
    {
        if (this.isEmpty())
        {
            throw new IllegalStateException("The Deque is empty");
        }

        T result = (T) buffer[this.start];
        buffer[preShiftStartOffset(1)] = null;
        decrementCount(1);
        changeNumber.getAndIncrement();
        return result;
    }

    public T removeLast()
    {
        if (this.isEmpty())
        {
            throw new IllegalStateException("The Deque is empty");
        }

        decrementCount(1);
        int endIndex = toBufferIndex(this.size());
        T result = (T) buffer[endIndex];
        buffer[endIndex] = null;
        changeNumber.getAndIncrement();

        return result;
    }

    public void addAll(Iterable<T> collection)
    {
        addLastAll(collection);
    }

    public void addFrontAll(Iterable<T> collection)
    {
        addFrontAll(collection, 0, Utility.count(collection));
    }

    public void addFrontAll(Iterable<T> collection, int fromIndex, int count)
    {
        insertAll(0, collection, fromIndex, count);
    }

    public void addLastAll(Iterable<T> collection)
    {
        addLastAll(collection, 0, Utility.count(collection));
    }

    public void addLastAll(
            Iterable<T> collection,
            int fromIndex,
            int count)
    {
        insertAll(this.size(), collection, fromIndex, count);
    }

    public void insertAll(int index, Iterable<T> collection)
    {
        int count = Utility.count(collection);
        this.insertAll(index, collection, 0, count);
    }

    public void insertAll(
            int index,
            Iterable<T> collection,
            int fromIndex,
            int count)
    {
        checkIndexOutOfRange(index - 1);

        if (0 == count)
            return;

        changeNumber.getAndIncrement();

        // Make room
        ensureCapacityFor(count);

        if (index < this.size() / 2)
        {
            // Inserting into the first half of the list

            if (index > 0)
            {
                // Move items down:
                //  [0, index) ->
                //  [Capacity - count, Capacity - count + index)
                int shiftIndex = this.capacity() - count;
                for (int j = 0; j < index; j++)
                {
                    buffer[toBufferIndex(shiftIndex + j)] =
                            buffer[toBufferIndex(j)];
                }
            }

            // shift the starting offset
            this.shiftStartOffset(-count);

        }
        else
        {
            // Inserting into the second half of the list

            if (index < this.size())
            {
                // Move items up:
                // [index, Count) -> [index + count, count + Count)
                int copyCount = this.size() - index;
                int shiftIndex = index + count;
                for (int j = 0; j < copyCount; j++)
                {
                    buffer[toBufferIndex(shiftIndex + j)] =
                            buffer[toBufferIndex(index + j)];
                }
            }
        }

        // Copy new items into place
        int i = index;
        for (T item : collection)
        {
            buffer[toBufferIndex(i)] = item;
            ++i;
        }

        // Adjust valid count
        incrementCount(count);
    }

    public void removeRange(int index, int count)
    {
        if (count == 0)
            return;

        changeNumber.getAndIncrement();

        if (this.isEmpty())
        {
            throw new IllegalStateException("The Deque is empty");
        }
        if (index > size() - count)
        {
            throw new IndexOutOfBoundsException(
                    "The supplied index is greater than the Count");
        }

        // Clear out the underlying array
        wipeBuffer(index, count);

        if (index == 0)
        {
            // Removing from the beginning: shift the start offset
            this.shiftStartOffset(count);
            this.count -= count;
            return;
        }
        else if (index == size() - count)
        {
            // Removing from the ending: trim the existing view
            this.count -= count;
            return;
        }

        if ((index + (count / 2)) < size() / 2)
        {
            // Removing from first half of list

            // Move items up:
            //  [0, index) -> [count, count + index)
            for (int j = 0; j < index; j++)
            {
                buffer[toBufferIndex(count + j)]
                        = buffer[toBufferIndex(j)];
            }

            // Rotate to new view
            this.shiftStartOffset(count);
        }
        else
        {
            // Removing from second half of list

            // Move items down:
            // [index + collectionCount, count) ->
            // [index, count - collectionCount)
            int copyCount = size() - count - index;
            int readIndex = index + count;
            for (int j = 0; j < copyCount; ++j)
            {
                buffer[toBufferIndex(index + j)] =
                        buffer[toBufferIndex(readIndex + j)];
            }
        }

        // Adjust valid count
        decrementCount(count);
    }

    public T get(int index)
    {
        checkIndexOutOfRange(index);
        return (T) buffer[toBufferIndex(index)];
    }

    public void set(int index, T item)
    {
        checkIndexOutOfRange(index);
        buffer[toBufferIndex(index)] = item;
        changeNumber.getAndIncrement();
    }

    @Override
    public Iterator<T> iterator()
    {
        return new Iterator<T>()
        {
            long localChange = changeNumber.get();
            int current = 0;

            @Override
            public boolean hasNext()
            {
                if (localChange != changeNumber.get())
                    throw new ConcurrentModificationException();
                return current < size();
            }

            @Override
            public T next()
            {
                if (localChange != changeNumber.get())
                    throw new ConcurrentModificationException();
                return get(current++);
            }
        };
    }

    @Override
    public Object[] toArray()
    {
        return toArray(new Object[size()]);
    }
}
