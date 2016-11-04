package org.hurlimann.zuul;

import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

/**
 * Class representing players in the game
 */
public class Player {
	private String name;
	private Room room;
	private SocketChannel socketChannel;

	public Player(String name, Room room, SocketChannel socketChannel) throws IOException {
		setName(name);
		setRoom(room);
		this.socketChannel = socketChannel;
	}

	private void writeToSocketChannel(String message) throws IOException {
		CharsetEncoder enc = Charset.defaultCharset().newEncoder();
		socketChannel.write(enc.encode(CharBuffer.wrap(message + "\n")));
	}

	private void writeToSocketChannel() throws IOException {
		writeToSocketChannel("");
	}

	/**
	 * Print out the opening message for the player.
	 */
	public void printWelcome() throws IOException {
		writeToSocketChannel();
		writeToSocketChannel("Welcome to the World of Zuul!");
		writeToSocketChannel("World of Zuul is a new, incredibly boring adventure game.");
		writeToSocketChannel("Type '" + CommandWord.HELP + "' if you need help.");
		writeToSocketChannel();
		writeToSocketChannel(room.getLongDescription());
	}

	/**
	 * Given a command, process (that is: execute) the command.
	 *
	 * @param command The command to be processed.
	 * @return true If the command ends the game, false otherwise.
	 */
	private boolean processCommand(Command command) throws IOException {
		boolean wantToQuit;
		wantToQuit = false;

		CommandWord commandWord = command.getCommandWord();

		switch (commandWord) {
			case UNKNOWN:
				writeToSocketChannel("I don't know what you mean...");
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

	/**
	 * Print out some help information. Here we print some stupid, cryptic
	 * message and a list of the command words.
	 */
	private void printHelp() throws IOException {
		writeToSocketChannel("You are lost. You are alone. You wander");
		writeToSocketChannel("around at the university.");
		writeToSocketChannel();
		writeToSocketChannel("Your command words are:");
		writeToSocketChannel(CommandWords.getCommandsString());
	}

	/**
	 * Try to go in one direction. If there is an exit, enter the new room,
	 * otherwise print an error message.
	 */
	private void goRoom(Command command) throws IOException {
		if (!command.hasSecondWord()) {
			// if there is no second word, we don't know where to go...
			writeToSocketChannel("Go where?");
			return;
		}

		String direction = command.getSecondWord();

		// Try to leave current room.
		Room nextRoom = room.getExit(direction);

		if (nextRoom == null) {
			writeToSocketChannel("There is no door!");
		} else {
			room = nextRoom;
			writeToSocketChannel(room.getLongDescription());
		}
	}

	/**
	 * "Quit" was entered. Check the rest of the command to see whether we
	 * really quit the game.
	 *
	 * @return true, if this command quits the game, false otherwise.
	 */
	private boolean quit(Command command) throws IOException {
		if (command.hasSecondWord()) {
			writeToSocketChannel("Quit what?");
			return false;
		} else {
			return true; // signal that we want to quit
		}
	}

	public boolean handleInput(final String input) throws IOException {
		Parser parser = new Parser(input);
		writeToSocketChannel("> ");

		Command command = parser.getCommand();
		return processCommand(command);
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
}
