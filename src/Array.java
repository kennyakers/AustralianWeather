
import java.util.Iterator;

public class Array<T> implements Iterable<T> {

    // This class is an ADT for a resizeable array.  The array is automatically resized
    // as necessary when new elements are added to the array using either set(index, value)
    // or append(value) or prepend(value).  The capacity of the array is doubled each time
    // that it is resized.
    // Definitions:
    //    SIZE is the 'length' of the array ... number of valid slots.
    //    CAPACITY is the total number of slots currently allocated for the array.
    //    STORAGE is the sum of the capacities for each time this array has been resied.
    private T[] array = null;	// The array data.
    private int length = 0;     // The length of the array.
    private int capacity = 0;	// Current storage allocation for the array.
    private int storage = 0;	// Total storage allocation for the array.

    public Array() {
        resize(1);
    }

    public Array(int capacity) {
        resize(capacity);
    }

    public Array(T[] values) {
        resize(values.length);
        for (T value : values) {
            append(value);
        }
    }

    public int size() {
        return this.length;
    }

    public int storage() {
        return this.storage;
    }

    public boolean contains(T value) {
        for (int i = 0; i < this.length; i++) {
            if (value.equals(this.array[i])) {
                return true;
            }
        }
        return false;
    }

    public int find(T value) {
        for (int i = 0; i < this.length; i++) {
            if (value.equals(this.array[i])) {
                return i;
            }
        }
        return -1;
    }

    public T get(int index) throws ArrayIndexOutOfBoundsException {
        if (index < 0 || index >= this.length) {
            throw new ArrayIndexOutOfBoundsException("Index out of bounds: " + index);
        } else {
            return array[index];
        }
    }

    public void set(int index, T value) throws ArrayIndexOutOfBoundsException {
        if (index < 0) {
            throw new ArrayIndexOutOfBoundsException("Index out of bounds: " + index);
        } else if (index >= this.capacity) {
            resize(index + 1);
        }

        this.array[index] = value;
        if (index >= this.length) {
            this.length = index + 1;
        }
    }

    public void append(T value) {
        if (this.length >= this.capacity) {
            resize(this.length + 1);
        }
        this.array[this.length++] = value;
    }

    public void prepend(T value) {
        if (this.length >= this.capacity) {
            resize(this.length + 1);
        }

        for (int i = this.length; i > 0; i--) {
            this.array[i] = this.array[i - 1];
        }

        this.array[0] = value;
        this.length++;
    }

    public void remove(T value) {
        int n = this.length;
        int j = 0;

        for (int i = 0; i < n; i++) {
            if (i != j) {
                this.array[j] = this.array[i];
            }
            if (!value.equals(this.array[i])) {
                j++;
            }
        }

        for (int i = j; i < n; i++) {
            this.array[i] = null;
            this.length--;
        }
    }

    public boolean equals(Iterable other) {
        Iterator iterator = other.iterator();
        for (int i = 0; i < this.length; i++) {
            if (!iterator.hasNext()) {
                return false;
            }
            if (!iterator.next().equals(this.array[i])) {
                return false;
            }
        }
        return !iterator.hasNext();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Iterable) {
            return equals((Iterable) other);
        } else {
            return false;
        }
    }

    private void resize(int minimum) {

        // Round the capacity up to the next power of two
        // greater than the minimum requested capacity.
        // This implements the doubling policy.
        int capacity = Math.max(1, this.capacity);
        while (capacity < minimum) {
            capacity *= 2;
        }

        // Save the current contents of the array and
        // allocate a new one with the desired capacity.
        T[] data = this.array;
        this.array = (T[]) new Object[capacity];
        this.storage += capacity;
        this.capacity = capacity;

        // Copy the current contents of the array
        // into the newly allocated storage.
        if (data != null) {
            for (int i = 0; i < this.length; i++) {
                this.array[i] = data[i];
            }
        }
    }

    // An image function to format an array as an aggregate: "{2, 3, 5, 7}"
    // The empty array is formatted as "{}"
    @Override
    public String toString() {
        String result = "{";
        boolean first = true;
        for (T element : this) {
            if (!first) {
                result += ", ";
            }
            result += element;
            first = false;
        }
        result += "}";
        return result;
    }

    // Both forward and reverse iterators are provided for the array.
    // Forward iterators traverse the array elements 0 to length-1
    // while reverse iterators traverse the array elements from
    // length-1 down to 0.
    public Iterator<T> forwardIterator() {
        return new ForwardIterator();
    }

    public Iterator<T> reverseIterator() {
        return new ReverseIterator();
    }

    public Iterator<T> iterator() {
        return forwardIterator();
    }

    private class ForwardIterator implements Iterator<T> {

        private int index;

        public ForwardIterator() {
            this.index = 0;
        }

        public boolean hasNext() {
            return this.index < Array.this.length;
        }

        public T next() {
            return Array.this.array[this.index++];
        }

    }

    private class ReverseIterator implements Iterator<T> {

        private int index;

        public ReverseIterator() {
            this.index = Array.this.length;
        }

        public boolean hasNext() {
            return this.index > 0;
        }

        public T next() {
            return Array.this.array[--this.index];
        }

    }

}
