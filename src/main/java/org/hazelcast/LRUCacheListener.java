package org.hazelcast;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import com.hazelcast.map.MapEvent;

public class LRUCacheListener<K, V> implements EntryListener<K,V> {
    EntryEvent<K,V> event;
    public EntryEvent<K, V> getEvent() {
        return event;
    }
    public void setEvent(EntryEvent<K, V> event) {
        this.event = event;
    }

    @Override
    public void entryAdded(EntryEvent<K, V> event) {
        setEvent(event);
    }

    @Override
    public void entryEvicted(EntryEvent<K, V> event) {
        setEvent(event);
    }

    @Override
    public void entryExpired(EntryEvent<K, V> event) {
        setEvent(event);
    }

    @Override
    public void entryRemoved(EntryEvent<K, V> event) {
        setEvent(event);
    }

    @Override
    public void entryUpdated(EntryEvent<K, V> event) {
        setEvent(event);
    }

    @Override
    public void mapCleared(MapEvent event) {
    }

    @Override
    public void mapEvicted(MapEvent event) {

    }
}
