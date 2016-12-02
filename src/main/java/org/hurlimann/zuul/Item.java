package org.hurlimann.zuul;

/**
 * Class representing an item.
 * An Item makes a Player stronger
 */
public class Item implements HasStats {
	private final String name;
	private final int attack;
	private final int defense;
	private final int agility;

	public Item(String name, int attack, int defense, int agility) {
		this.name = name;
		this.attack = attack;
		this.defense = defense;
		this.agility = agility;
	}

	public String getName() {
		return name;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getAttack() {
		return attack;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getDefense() {
		return defense;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getAgility() {
		return agility;
	}
}