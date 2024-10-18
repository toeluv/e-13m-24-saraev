package ru.nti.triplet;

import java.util.*;

public class TripletDeque<E> implements Containerable, Deque<E> {

    private final int CONTAINER_SIZE = 5;
    private static final int DEFAULT_QUEUE_SIZE = 1000;
    private int queueSize;
    private int maxQueueSize;
    private Container first;
    private Container last;

    public TripletDeque(int maxQueueSize) {
        this.maxQueueSize = maxQueueSize;
        this.queueSize = 0;
        this.first = new Container();
        this.last = first;
    }

    public TripletDeque() {
        this(DEFAULT_QUEUE_SIZE);
    }

    @Override
    public void addFirst(E e) {
        offerFirst(e);
    }

    @Override
    public void addLast(E e) {
        offerLast(e);
    }

    @Override
    public boolean offerFirst(E e) {
        if (e == null) {
            throw new NullPointerException("Can't insert null in queue");
        }
        if (queueSize >= maxQueueSize) {
            return false;
        }
        if (first.isFull()) {
            Container newFirst = new Container();
            newFirst.next = first;
            first.previous = newFirst;
            first = newFirst;
        }
        first.addFirst(e);
        queueSize++;
        return true;
    }

    @Override
    public boolean offerLast(E e) {
        if (e == null) {
            throw new NullPointerException("Can't insert null in queue");
        }
        if (queueSize >= maxQueueSize) {
            return false;
        }
        if (last.isFull()) {
            Container newLast = new Container();
            newLast.previous = last;
            last.next = newLast;
            last = newLast;
        }
        last.addLast(e);
        queueSize++;
        return true;
    }

    @Override
    public E removeFirst() {
        if (queueSize == 0) {
            throw new NoSuchElementException();
        }
        E element = first.removeFirst();
        queueSize--;
        if (first.isEmpty() && first.next != null) {
            first = first.next;
            first.previous = null;
        }
        return element;
    }

    @Override
    public E removeLast() {
        if (queueSize == 0) {
            throw new NoSuchElementException();
        }
        E element = last.removeLast();
        queueSize--;
        if (last.isEmpty() && last.previous != null) {
            last = last.previous;
            last.next = null;
        }
        return element;
    }

    @Override
    public E pollFirst() {
        try {
            return removeFirst();
        } catch (NoSuchElementException ex) {
            return null;
        }
    }

    @Override
    public E pollLast() {
        try {
            return removeLast();
        } catch (NoSuchElementException ex) {
            return null;
        }
    }

    @Override
    public E getFirst() {
        if (queueSize == 0) {
            throw new NoSuchElementException();
        }
        return first.getFirst();
    }

    @Override
    public E getLast() {
        if (queueSize == 0) {
            throw new NoSuchElementException();
        }
        return last.getLast();
    }

    @Override
    public E peekFirst() {
        try {
            return getFirst();
        } catch (NoSuchElementException ex) {
            return null;
        }
    }

    @Override
    public E peekLast() {
        try {
            return getLast();
        } catch (NoSuchElementException ex) {
            return null;
        }
    }

