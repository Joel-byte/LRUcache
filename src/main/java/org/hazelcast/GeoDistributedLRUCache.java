package org.hazelcast;

import com.hazelcast.config.Config;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import java.util.*;

public class GeoDistributedLRUCache<K extends Comparable<K>, V> {

	private static HazelcastInstance hazelcastInstance;
	private final IMap<K, CacheItem<K,V>> map;
	private static int size  = 0;
	private CacheItem<K,V> head;
	private CacheItem<K,V> tail;
	LRUCacheListener<K, V> listener;
	GeoDistributedLRUCache(String mapName) {
		hazelcastInstance = Hazelcast.newHazelcastInstance(new Config());
		map = hazelcastInstance.getMap(mapName);
		listener = new LRUCacheListener<>();
		map.addEntryListener(listener, true);
	}

	public static HazelcastInstance getHazelcastInstance() {
		return hazelcastInstance;
	}

	public static void setSize(int newSize) {
		size = newSize;
	}
	public static int getSize() { return size; }
	public void put(K key, V value, Long expirationTime) {

		CacheItem<K,V> item = new CacheItem<>(key, value, expirationTime);
		// if capacity is reached or key already exist
		// the reason why I put the -1 is, because the size of the map is updated
		// only after leaving this put method
		if (map.size() == getSize()) {
			if(map.containsKey(key)) {
				// we move the item to the front of the linkedList
				moveItemToTheFrontOfTheLinkedList(item);
			}else {
				// we remove the last item from the linkedList and (get it back, used to)
				removeTheLastItemAddedToTheLinkedList();
				// we then add the new item to the front of the linkedList
				addItemToTheFrontOfTheLinkedList(item);
			}
			map.remove(key);
			map.put(key,  item);
		}
		// if not and key already exist
		else {
			// add only to the linkedList if only cache doesn't contain key
			if(!map.containsKey(key))
				addItemToTheFrontOfTheLinkedList(item);
			// add it also to the cache
			map.put(key, item);

		}

		System.out.println("Item added successfully...\n");
	}


	public V get(K key) {

		// we query the item at the specified index with the key
		CacheItem<K,V> item = map.get(key);

		// we check if the item is not null
		if (item != null) {
			// but is expired
			if (item.isExpired()) {
				// we then remove it from the map and the doubleLinkedList
				removeAnItemFromTheLinkedListWithKey(key);
				map.remove(key);
				return null;
			}
			// and not expired
			// we add it to the front
			else {

				listener.setEvent(new EntryEvent<>("LRUCacheListener", hazelcastInstance.getCluster().getLocalMember(),
						4,item.getKey(),item.getValue()));
				// we remove it and add it back to the front
				//removeAnItemFromTheLinkedListWithKey(key);
				if(head != null){
					moveItemToTheFrontOfTheLinkedList(item);
				}
				else{
					putLinkedListToUpdateAfterAGetWithAnewInstance();
				}
				//remove the item
				// and add it back to be at the front
				map.remove(key);
				map.put(item.getKey(),  item);
				return item.getValue();

			}
		}
		return null;


	}


	private void removeAnItemFromTheLinkedListWithKey(K key) {

		// looping through the double linkedList
		while (head != null) {
			if(key.equals(head.getKey())) {
				// if it's the last item in the doubleLinkedList
				if(head.getNext() == null) {

					head = null;
					return;
				}
				// if it's the first item in the doubleLinkedList
				else if (head.getPrev() == null) {

					head = null;
					//tempHeadCacheItemTemp =  tempHead = null;
					return;
				}
				// if item in the middle
				else {

					head.getNext().setPrev(head.getPrev());
					head.getPrev().setNext(head.getNext());
					return;
				}
			}
			head = head.getNext();

		}

	}
	// removes the last item in the linkedList and return the deleted item
	private void removeTheLastItemAddedToTheLinkedList() {

		if (head == null || head.getNext() == null){
			head = null;
			return;
		}

		// looping through the double linkedList
		while (head != null) {
			// check if we reached the last item
			if (head.getNext() == null) {
				head = head.getPrev();
				head.setNext(null);
				break;
			}
			head = head.getNext();
		}
		// if we have more than 2 items (means that head was not actually),
		// we put it to the actual head
		while (head != null){
			if(head.getPrev() == null){
				break;
			}
			head = head.getPrev();
		}

	}


