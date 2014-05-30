package cmsc420.sortedmap;

import java.util.Collection;
import java.util.Comparator;
import java.util.TreeMap;
    
public class GuardedAvlGTree<K, V> extends AvlGTree<K, V> {
    private TreeMap<K, V> guard;
    private boolean shouldWarn = true;
    public GuardedAvlGTree(Comparator<? super K> comparator, int maxBalance) {
        super(comparator, maxBalance);
        guard = new TreeMap<K, V>(comparator);
    }
    public V put (final K key, final V val) {
        final V ret = super.put(key, val);
        final V correctVal = guard.put(key, val);
        if (shouldWarn && ret != correctVal) // Object equality is sufficient
            System.err.println("My map said " + ret + " instead of " + correctVal);
        return correctVal; // Save yourself
    }
    @Override
    public boolean containsKey(Object key) {
        final boolean ret = super.containsKey(key);
        final boolean correctVal = guard.containsKey(key);
        if (shouldWarn && ret != correctVal) // Object equality is sufficient
            System.err.println("My map said " + ret + " instead of " + correctVal);
        return correctVal; // Save yourself
    }
    @Override
    public V get(Object key) {
        final V ret = super.get(key);
        final V correctVal = guard.get(key);
        if (shouldWarn && ret != correctVal) // Object equality is sufficient
            System.err.println("My map said " + ret + " instead of " + correctVal);
        return correctVal; // Save yourself
    }
    @Override
    public boolean isEmpty() {
        final boolean ret = super.isEmpty();
        final boolean correctVal = guard.isEmpty();
        if (shouldWarn && ret != correctVal) // Object equality is sufficient
            System.err.println("My map said " + ret + " instead of " + correctVal);
        return correctVal; // Save yourself
    }
    @Override
    public V remove(Object key) {
        throw new UnsupportedOperationException();
    }
    @Override
    public int size() {
        final int ret = super.size();
        final int correctVal = guard.size();
        if (shouldWarn && ret != correctVal) // Object equality is sufficient
            System.err.println("My map said " + ret + " instead of " + correctVal);
        return correctVal; // Save yourself
    }
    @Override
    public void clear() {
        super.clear();
        guard.clear();
    }

    @Override
    public Collection<V> values() {
        return guard.values();
    }
}