    @Override
    public boolean removeFirstOccurrence(Object o) {
        for (Container container = first; container != null; container = container.next) {
            if (container.removeAsFromFirst(o)) {
                queueSize--;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean removeLastOccurrence(Object o) {
        for (Container container = last; container != null; container = container.previous) {
            if (container.removeAsFromLast(o)) {
                queueSize--;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean add(E e) {
        return offerLast(e);
    }

    @Override
    public boolean offer(E e) {
        return offerLast(e);
    }

    @Override
    public E remove() {
        return removeFirst();
    }

    @Override
    public E poll() {
        return pollFirst();
    }

    @Override
    public E element() {
        return getFirst();
    }

    @Override
    public E peek() {
        return peekFirst();
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        for (E e : c) {
            addLast(e);
        }
        return true;
    }

    @Override
    public void push(E e) {
        addFirst(e);
    }

    @Override
    public E pop() {
        return removeFirst();
    }

    @Override
    public boolean remove(Object o) {
        return removeFirstOccurrence(o);
    }

    @Override
    public boolean contains(Object o) {
        for (Container container = first; container != null; container = container.next) {
            for (int i = 0; i < container.elementsCount; i++) {
                if (Objects.equals(o, container.array[i])) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public int size() {
        return queueSize;
    }

    @Override
    public boolean isEmpty() {
        return queueSize == 0;
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<>() {
            private Container currentContainer = first;
            private int positionInsideContainer = 0;

            @Override
            public boolean hasNext() {
                return currentContainer != null
                        && (positionInsideContainer < currentContainer.elementsCount
                        || currentContainer.next != null);
            }

            @Override
            public E next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                if (positionInsideContainer >= currentContainer.elementsCount) {
                    currentContainer = currentContainer.next;
                    positionInsideContainer = 0;
                }
                E e = currentContainer.array[positionInsideContainer];
                positionInsideContainer++;
                return e;
            }
        };
    }

    @Override
    public Object[] getContainerByIndex(int cIndex) {
        int count = 0;
        Container container = first;
        while (container != null && count < cIndex) {
            container = container.next;
            count++;
        }
        return container != null ? container.array : null;
    }

    @Override
    public void clear() {
        Container current = first;
        while (current != null) {
            Container next = current.next;
            current.array = null;
            current.next = null;
            current.previous = null;
            current = next;
        }
        this.first = new Container();
        this.last = first;
        this.queueSize = 0;
    }

    private class Container {

        private E[] array;
        private Container next;
        private Container previous;
        private int elementsCount;

        public Container() {
            this.array = (E[]) new Object[CONTAINER_SIZE];
            this.elementsCount = 0;
        }

        private boolean isFull() {
            return elementsCount == array.length;
        }

        private boolean isEmpty() {
            return elementsCount == 0;
        }

        public boolean removeAsFromFirst(Object o) {
            for (int i = 0; i < elementsCount; i++) {
                if (Objects.equals(array[i], o)) {
                    removeByIndex(i);
                    return true;
                }
            }
            return false;
        }

        public boolean removeAsFromLast(Object o) {
            for (int i = elementsCount - 1; i >= 0; i--) {
                if (Objects.equals(array[i], o)) {
                    removeByIndex(i);
                    return true;
                }
            }
            return false;
        }

        public void addFirst(E e) {
            int freeIndex = array.length - 1;
            while (freeIndex >= 0 && array[freeIndex] != null) {
                freeIndex--;
            }
            if (freeIndex > 0) {
                for (int i = freeIndex; i > 0; i--) {
                    array[i] = array[i - 1];
                }
            }
            array[freeIndex] = e;
            elementsCount++;
        }

        public void addLast(E e) {
            array[elementsCount] = e;
            elementsCount++;
        }

        private E removeFirst() {
            for (int i = 0; i < array.length; i++) {
                if (array[i] != null) {
                    E toRemove = array[i];
                    removeByIndex(i);
                    return toRemove;
                }
            }
            return null;
        }

        private E removeLast() {
            int lastIndex = array.length - 1;
            while (lastIndex >= 0 && array[lastIndex] == null) {
                lastIndex--;
            }
            E e = array[lastIndex];
            array[lastIndex] = null;
            elementsCount--;
            return e;
        }

        private E getFirst() {
            for (int i = 0; i < array.length; i++) {
                if (array[i] != null) {
                    return array[i];
                }
            }
            return null;
        }

        private E getLast() {
            for (int i = array.length - 1; i >= 0; i--) {
                if (array[i] != null) {
                    return array[i];
                }
            }
            return null;
        }

        private void removeByIndex(int index) {
            for (int i = index; i < elementsCount - 1; i++) {
                array[i] = array[i + 1];
            }
            array[elementsCount - 1] = null;
            elementsCount--;
            if (next != null && !next.isEmpty()) {
                array[elementsCount] = next.removeFirst();
                elementsCount++;
            }
            if (next != null && next.isEmpty()) {
                next = null;
            }
        }
    }

    /*isn't need to implement*/
    @Override
    public boolean removeAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return false;
    }

    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException("Unrealised");
    }

    @Override
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException("Unrealised");
    }

    @Override
    public Iterator<E> descendingIterator() {
        throw new UnsupportedOperationException("Unrealised");
    }
}