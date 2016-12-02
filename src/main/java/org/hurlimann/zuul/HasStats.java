package org.hurlimann.zuul;

/**
 * Interface for every class which has stats to abstract the handling those.
 */
public interface HasStats {
	/**
	 * @return attack
	 */
	public int getAttack();

	/**
	 * @return defense
	 */
	public int getDefense();

	/**
	 * @return defense
	 */
	public int getAgility();
}
