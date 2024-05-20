package org.hazelcast;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class GeoDistributedLRUCache<K extends Comparable<K>, V> {
	
	private static HazelcastInstance hazelcastInstance;

	private final IMap<K, CacheItem<V>> map;

	private final FixedSizeLinkedHashMap<K, CacheItem<V>> fixedSizeLinkedHashMap;
	private int capacity;
	private int size  = 0;
	GeoDistributedLRUCache(int capacity, String mapName) {
		hazelcastInstance = Hazelcast.newHazelcastInstance(new Config());
		map = hazelcastInstance.getMap(mapName);
		this.setCapacity(capacity);
		fixedSizeLinkedHashMap = new FixedSizeLinkedHashMap<>(this.getCapacity());
		size++;
	}

	public static HazelcastInstance getHazelcastInstance() {
		return hazelcastInstance;
	}

	public FixedSizeLinkedHashMap<K, CacheItem<V>> getFixedSizeLinkedHashMap() {
		return fixedSizeLinkedHashMap;
	}


	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int newCapacity){
		capacity = newCapacity;
	}

	public void put(K key, V value, Long ttl) {

		CacheItem<V> item = new CacheItem<>(value, ttl);
		fixedSizeLinkedHashMap.put(key,item);
		syncFixedLinkedHashMapToImap(map);

	}

	public void putItem(K key, CacheItem<V> item) {

		fixedSizeLinkedHashMap.put(key,item);
		syncFixedLinkedHashMapToImap(map);

	}
	
	public V get(K key) {
		// we query the item we want to get from the hashmap  and stock it in item var
		var item = fixedSizeLinkedHashMap.get(key);
		// then we remove from the hashmap
		fixedSizeLinkedHashMap.remove(key);
		// then we add it back for it to be added at the front of the hashmap
		fixedSizeLinkedHashMap.put(key, item);
		// and finally we put all the values in the hashmap into the cache
		syncFixedLinkedHashMapToImap(map);
		// if cache is empty we call this to set again its size to one to reverse the fixedSizeLinkedHashMap
		setSize(1);
		// and return the value of the front of the cache, which is the new item added
		return map.get(key).getValue();
	}

	private void syncFixedLinkedHashMapToImap(IMap<K,CacheItem<V>> map){
		// Sync LinkedHashMap with distributed IMap
		try {
			// know that these values won't be added in order
			// because it's the internal behavior
			map.putAll(fixedSizeLinkedHashMap);

		}catch (Exception ignored){

		}
	}

	private FixedSizeLinkedHashMap<K, CacheItem<V>> reverseLinkedHashMap(FixedSizeLinkedHashMap<K, CacheItem<V>> fixedSizeLinkedHashMap) {
		// Get entries from the original map
		List<Map.Entry<K, CacheItem<V>>> entryList = new ArrayList<>(fixedSizeLinkedHashMap.entrySet());
		// Reverse the list
		if (getSize() == 1) {
			Collections.reverse(entryList);
			setSize(2);
		}
		fixedSizeLinkedHashMap.clear();
		// Put the reversed entries into the new map
		for (Map.Entry<K, CacheItem<V>> entry : entryList) {
			//cache.put(entry.getKey(), entry.getValue());
			fixedSizeLinkedHashMap.put(entry.getKey(), entry.getValue());
		}

		return fixedSizeLinkedHashMap;


	}
	
	public String printCache() {


		// Retrieve entries and print in insertion order
		StringBuilder str = new StringBuilder();
		var save = new FixedSizeLinkedHashMap<K, CacheItem<V>>(getCapacity());
		save = reverseLinkedHashMap(fixedSizeLinkedHashMap);


		// this is to set the fixedLinkedHashMap with the values of the cache
		// because when we run an instance of the hazelcast we lose all his values
		if(fixedSizeLinkedHashMap.getMaxSize() == 0) {

			fixedSizeLinkedHashMap.setMaxSize(map.size());
			List<K> entryList = new ArrayList<>(map.keySet());
			Collections.sort(entryList);
			Collections.reverse(entryList);

			for (K k : entryList) {
				this.putItem(k, fixedSizeLinkedHashMap.get(k));
				fixedSizeLinkedHashMap.put(k, map.get(k));
			}
			save = getFixedSizeLinkedHashMap();
		}

		//  evict Expired items before printing them
		evictExpiredItems();

		for (Map.Entry<K, CacheItem<V>> item : save.entrySet()) {

			str.append(item.getKey()).append(" -> ").append(item.getValue());
			str.append("\n");
		}

		 return str.toString();
	}

	private void evictExpiredItems() {
		// delete all the item the ttl is reached
		for (Map.Entry<K, CacheItem<V>> entry : map.entrySet()) {
			if (entry.getValue().isExpired()) {
				System.out.println("item with key "+entry.getKey()+" is Expired");
				fixedSizeLinkedHashMap.remove(entry.getKey());
				map.evict(entry.getKey());
			}
			if (map.size() == 0){
				System.out.println("cache is empty");
				// if cache is empty we call this to set again its size to one to reverse the fixedSizeLinkedHashMap
				setSize(1);
			}
		}


	}

}
