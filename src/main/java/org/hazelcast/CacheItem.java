package org.hazelcast;

import java.io.Serializable;

class CacheItem<V> implements Serializable {

    private final V value;
    private Long timeToLive;
    
    public CacheItem(V value, long ttl) {
        this.value = value;
        this.setTimeToLive(ttl);
    }

	public V getValue() {
        return value;
    }

	public String toString() {

		// this works on primitives type only
		return (String) this.value;
	}

	public Long getTimeToLive() {
		return timeToLive;
	}

	public void setTimeToLive(Long timeToLive) {
		this.timeToLive = timeToLive;
	}

	public boolean isExpired() {
		return System.currentTimeMillis() > timeToLive;
	}





}
