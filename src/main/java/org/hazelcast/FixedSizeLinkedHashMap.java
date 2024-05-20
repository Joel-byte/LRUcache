package org.hazelcast;

import java.util.LinkedHashMap;
import java.util.Map;

public class FixedSizeLinkedHashMap<K, V> extends LinkedHashMap<K, V> {
    private int maxSize;

    public FixedSizeLinkedHashMap(int maxSize) {
        this.setMaxSize(maxSize);
    }

    // this method is use internally by the system to remove the eldest entry in the map
    // depending on the value returned
    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > maxSize;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

}
