package parser.type;

import java.util.Iterator;

class GenericWithInnerExample<T> implements Iterable<T> {

    private Node<T> first;

    GenericWithInnerExample() {
    }

    GenericWithInnerExample(Iterable<T> iterable) {
        for (T element : iterable) {
            push(element);
        }
    }

    void push(T element) {
        final Node<T> node = new Node<>(element);
        if (this.first == null) {
            this.first = node;
        } else {
            Node<T> last = this.first;
            while (last.next != null) {
                last = last.next;
            }
            last.next = node;
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new LinkedListIterator<>(this.first);
    }

    private static class Node<T> {
        private final T value;
        private Node<T> next;

        private Node(T value) {
            this.value = value;
        }
    }

    private static class LinkedListIterator<T> implements Iterator<T> {
        private Node<T> node;

        private LinkedListIterator(Node<T> node) {
            this.node = node;
        }

        @Override
        public boolean hasNext() {
            return this.node != null;
        }

        @Override
        public T next() {
            T value = this.node.value;
            this.node = this.node.next;
            return value;
        }
    }
}
