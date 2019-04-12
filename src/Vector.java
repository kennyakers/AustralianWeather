
import java.util.Arrays;
import java.util.Iterator;

public class Vector {

    private double[] vector;	// The array data.
    private int length = 0;     // The length of the array.
    private int capacity = 0;	// Current storage allocation for the array.
    private int storage = 0;	// Total storage allocation for the array.

    public Vector() {
        this(new double[1]); // Make a new Vector of length 1.
    }

    public Vector(double[] inputVec) {
        // Do a deep copy of inputVec, so changes to inputVec do not affect this.vector.
        resize(inputVec.length);
        for (double value : inputVec) {
            this.append(value);
        }
    }

    public Vector plus(Vector other) {
        if (this.vector.length != other.vector.length) {
            throw new IllegalArgumentException("Vector lengths do not match.");
        }

        double[] result = new double[this.vector.length];
        for (int i = 0; i < this.vector.length; i++) {
            result[i] = this.vector[i] + other.vector[i];
        }
        return new Vector(result);
    }

    public Vector negate() {
        double[] result = new double[this.vector.length];
        for (int i = 0; i < this.vector.length; i++) {
            result[i] *= -1.0;
        }
        return new Vector(result);
    }

    public Vector minus(Vector other) {
        Vector negated = other.negate();
        return this.plus(negated);
    }

    public Vector scale(double scalar) {
        double[] result = new double[this.vector.length];
        for (int i = 0; i < this.vector.length; i++) {
            result[i] = this.vector[i] * scalar;
        }
        return new Vector(result);
    }

    public double dot(Vector other) {
        if (this.vector.length != other.vector.length) {
            throw new IllegalArgumentException("Vector lengths do not match.");
        }
        double sum = 0.0;
        for (int i = 0; i < this.vector.length; i++) {
            sum += this.vector[i] * other.vector[i];
        }
        return sum;
    }

    public double norm() {
        return Math.sqrt(this.dot(this));
    }

    public double normSquared() {
        return this.dot(this);
    }

    public boolean equals(Vector other) {
        return Arrays.equals(this.vector, other.vector);
    }

    public double[] getVectorComponents() {
        double[] toReturn = new double[this.length()];
        for (int i = 0; i < this.length(); i++) {
            toReturn[i] = this.vector[i];
        }
        return toReturn;
    }

    //---------------------------Resizing array methods-------------------------
    public int length() { // Returns the number of elements in the array, not how long it is.
        return this.length;
    }

    public int size() { // Returns how many slots are in the array, not how many things are in them.
        return this.vector.length;
    }

    public int storage() {
        return this.storage;
    }

    public boolean contains(double value) {
        for (int i = 0; i < this.length; i++) {
            if (this.vector[i] == value) {
                return true;
            }
        }
        return false;
    }

    public int find(double value) {
        for (int i = 0; i < this.length; i++) {
            if (this.vector[i] == value) {
                return i;
            }
        }
        return -1;
    }

    public double get(int index) throws ArrayIndexOutOfBoundsException {
        if (index < 0 || index >= this.length) {
            throw new ArrayIndexOutOfBoundsException("Index out of bounds: " + index);
        } else {
            return vector[index];
        }
    }

    public void set(int index, double value) throws ArrayIndexOutOfBoundsException {
        if (index < 0) {
            throw new ArrayIndexOutOfBoundsException("Index out of bounds: " + index);
        } else if (index >= this.capacity) {
            resize(index + 1);
        }

        this.vector[index] = value;
        if (index >= this.length) {
            this.length = index + 1;
        }
    }

    public void append(double value) {
        if (this.length >= this.capacity) {
            resize(this.length + 1);
        }
        this.vector[this.length++] = value;
    }

    public void prepend(double value) {
        if (this.length >= this.capacity) {
            resize(this.length + 1);
        }

        for (int i = this.length; i > 0; i--) {
            this.vector[i] = this.vector[i - 1];
        }

        this.vector[0] = value;
        this.length++;
    }

    public void remove(double value) {
        int n = this.length;
        int j = 0;

        for (int i = 0; i < n; i++) {
            if (i != j) {
                this.vector[j] = this.vector[i];
            }
            if (this.vector[i] != value) {
                j++;
            }
        }

        for (int i = j; i < n; i++) {
            this.vector[i] = 0.0;
            this.length--;
        }
    }

    public boolean equals(Iterable other) {
        Iterator iterator = other.iterator();
        for (int i = 0; i < this.length; i++) {
            if (!iterator.hasNext()) {
                return false;
            }
            if (!iterator.next().equals(this.vector[i])) {
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
        double[] data = this.vector;
        this.vector = new double[capacity];
        this.storage += capacity;
        this.capacity = capacity;

        // Copy the current contents of the array
        // into the newly allocated storage.
        if (data != null) {
            for (int i = 0; i < this.length; i++) {
                this.vector[i] = data[i];
            }
        }
    }

    // An image function to format an array as an aggregate: "{2, 3, 5, 7}"
    // The empty array is formatted as "{}"
    @Override
    public String toString() {
        String result = "{";
        boolean first = true;
        for (int i = 0; i < this.length(); i++) {
            if (!first) {
                result += ", ";
            }
            result += this.get(i);
            first = false;
        }
        result += "}";
        return result;
    }
}
