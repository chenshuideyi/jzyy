package com.csdy.jzyy.java;

import java.util.HashMap;

public class NonNullHashMap<K, V> extends HashMap<K, V> {
    @Override
    public V put(K key, V value) {
        if (value == null)
            throw new NullPointerException("Map value is null.");
        return super.put(key, value);
    }
}
