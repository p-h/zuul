package org.hurlimann.zuul;

/**
 * Class representing an item.
 * An Item makes a Player stronger
 */
public class Item implements HasStats {
	private final int id;
	private final String name;
	private final int attack;
	private final int defense;
	private final int agility;

	public Item(int id, String name, int attack, int defense, int agility) {
		this.id = id;
		this.name = name;
		this.attack = attack;
		this.defense = defense;
		this.agility = agility;
	}

	public int getId() {
		return id;
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

	@Override
	public String toString() {
		return String.format(
				"Item{id='%d', name='%s', attack='%d', defense='%d', agility='%d'}",
				id, name, attack, defense, agility);
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		final Item item = (Item) o;

		if (getAttack() != item.getAttack()) return false;
		if (getDefense() != item.getDefense()) return false;
		if (getAgility() != item.getAgility()) return false;
		if (getId() != item.getId()) return false;
		return getName().equals(item.getName());
	}

	@Override
	public int hashCode() {
		int result = getName().hashCode();
		result = 31 * result + getAttack();
		result = 31 * result + getDefense();
		result = 31 * result + getAgility();
		result = 31 * result + getId();
		return result;
	}
}
