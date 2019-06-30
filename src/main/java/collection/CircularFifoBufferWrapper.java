package collection;

import logger.AppLogger;
import org.apache.commons.collections.buffer.CircularFifoBuffer;
import org.codehaus.jackson.annotate.JsonTypeInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/*
We have this class to overcome JSON serialization problem.
It creates buffer with default constructor, which configures the default buffer size.
However we want a different buffer size.
 */
@SuppressWarnings("unchecked")
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
@org.codehaus.jackson.annotate.JsonIgnoreProperties(ignoreUnknown = true)
public class CircularFifoBufferWrapper<T> implements Iterable<T> {
    private static final AppLogger logger = AppLogger.getLogger(CircularFifoBufferWrapper.class);

    private int maxSize;

    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
    private CircularFifoBuffer buffer;

    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
    private ArrayList arrayForSerialize;

    // used by JSON
    @SuppressWarnings("unused")
    public CircularFifoBufferWrapper() {
        this.buffer = null;
        this.maxSize = 0;
    }

    public CircularFifoBufferWrapper(int size) {
        logger.debug("init with size {}", size);
        this.buffer = new CircularFifoBuffer(size);
        this.maxSize = size;
    }

    public CircularFifoBufferWrapper(CircularFifoBufferWrapper other) {
        this.buffer = new CircularFifoBuffer(other.buffer.maxSize());
        this.maxSize = other.maxSize();
        this.buffer.addAll(other.buffer);
    }

    // used by JSON
    @SuppressWarnings("unused")
    public ArrayList getArrayForSerialize() {
        arrayForSerialize = new ArrayList(buffer.size());
        arrayForSerialize.addAll(buffer);
        return arrayForSerialize;
    }

    // used by JSON
    @SuppressWarnings("unused")
    public void setArrayForSerialize(ArrayList arrayForSerialize) {
        logger.debug("set buffer for serialize {}", arrayForSerialize);
        this.arrayForSerialize = arrayForSerialize;
        buildBufferIfPossible();
    }

    @com.fasterxml.jackson.annotation.JsonIgnore
    @org.codehaus.jackson.annotate.JsonIgnore
    public CircularFifoBuffer getBuffer() {
        return this.buffer;
    }

    @com.fasterxml.jackson.annotation.JsonIgnore
    @org.codehaus.jackson.annotate.JsonIgnore
    public void setBuffer(CircularFifoBuffer buffer) throws Exception {
        logger.debug("set buffer is ignored due to JSON build issues{}", buffer);
    }

    // used by JSON
    @SuppressWarnings("unused")
    public int getMaxSize() {
        return this.maxSize;
    }

    // used by JSON
    @SuppressWarnings("unused")
    public void setMaxSize(int maxSize) throws Exception {
        logger.debug("set max size {}", maxSize);
        this.maxSize = maxSize;
        buildBufferIfPossible();
    }

    private void buildBufferIfPossible() {
        if (maxSize == 0) {
            return;
        }
        if (arrayForSerialize == null) {
            return;
        }
        logger.debug("building buffer");
        buffer = new CircularFifoBuffer(maxSize);
        buffer.addAll(arrayForSerialize);
        arrayForSerialize = null;
    }

    public Iterator<T> iterator() {
        return buffer.iterator();
    }

    public T lastElement() {
        Iterator<T> iterator = iterator();
        T last = null;
        while (iterator.hasNext()) {
            last = iterator.next();
        }
        return last;
    }

    public boolean add(T o) {
        return this.buffer.add(o);
    }

    public boolean remove(Object o) {
        return this.buffer.remove(o);
    }

    public boolean addAll(Collection collection) {
        return this.buffer.addAll(collection);
    }

    public int size() {
        return buffer.size();
    }

    @com.fasterxml.jackson.annotation.JsonIgnore
    @org.codehaus.jackson.annotate.JsonIgnore
    public boolean isEmpty() {
        return this.buffer.isEmpty();
    }

    public boolean contains(Object o) {
        return this.buffer.contains(o);
    }

    @com.fasterxml.jackson.annotation.JsonIgnore
    @org.codehaus.jackson.annotate.JsonIgnore
    public boolean isFull() {
        return this.buffer.isFull();
    }

    public int maxSize() {
        return this.maxSize;
    }

    public Object remove() {
        return this.buffer.remove();
    }

    public T get() {
        return (T) this.buffer.get();
    }

    //NOTICE: not the default equals, do not regenerate
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }
        if (other.getClass() != getClass()) {
            return false;
        }

        CircularFifoBufferWrapper otherWrapper = (CircularFifoBufferWrapper) other;

        if (maxSize != otherWrapper.maxSize) {
            return false;
        }
        if (buffer.size() != otherWrapper.buffer.size()) {
            return false;
        }

        Iterator<T> iterA = this.iterator();
        Iterator<T> iterB = otherWrapper.iterator();
        while (iterA.hasNext()) {
            T a = iterA.next();
            T b = iterB.next();
            if (!a.equals(b)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = maxSize;
        result = 31 * result + buffer.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return buffer.toString();
    }
}
