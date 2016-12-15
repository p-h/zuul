package org.hurlimann.zuul;

import java.util.Scanner;

/**
 * This class is part of the "World of Zuul" application. "World of Zuul" is a
 * very simple, text based adventure game.
 * <p>
 * This parser reads user input and tries to interpret it as an "Adventure"
 * command. Every time it is called it reads a line from the terminal and tries
 * to interpret the line as a two-word command. It returns the command as an
 * object of class Command.
 * <p>
 * The parser has a set of known command words. It checks user input against the
 * known commands, and if the input is not one of the known commands, it returns
 * a command object that is marked as an unknown command.
 *
 * @author Michael Kölling and David J. Barnes
 * @version 2011.08.10
 */
class Parser {
	private final String input;

	/**
	 * Create a parser to read from the terminal window.
	 *
	 * @param input the string that the user entered
	 */
	public Parser(String input) {
		this.input = input;
	}

	/**
	 * @return The next command from the user null if there is none
	 */
	public Command getCommand() {
		String word1 = null;
		String word2 = null;
		String rest = null;

		// Find up to two words on the line.
		try (Scanner tokenizer = new Scanner(input)) {
			if (tokenizer.hasNext()) {
				word1 = tokenizer.next(); // get first word
				if (tokenizer.hasNext()) {
					word2 = tokenizer.next(); // get second word
					rest = tokenizer.nextLine();
				}
			}
		}

		return new Command(CommandWords.getCommandWord(word1), word2, rest);
	}
}
