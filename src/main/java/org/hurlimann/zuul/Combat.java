package org.hurlimann.zuul;

/**
 * Class representing a combat taking place between two players
 * a combat can only occur between two players
 * players which are engaged in combat cannot switch rooms, pick up items or interact with other players
 */
class Combat {
	private Player player1;
	private Player player2;

	public Combat(Player player1, Player player2) {
		this.player1 = player1;
		this.player2 = player2;
	}

	public Player getPlayer1() {
		return player1;
	}

	public Player getPlayer2() {
		return player2;
	}

	public Player[] getPlayers() {
		return new Player[]{player1, player2};
	}
}
