package org.hazelcast;

import com.hazelcast.cluster.Cluster;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Main {


	public static void main(String[] args) {

		Scanner scan, scanOne;
		String quit = "";
		GeoDistributedLRUCache<String, String> cache = new GeoDistributedLRUCache<>(0, "cache");
		// Get the cluster object
		Cluster cluster = GeoDistributedLRUCache.getHazelcastInstance().getCluster();
		// Get the set of members and size
		int clusterSize = cluster.getMembers().size();

		do {

			System.out.println("|-----------Menu-----------|\n");
			System.out.println("1-)setting cache size, put, get... \n2-)quit");
			scan = new Scanner(System.in);
			String choice = scan.nextLine();

			switch (choice) {
				case "1" -> {

					System.out.println("Option 1");
					String choiceOne;
					if (clusterSize <= 1) {

						System.out.println("\t1-)Enter cache size (e.g: 3)");
						scanOne = new Scanner(System.in);
						choiceOne = scanOne.nextLine();
						cache.setCapacity(Integer.parseInt(choiceOne));
						cache.getFixedSizeLinkedHashMap().setMaxSize(Integer.parseInt(choiceOne));
					}

					do {
						System.out.println("1-)put\n2-)get\n3-)show cache\n4-)quit");
						scanOne = new Scanner(System.in);
						choiceOne = scanOne.nextLine();
						var splitOne = choiceOne.split("");

						switch (splitOne[0]) {
							case "1" -> {

								//cache.setCache(GeoDistributedLRUCache.getHazelcastInstance().getMap("cache"));
								System.out.println("Option 2");
								System.out.println("\t1-)Enter key - Enter value - Enter expirationTime (e.g: 1 one 5)");
								scanOne = new Scanner(System.in);
								choiceOne = scanOne.nextLine();
								String[] split = choiceOne.split(" ");

								cache.put(split[0], split[1], System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(Integer.parseInt(split[2])));
								// to change the size of fixedSizeLinkedHashMap when the user
								// add only item and move to the other instance
							}
							case "2" -> {

								System.out.println("Option 2");
								System.out.println("\t1-)Enter the key which you want the value (e.g: 2)");
								scanOne = new Scanner(System.in);
								choiceOne = scanOne.nextLine();

								// works only if key is integer and value is string
								System.out.println("getting the item with key : " + choiceOne);

								try {

									String value = String.valueOf(cache.get(choiceOne));
									System.out.println(choiceOne + " -> " + value);
									System.out.println();
									System.out.println("\tPrinting cache...");
									System.out.println(cache.printCache());


								} catch (Exception e) {
									System.out.println("key does not exist");
								}

							}
							case "3" -> {
								System.out.println("Option 3");
								System.out.println("\tPrinting cache...");
								System.out.println(cache.printCache());
							}
							case "4" -> {
								quit = "quit";
								System.out.println("quit menu 1");
							}
							default -> {
								System.out.println("make a choice");
							}
						}
					} while (!quit.equals("quit"));

				}
				case "2" -> {

					quit = "quit";
					System.out.println("Astaluego");
				}
				default -> {
					System.out.println("make a choice");
				}
			}
			}
			while (!quit.equals("quit")) ;

			scan.close();
		}
}