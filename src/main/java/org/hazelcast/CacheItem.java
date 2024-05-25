package org.hazelcast;

import java.io.Serializable;

class CacheItem<K, V> implements Serializable {

    private V value;

	private Long timeToLive;

	private CacheItem<K, V> prev;
	private CacheItem<K, V> next;
	private K key;

    public CacheItem(K key, V value, long ttl) {
		this.setKey(key);
		this.setValue(value);
		this.setTimeToLive(ttl);
    }
	public V getValue() {
        return value;
    }

	public void setValue(V value) {
		this.value = value;
	}

	public K getKey() {
		return key;
	}

	public void setKey(K key) {
		this.key = key;
	}

	public String toString() {

		// this works on primitives type only
		return (String) this.value;
	}

	public void setTimeToLive(Long timeToLive) {
		this.timeToLive = timeToLive;
	}

	public boolean isExpired() {
		return System.currentTimeMillis() > timeToLive;
	}

	public CacheItem<K,V> getPrev() {
		return prev;
	}

	public void setPrev(CacheItem<K,V> prev) {
		this.prev = prev;
	}

	public CacheItem<K,V> getNext() {
		return next;
	}

	public void setNext(CacheItem<K,V> next) {
		this.next = next;
	}



}
