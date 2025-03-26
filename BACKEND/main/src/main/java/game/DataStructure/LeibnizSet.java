package game.DataStructure;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class LeibnizSet <E> implements Set<E> {

    HashMap<Integer, E> elements;

    public LeibnizSet() {
        elements = new HashMap<>();
    }

    @Override
    public Iterator<E> iterator() {
        return elements.values().iterator();
    }

    @Override
    public Object[] toArray() {
        return elements.values().toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return (T[]) elements.values().toArray();
    }

    @Override
    public boolean add(E e) {
        if(elements.containsKey(e.hashCode())) return false;
        elements.put(e.hashCode(), e);
        return true;
    }

    @Override
    public boolean remove(Object o) {
        if(!elements.containsKey(o.hashCode())) return false;
        elements.remove(o.hashCode());
        return true;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return false;
    }

    @Override
    public void clear() {

    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean contains(Object o) {
        return false;
    }
}