	private void moveItemToTheFrontOfTheLinkedList(CacheItem<K,V> item){

		if(item ==  null){
			return;
		}

		var temp = head;
		var tempS = head;

		while (tempS != null){
			if(tempS.getNext() == null){
				tail = tempS;
				break;
			}
			tempS = tempS.getNext();
		}

		while(temp != null){
			if (temp.getKey().equals(item.getKey()))
			{
				item = temp;
				break;
			}
			temp = temp.getNext();

		}
		// if item is the first element on the linkedList
		if(head == item){
			return;
		}
		// if item is at the end of the linkedList
		if(item == tail){
			tail = tail.getPrev();
			tail.setNext(null);
		}
		// if item is in the middle of the linkedList
		if (item.getPrev() != null && item.getNext() != null){
			item.getPrev().setNext(item.getNext());
			item.getNext().setPrev(item.getPrev());
		}
		item.setPrev(null);
		item.setNext(head);
		head.setPrev(item);
		head = item;

	}

	private void addItemToTheFrontOfTheLinkedList(CacheItem<K,V> item) {
		// this means that there's already an item in the doubleLinkedList
		if (head != null) {
			// we set the next item of the newItem to head, note that here head is never null
			item.setNext(head);
			// then we set the previous item to the head to new item
			head.setPrev(item);
			// and we point the head to the item for it to become head again
		}

		// when it's the first item we are adding to the list
		// we just point it to the new item
		head = item;
	}

	private void evictExpiredItems() {
		// delete all the item when the ttl is reached
		for (Map.Entry<K, CacheItem<K, V>> entry : map.entrySet()) {
			if (entry.getValue().isExpired()) {
				System.out.println("item with key "+entry.getKey()+" is Expired");
				removeAnItemFromTheLinkedListWithKey(entry.getKey());
				map.evict(entry.getKey());
			}
		}
		if (map.size() == 0){
			System.out.println("cache is empty");
			// the reason why we did this is, because the second instance linkedList stayed in memory
			// without deletion
			while (head != null){
				head = head.getNext();
				if(head == null)
					break;
				head.setPrev(null);
			}
		}
	}

	public void putLinkedListToUpdateAfterAnewInstance(){

		List<K> rankList = new ArrayList<>(map.keySet());
		Collections.sort(rankList);
		// we need to do this not to have duplicated values in the linkedList
		// when we run a new instance of the hazelcast
		// delete all the linkedList
		while (head != null){
			head = head.getNext();
			if(head == null)
				break;
			head.setPrev(null);
		}
		// setting the size in oder to add values to the linkedList
		// because in the put method we are using the size to check whether we reached the map size before putting value,
		// Size is considered as the doubleLinkedList size
		setSize(map.size());
		for (K k : rankList) {
			var item = map.get(k);
			// know that since the keys of the first map are identical to the second map when we run the program
			// they won't be added to map again
			addItemToTheFrontOfTheLinkedList(item);
		}
	}

	public void putLinkedListToUpdateAfterAGetWithAnewInstance() {
		var type = listener.getEvent().getEventType().getType();
		// so for the program not to hit all the time this condition whenever we make a get call
		// we need to verify that the member size is greater than one
		if (getHazelcastInstance().getCluster().getMembers().size() > 1) {
			// means that we first made a get call
			// 4 means updated
			if (type == 4) {
				var item = map.get(listener.getEvent().getKey());
				moveItemToTheFrontOfTheLinkedList(item);

			} else if (type == 1) {
				var key = listener.getEvent().getKey();
				var item = map.get(listener.getEvent().getKey());
				// we add only to the linkedList if the head key is not equal to itself
				if(!head.getKey().equals(key)){
					addItemToTheFrontOfTheLinkedList(item);
					removeTheLastItemAddedToTheLinkedList();
				}
			}
		}else {
			if (listener.getEvent().getEventType().getType() == 4) {
				var item = map.get(listener.getEvent().getKey());
				moveItemToTheFrontOfTheLinkedList(item);
			}
		}
	}
	public void printCache() {
		evictExpiredItems();
		//means that we run a new instance of the hazelcast
		//we now have to reorder our new linkedList
		//since the map was the only element kept in memory
		if (head == null) {
			putLinkedListToUpdateAfterAnewInstance();
		}else{
			putLinkedListToUpdateAfterAGetWithAnewInstance();

		}
		// printing linkedList
			CacheItem<K, V> tempheadCacheItem = head;
			while (tempheadCacheItem != null) {
				System.out.println(tempheadCacheItem.getKey() + " value : " + tempheadCacheItem.getValue());
				tempheadCacheItem = tempheadCacheItem.getNext();
			}
			System.out.println();
	}
}
