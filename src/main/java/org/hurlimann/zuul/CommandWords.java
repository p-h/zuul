package org.hurlimann.zuul;

import java.util.HashMap;

/**
 * This class is part of the "World of Zuul" application. "World of Zuul" is a
 * very simple, text based adventure game.
 * <p>
 * This class holds an enumeration of all command words known to the game. It is
 * used to recognise commands as they are typed in.
 *
 * @author Michael Kölling and David J. Barnes
 * @version 2011.08.10
 */

class CommandWords {
	// A mapping between a command word and the CommandWord
	// associated with it.
	private static final HashMap<String, CommandWord> validCommands;

	private CommandWords() {
	}

	static {
		validCommands = new HashMap<>();
		for (CommandWord command : CommandWord.values()) {
			if (command != CommandWord.UNKNOWN) {
				validCommands.put(command.toString(), command);
			}
		}
	}

	/**
	 * Find the CommandWord associated with a command word.
	 *
	 * @param commandWord The word to look up.
	 * @return The CommandWord corresponding to commandWord, or UNKNOWN if it is
	 * not a valid command word.
	 */
	public static CommandWord getCommandWord(String commandWord) {
		CommandWord command = validCommands.get(commandWord);
		if (command != null) {
			return command;
		} else {
			return CommandWord.UNKNOWN;
		}
	}

	/**
	 * Check whether a given String is a valid command word.
	 *
	 * @return true if it is, false if it isn't.
	 */
	public static boolean isCommand(String aString) {
		return validCommands.containsKey(aString);
	}

	/**
	 * @return all valid commands as a space separated string.
	 */
	public static String getCommandsString() {
		return validCommands.keySet().stream().reduce("", (head, tail) -> head + " " + tail).trim();
	}
}
