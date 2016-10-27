package org.hurlimann.zuul;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;

/**
 * Class representing players in the game
 */
public class Player {
	private String name;
	private Room room;
	private Socket socket;
	private Parser parser;

	public Player(String  name, Room room, Socket socket) throws IOException {
		setName(name);
		setRoom(room);
		this.socket = socket;

		parser = new Parser(socket.getInputStream());
	}

	/**
	 * Print out the opening message for the player.
	 */
	public void printWelcome() throws IOException {
		try (PrintStream outStream = new PrintStream(socket.getOutputStream())) {
			outStream.println();
			outStream.println("Welcome to the World of Zuul!");
			outStream.println("World of Zuul is a new, incredibly boring adventure game.");
			outStream.println("Type '" + CommandWord.HELP + "' if you need help.");
			outStream.println();
			outStream.println(room.getLongDescription());
		}
	}

	/**
	 * Given a command, process (that is: execute) the command.
	 *
	 * @param command The command to be processed.
	 * @return true If the command ends the game, false otherwise.
	 */
	private boolean processCommand(Command command) throws IOException {
		boolean wantToQuit;
		try (PrintStream outStream = new PrintStream(socket.getOutputStream())) {
			wantToQuit = false;

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
		}
		return wantToQuit;
	}

	/**
	 * Print out some help information. Here we print some stupid, cryptic
	 * message and a list of the command words.
	 */
	private void printHelp() throws IOException {
		try (PrintStream outStream = new PrintStream(socket.getOutputStream())) {
			outStream.println("You are lost. You are alone. You wander");
			outStream.println("around at the university.");
			outStream.println();
			outStream.println("Your command words are:");
			outStream.println(parser.getCommandsString());
		}
	}

	/**
	 * Try to go in one direction. If there is an exit, enter the new room,
	 * otherwise print an error message.
	 */
	private void goRoom(Command command) throws IOException {
		try (PrintStream outStream = new PrintStream(socket.getOutputStream())) {
			if (!command.hasSecondWord()) {
				// if there is no second word, we don't know where to go...
				outStream.println("Go where?");
				return;
			}

			String direction = command.getSecondWord();

			// Try to leave current room.
			Room nextRoom = room.getExit(direction);

			if (nextRoom == null) {
				outStream.println("There is no door!");
			} else {
				room = nextRoom;
				outStream.println(room.getLongDescription());
			}
		}
	}

	/**
	 * "Quit" was entered. Check the rest of the command to see whether we
	 * really quit the game.
	 *
	 * @return true, if this command quits the game, false otherwise.
	 */
	private boolean quit(Command command) throws IOException {
		try (PrintStream outStream = new PrintStream(socket.getOutputStream())) {
			if (command.hasSecondWord()) {
				outStream.println("Quit what?");
				return false;
			} else {
				return true; // signal that we want to quit
			}
		}
	}

	public void HandleInput() throws IOException {
		PrintStream outStream = new PrintStream(getSocket().getOutputStream());
		InputStream inStream = getSocket().getInputStream();
		Parser parser = new Parser(inStream);
		outStream.print("> ");

		Command command = parser.getCommand();
		processCommand(command);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}

	public Socket getSocket() {
		return socket;
	}
}
