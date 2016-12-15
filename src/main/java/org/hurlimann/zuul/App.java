package org.hurlimann.zuul;

/**
 * Main class of the app.
 */
class App {

	/**
	 * Starting point of the app.
	 */
	public static void main(String[] args) {
		try {
			Game myGame = new Game();
			myGame.play();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
