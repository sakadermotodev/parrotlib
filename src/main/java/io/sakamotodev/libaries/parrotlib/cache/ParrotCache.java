package io.sakamotodev.libaries.parrotlib.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ParrotCache<K, V> {

    private final Map<K, V> map = new ConcurrentHashMap<>();

    public void put(K key, V value) {
        map.put(key, value);
    }

    public V get(K key) {
        return map.get(key);
    }

    public boolean contains(K key) {
        return map.containsKey(key);
    }

    public void remove(K key) {
        map.remove(key);
    }

    public Map<K, V> raw() {
        return map;
    }
}
