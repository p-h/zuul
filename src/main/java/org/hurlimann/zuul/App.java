package org.hurlimann.zuul;

class App {

	public static void main(String[] args) {
		Game myGame = new Game();
		try {
			myGame.initialize();
			myGame.play();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
