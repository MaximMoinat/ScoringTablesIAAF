package util;

import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;

public abstract class NestedHashMap<K1,K2,V> extends HashMap<K1,TreeMap<K2, V>> {

    public V get(K1 key1, K2 key2) {
        return this.get(key1).get(key2);
    }

    public void put(K1 key1, K2 key2, V value) {
        if (!this.containsKey(key1)) {
            this.put(key1, new TreeMap<>());
        }
        this.get(key1).put(key2, value);
    }

    public boolean containsKey(K1 key1, K2 key2) {
        return this.containsKey(key1) && this.get(key1).containsKey(key2);
    }

    public Set<K1> keySet1() {
        return this.keySet();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (K1 key1 : this.keySet()) {
            result.append(key1);
            result.append('\n');
            for (K2 key2 : this.get(key1).keySet()) {
                result.append('\t');
                result.append(key2);
                result.append("\n\t\t");
                result.append(this.get(key1, key2));
                result.append('\n');
            }
        }
        return result.toString();
    }
}
