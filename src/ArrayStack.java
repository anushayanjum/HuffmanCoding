public class ArrayStack<E> implements Stack<E> {
    public static final int CAPACITY = 1000;  // default array capacity

    @SuppressWarnings("unchecked")
    private E[] data = (E[]) new Object[CAPACITY];  // safe generic array creation

    private int t = -1;  // index of the top element in stack

    /** Construct a stack with default capacity. */
    public ArrayStack() { }

    /**
     * Construct a stack with the given capacity.
     *
     * @param capacity maximum number of elements
     */
    @SuppressWarnings("unchecked")
    public ArrayStack(int capacity) {
        data = (E[]) new Object[capacity];
    }

    /** @return number of elements in the stack */
    public int size() {
        return t + 1;
    }

    /** @return true if the stack is empty */
    public boolean isEmpty() {
        return t == -1;
    }

    /**
     * Pushes an element onto the top of this stack.
     *
     * @param e element to push
     * @throws IllegalStateException if the stack is full
     */
    public void push(E e) {
        if (size() == data.length) {
            throw new IllegalStateException("Stack is full");
        }
        data[++t] = e;
    }

    /**
     * Peeks at the top element without removing it.
     *
     * @return top element, or null if empty
     */
    public E top() {
        return isEmpty() ? null : data[t];
    }

    /**
     * Pops and returns the top element.
     *
     * @return popped element, or null if empty
     */
    public E pop() {
        if (isEmpty()) return null;
        E answer = data[t];
        data[t] = null;  // dereference for GC
        t--;
        return answer;
    }

    /** Clears all elements from the stack. */
    public void clear() {
        // Optionally null out entries for GC:
        for (int i = 0; i <= t; i++) {
            data[i] = null;
        }
        t = -1;
    }
}
