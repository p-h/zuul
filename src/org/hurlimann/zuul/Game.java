package org.hurlimann.zuul;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * This class is the main class of the "World of Zuul" application. "World of
 * Zuul" is a very simple, text based adventure game. Users can walk around some
 * scenery. That's all. It should really be extended to make it more
 * interesting!
 * <p>
 * To play this game, create an instance of this class and call the "play"
 * method.
 * <p>
 * This main class creates and initialises all the others: it creates all rooms,
 * creates the parser and starts the game. It also evaluates and executes the
 * commands that the parser returns.
 *
 * @author Michael Kölling and David J. Barnes
 * @version 2011.08.10
 */

public class Game {
	private Room startingRoom;
	private ArrayList<Player> players;

	/**
	 * Create the game and initialise its internal map.
	 */
	public Game() {
		createRooms();
	}

	public void Initialize() {
		Socket socket;
		try (ServerSocket serverSocket = new ServerSocket(37888)) {
			socket = serverSocket.accept();

			// TODO: Implement username
			players.add(new Player("user123", startingRoom, socket));

		} catch (IOException e) {
			System.err.println(
					"Unable to establish network connection. Are you sure you are allowed to listen on this port?");
		}
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

			handlePlayerInput();
		}
	}

	private void handleNewConnections() {
		printWelcome();
	}

	private void handlePlayerInput() throws IOException {
		for (Player player : players) {
			PrintStream outStream = new PrintStream(player.getSocket().getOutputStream());
			InputStream inStream = player.getSocket().getInputStream();
			Parser parser = new Parser(inStream);
			outStream.print("> ");

			Command command = parser.getCommand();
			processCommand(command);
		}
	}

	/**
	 * Print out the opening message for the player.
	 */
	private void printWelcome() {
		outStream.println();
		outStream.println("Welcome to the World of Zuul!");
		outStream.println("World of Zuul is a new, incredibly boring adventure game.");
		outStream.println("Type '" + CommandWord.HELP + "' if you need help.");
		outStream.println();
		outStream.println(startingRoom.getLongDescription());
	}

	/**
	 * Given a command, process (that is: execute) the command.
	 *
	 * @param command The command to be processed.
	 * @return true If the command ends the game, false otherwise.
	 */
	private boolean processCommand(Command command) {
		boolean wantToQuit = false;

		CommandWord commandWord = command.getCommandWord();

		switch (commandWord) {
			case UNKNOWN:
				outStream.println("I don't know what you mean...");
				break;

			case HELP:
				printHelp();
				break;

			case GO:
				goRoom(command);
				break;

			case QUIT:
				wantToQuit = quit(command);
				break;
		}
		return wantToQuit;
	}

	// implementations of user commands:

	/**
	 * Print out some help information. Here we print some stupid, cryptic
	 * message and a list of the command words.
	 */
	private void printHelp() {
		outStream.println("You are lost. You are alone. You wander");
		outStream.println("around at the university.");
		outStream.println();
		outStream.println("Your command words are:");
		outStream.println(parser.getCommandsString());
	}

	/**
	 * Try to go in one direction. If there is an exit, enter the new room,
	 * otherwise print an error message.
	 */
	private void goRoom(Command command) {
		if (!command.hasSecondWord()) {
			// if there is no second word, we don't know where to go...
			outStream.println("Go where?");
			return;
		}

		String direction = command.getSecondWord();

		// Try to leave current room.
		Room nextRoom = startingRoom.getExit(direction);

		if (nextRoom == null) {
			outStream.println("There is no door!");
		} else {
			startingRoom = nextRoom;
			outStream.println(startingRoom.getLongDescription());
		}
	}

	/**
	 * "Quit" was entered. Check the rest of the command to see whether we
	 * really quit the game.
	 *
	 * @return true, if this command quits the game, false otherwise.
	 */
	private boolean quit(Command command) {
		if (command.hasSecondWord()) {
			outStream.println("Quit what?");
			return false;
		} else {
			return true; // signal that we want to quit
		}
	}
}
