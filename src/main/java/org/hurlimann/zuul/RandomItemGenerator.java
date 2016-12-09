package org.hurlimann.zuul;

import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

class RandomItemGenerator {
	private RandomItemGenerator() {
	}

	private static int nextItemId = 1;

	private static final String[] itemNamePrefixes = {
			"Cape",
			"Sword",
			"Wand",
			"Cuirass",
			"Axe",
	};
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
