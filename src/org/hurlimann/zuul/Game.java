package org.hurlimann.zuul;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

/**
 * This class is the main class of the "World of Zuul" application. "World of
 * Zuul" is a very simple, network-text based adventure game. Users can walk around some
 * scenery. That's all. It should really be extended to make it more
 * interesting!
 * <p>
 * To play this game, create an instance of this class, call the "initialize" and the "play"
 * method.
 *
 * @author Philippe Hürlimann, Michael Kölling and David J. Barnes
 * @version 2016.10.27
 */

public class Game {
	private static Room startingRoom;
	private ArrayList<Player> players;
	private ServerSocketChannel serverSocketChannel;

	/**
	 * Create the game and initialise its internal map.
	 */
	public Game() {
		createRooms();
	}

	public void initialize() throws IOException {
		players = new ArrayList<>();

		serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.configureBlocking(false);
		serverSocketChannel.socket().bind(new InetSocketAddress(InetAddress.getLocalHost(), 37888));
	}

	/**
	 * Create all the rooms and link their exits together.
	 */
	private void createRooms() {
		Room outside, theater, pub, lab, office;

		// create the rooms
		outside = new Room("outside the main entrance of the university");
		theater = new Room("in a lecture theater");
		pub = new Room("in the campus pub");
		lab = new Room("in a computing lab");
		office = new Room("in the computing admin office");

		// initialise room exits
		outside.setExit("east", theater);
		outside.setExit("south", lab);
		outside.setExit("west", pub);

		theater.setExit("west", outside);

		pub.setExit("east", outside);

		lab.setExit("north", outside);
		lab.setExit("east", office);

		office.setExit("west", lab);

		startingRoom = outside; // start game outside
	}

	/**
	 * Main play routine. Loops until end of play.
	 */
	public void play() throws IOException {

		while (true) {
			handleNewConnections();


			for (Player player : players) {
				player.HandleInput();
			}
		}
	}

	private void handleNewConnections() throws IOException {
		SocketChannel newPlayerSocketChannel;
		while ((newPlayerSocketChannel = serverSocketChannel.accept()) != null) {

			final Socket newPlayerSocket = newPlayerSocketChannel.socket();
			// TODO: Implement username
			final Player newPlayer = new Player("user123", startingRoom, newPlayerSocket);
			players.add(newPlayer);

			newPlayer.printWelcome();
		}
	}
}
