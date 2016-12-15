package org.hurlimann.zuul;

import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

/**
 * Utility to create random items with random stats.
 * The type of the item doesn't have any effects on the stats of the item. They are completely random.
 */
class RandomItemGenerator {
	private RandomItemGenerator() {
	}

	private static int nextItemId = 1;

	/**
	 * The different item types that can be generated.
	 */
	private static final String[] itemNamePrefixes = {
			"Cape",
			"Sword",
			"Wand",
			"Cuirass",
			"Axe",
	};

	/**
	 * Typical fantasy create from which an item could come from */
	private final static String[] itemNameSuffixes = {
			"Assassin",
			"Dragon",
			"Faun",
			"Giant",
			"Goblin",
			"Goblin",
			"Lion",
			"Magician",
			"Orc",
			"Rat",
			"Troll",
			"Turtle",
			"Warrior",
			"Werewolf",
			"Wyvern",
			"Shiba Inu",
	};
	private static final String[] itemNames =
			Stream.of(itemNamePrefixes)
					.flatMap(s -> Stream.of(itemNameSuffixes)
							.map(p -> String.format("%s of the %s", s, p)))
					.toArray(String[]::new);

	/**
	 * Generates an item with completely random stats.
	 * @return generated item
	 */
	static Item generate() {
		ThreadLocalRandom random = ThreadLocalRandom.current();
		int nameIndex = random.nextInt(0, itemNames.length);
		String name = itemNames[nameIndex];
		int attack = random.nextInt(10, 100);
		int defense = random.nextInt(10, 100);
		int agility = random.nextInt(10, 100);
		return new Item(nextItemId++, name, attack, defense, agility);
	}
}
