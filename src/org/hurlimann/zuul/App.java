package org.hurlimann.zuul;

import java.io.IOException;

public class App {

	public static void main(String[] args) {
		Game myGame = new Game();
		try {
			myGame.initialize();
			myGame.play();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
