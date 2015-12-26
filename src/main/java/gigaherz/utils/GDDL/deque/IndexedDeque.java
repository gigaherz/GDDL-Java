package gigaherz.utils.GDDL.deque;

import gigaherz.utils.GDDL.Utility;
import sun.plugin.dom.exception.InvalidStateException;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@SuppressWarnings("unchecked,unused")
public class IndexedDeque<T> implements List<T>
{
    private static final int defaultCapacity = 16;

    private final AtomicLong changeNumber = new AtomicLong(0);

    private int capacityMask;
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

        setCapacity(capacity);
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
            throw new IndexOutOfBoundsException(
                    "Capacity is less than 0.");

        if (value < size())
            throw new IllegalStateException(
                    "Capacity cannot be set to a value less than the number of elements");

        value = Utility.upperPower(value);
        if (null != buffer && value == buffer.length)
            return;

        changeNumber.getAndIncrement();

        Object[] newBuffer = new Object[value];
        buffer = toArray(newBuffer);
        start = 0;
        capacityMask = value - 1;
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
        return size() == capacity();
    }

    @Override
    public boolean isEmpty()
    {
        return 0 == size();
    }

    private void ensureCapacityFor(int numElements)
    {
        if (size() + numElements > capacity())
        {
            setCapacity(size() + numElements);
        }
    }

    private int toBufferIndex(int index)
    {
        return (index + start) & capacityMask;
    }

    private int shiftStartPost(int value)
    {
        int offset = start;
        start = toBufferIndex(value);
        return offset;
    }

    private int shiftStartPre(int value)
    {
        start = toBufferIndex(value);
        return start;
    }

    @Override
    public int size()
    {
        return count;
    }

    private void incrementCount(int value)
    {
        count += value;
    }

    private void decrementCount(int value)
    {
        count = Math.max(size() - value, 0);
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
    public boolean addAll(int index, Collection<? extends T> c)
    {
        return false;
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

    private void wipeBuffer(int start, int length)
    {
        int offset = toBufferIndex(start);
        if (offset + length > capacity())
        {
            int len = capacity() - offset;
            Arrays.fill(buffer, offset, offset + len, null);

            len = toBufferIndex(start + length);
            Arrays.fill(buffer, 0, len, null);
        }
        else
        {
            Arrays.fill(buffer, offset, offset + length, null);
        }
    }

    @Override
    public void clear()
    {
        if (size() > 0)
        {
            changeNumber.getAndIncrement();
            wipeBuffer(0, size());
        }
        count = 0;
        start = 0;
    }

    @Override
    public boolean contains(Object item)
    {
        return indexOf(item) != -1;
    }

    @Override
    public boolean remove(Object item)
    {
        int index = indexOf(item);

        if (-1 == index)
            return false;

        remove(index);
        return true;
    }

    @Override
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

        if (index == size())
        {
            index = -1;
        }

        return index;
    }

    @Override
    public int lastIndexOf(Object o)
    {
        // TODO
        return 0;
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex)
    {
        throw new IllegalStateException("Operation not implemented");
    }

    public T remove(int index)
    {
        T prev = get(index);

        if (index == 0)
        {
            removeFirst();
            return prev;
        }

        if (index == size() - 1)
        {
            removeLast();
            return prev;
        }

        removeRange(index, 1);

        return prev;
    }

    public void addFirst(T item)
    {
        changeNumber.getAndIncrement();

        ensureCapacityFor(1);
        buffer[shiftStartPre(-1)] = item;
        incrementCount(1);
    }

    public void addLast(T item)
    {
        changeNumber.getAndIncrement();

        ensureCapacityFor(1);
        buffer[toBufferIndex(size())] = item;
        incrementCount(1);
    }

    public T removeFirst()
    {
        if (isEmpty())
        {
            throw new IllegalStateException("The Deque is empty");
        }

        changeNumber.getAndIncrement();

        T result = (T) buffer[start];
        buffer[shiftStartPost(1)] = null;
        decrementCount(1);
        return result;
    }

    public T removeLast()
    {
        if (isEmpty())
        {
            throw new IllegalStateException("The Deque is empty");
        }

        changeNumber.getAndIncrement();

        decrementCount(1);
        int endIndex = toBufferIndex(size());
        T result = (T) buffer[endIndex];
        buffer[endIndex] = null;

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
        insertAll(size(), collection, fromIndex, count);
    }

    public void insertAll(int index, Iterable<T> collection)
    {
        int count = Utility.count(collection);
        insertAll(index, collection, 0, count);
    }

    public void insertAll(
            int index,
            Iterable<T> collection,
            int fromIndex,
            int count)
    {
        if (index - 1 >= size())
        {
            throw new IndexOutOfBoundsException(
                    "The supplied index is greater than the Count");
        }

        if (0 == count)
            return;

        changeNumber.getAndIncrement();

        // Make room
        ensureCapacityFor(count);

        if (index < size() / 2)
        {
            // Inserting into the first half of the list

            if (index > 0)
            {
                // Move items down:
                //  [0, index) ->
                //  [Capacity - count, Capacity - count + index)
                int shiftIndex = capacity() - count;
                for (int j = 0; j < index; j++)
                {
                    buffer[toBufferIndex(shiftIndex + j)] =
                            buffer[toBufferIndex(j)];
                }
            }

            // shift the starting offset
            int value = -count;
            toBufferIndex(value);

        }
        else
        {
            // Inserting into the second half of the list

            if (index < size())
            {
                // Move items up:
                // [index, Count) -> [index + count, count + Count)
                int copyCount = size() - index;
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

        if (isEmpty())
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
            toBufferIndex(count);
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
            toBufferIndex(count);
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
        if (index >= size())
        {
            throw new IndexOutOfBoundsException(
                    "The supplied index is greater than the Count");
        }
        return (T) buffer[toBufferIndex(index)];
    }

    @Override
    public T set(int index, T item)
    {
        if (index >= size())
        {
            throw new IndexOutOfBoundsException(
                    "The supplied index is greater than the Count");
        }

        T prev = get(index);
        if(prev == item)
            return prev;
        changeNumber.getAndIncrement();
        buffer[toBufferIndex(index)] = item;
        return prev;
    }

    @Override
    public void add(int index, T element)
    {

    }

    @Override
    public ListIterator<T> listIterator()
    {
        return listIterator(0);
    }

    @Override
    public Iterator<T> iterator()
    {
        return listIterator(0);
    }

    @Override
    public ListIterator<T> listIterator(final int index)
    {
        return new ListIterator<T>()
        {
            long localChange = changeNumber.get();
            int current = index;
            int modificationIndex = -1;

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
                modificationIndex = current;
                return get(current++);
            }

            @Override
            public boolean hasPrevious()
            {
                if (localChange != changeNumber.get())
                    throw new ConcurrentModificationException();
                return current > 0;
            }

            @Override
            public T previous()
            {
                if (localChange != changeNumber.get())
                    throw new ConcurrentModificationException();
                modificationIndex = --current;
                return get(current);
            }

            @Override
            public int nextIndex()
            {
                if (localChange != changeNumber.get())
                    throw new ConcurrentModificationException();
                return current;
            }

            @Override
            public int previousIndex()
            {
                if (localChange != changeNumber.get())
                    throw new ConcurrentModificationException();
                return current-1;
            }

            @Override
            public void remove()
            {
                if(modificationIndex < 0)
                    throw new IllegalStateException();
                if (localChange != changeNumber.get())
                    throw new ConcurrentModificationException();
                IndexedDeque.this.remove(modificationIndex);
                modificationIndex = -1;
                localChange = changeNumber.get();
            }

            @Override
            public void set(T t)
            {
                if(modificationIndex < 0)
                    throw new IllegalStateException();
                if (localChange != changeNumber.get())
                    throw new ConcurrentModificationException();
                IndexedDeque.this.set(modificationIndex, t);
                localChange = changeNumber.get();
            }

            @Override
            public void add(T t)
            {
                if(modificationIndex < 0)
                    throw new IllegalStateException();
                if (localChange != changeNumber.get())
                    throw new ConcurrentModificationException();
                IndexedDeque.this.add(modificationIndex, t);
                modificationIndex++;
                current++;
                localChange = changeNumber.get();
            }
        };
    }

    @Override
    public Object[] toArray()
    {
        return toArray(new Object[size()]);
    }
}
