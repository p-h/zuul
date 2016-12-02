package org.hurlimann.zuul;

/**
 * Interface for every class which has stats to abstract the handling those.
 */
interface HasStats {
	/**
	 * @return attack
	 */
	int getAttack();

	/**
	 * @return defense
	 */
	int getDefense();

	/**
	 * @return defense
	 */
	int getAgility();
}
