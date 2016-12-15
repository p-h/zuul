package org.hurlimann.zuul;

import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.ToIntFunction;

/**
 * Class representing players in the game.
 * Handles input and output with a particular player and interactions with other players
 */
public class Player implements HasStats {
	private String name;
	private Room room;
	private final SocketChannel socketChannel;

	private List<Item> items = new ArrayList<>();

	private int hitPoints = 100;
	private int baseAttack;
	private int baseDefense;
	private int baseAgility;
	private boolean toDelete = false;

	public Player(String name, Room room, SocketChannel socketChannel) {
		this.name = name;
		this.room = room;
		this.socketChannel = socketChannel;
	}

	/**
	 * Sends a message to a particular player
	 *
	 * @param message to send to the player
	 * @throws IOException
	 */
	private void writeToSocketChannel(String message) throws IOException {
		CharsetEncoder enc = Charset.defaultCharset().newEncoder();
		socketChannel.write(enc.encode(CharBuffer.wrap(message + "\n")));
	}

	/**
	 * Sends a newline to a player
	 * @throws IOException
	 */
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
				if (isInCombat()) {
					writeToSocketChannel("You can't leave. You're in combat.");
				} else {
					goRoom(command);
				}
				break;

			case QUIT:
				wantToQuit = quit(command);
				break;
			case SETNAME:
				String newName = command.getSecondWord();
				if (newName == null || newName.isEmpty()) {
					writeToSocketChannel("Please provide a name.");
				} else {
					this.name = newName;
					writeToSocketChannel("Hi " + newName + "!");
				}
				break;
			case LOOK:
				printRoomContents();
				break;
			case ATTACK:
				handleAttack(command);
				break;
			case SAY:
				String sentence = command.getSecondWord() + " " + command.getRest();
				String message = this.getName() + ": " + sentence;
				room.getPlayers().forEach(p -> p.tell(message));
				break;
			case PICKUP:
				String itemIdStr = command.getSecondWord();
				if (itemIdStr == null || itemIdStr.isEmpty()) {
					writeToSocketChannel("What item do you want to pick up?");
				} else {
					try {
						int itemId = Integer.parseInt(itemIdStr);
						Optional<Item> itemOptional = room.pickUpItem(itemId);
						if (itemOptional.isPresent()) {
							Item item = itemOptional.get();
							items.add(item);
							writeToSocketChannel("Congratulations on your brand new " + item.getName());
						} else {
							writeToSocketChannel("That item doesn't exist.");
						}
					} catch (NumberFormatException ex) {
						writeToSocketChannel("That's not a valid number");
					}
				}
				break;
		}
		return wantToQuit;
	}

	/**
	 * Sends a message to a player
	 * use this method if you don't mind if it fails.
	 * @param message
	 */
	private void tell(String message) {
		try {
			writeToSocketChannel(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Handles an attack on another player.
	 * Either triggers an attack if a player is already in combat or creates a new combat.
	 * @param command
	 * @throws IOException
	 */
	private void handleAttack(Command command) throws IOException {
		if (isInCombat()) {
			Optional<Player> otherPlayerOptional = room.getCombatingPlayer(this);
			if (otherPlayerOptional.isPresent()) {
				Player otherPlayer = otherPlayerOptional.get();
				otherPlayer.takeHit(this.getEffectiveAttack());
				int otherPlayerHP = otherPlayer.getHitPoints();
				writeToSocketChannel("Hit " + otherPlayer.getName() + ".\n" +
						otherPlayerHP + " HP remaining!");
			}
		} else {
			initiateCombat(command);
		}
	}

	/**
	 * Creates a new combat with another player
	 * @param command
	 * @throws IOException
	 */
	private void initiateCombat(Command command) throws IOException {
		String otherPlayerName = command.getSecondWord();
		if (otherPlayerName == null || otherPlayerName.isEmpty()) {
			writeToSocketChannel("Attack who?");
		} else {
			Optional<Player> optionalPlayerToAttack = room.getPlayers()
					.stream()
					.filter(p -> p.getName().equals(otherPlayerName))
					.findFirst();
			if (optionalPlayerToAttack.isPresent()) {
				Player playerToAttack = optionalPlayerToAttack.get();
				room.addCombat(new Combat(this, playerToAttack));
				writeToSocketChannel("Successfully attacked " + otherPlayerName + "!");
				playerToAttack.tell("You've been attacked by " + this.getName());
			} else {
				writeToSocketChannel("Specified player doesn't exist.");
			}
		}
	}

	/**
	 * @return wheter or not a player is in combat
	 */
	public boolean isInCombat() {
		return room.getPlayersInCombat().stream().anyMatch(this::equals);
	}

	/**
	 * Print out what items and players are in the room
	 */
	private void printRoomContents() throws IOException {
		writeToSocketChannel("These are the contents of the room.");
		writeToSocketChannel("Players:");
		for (Player player : room.getPlayers()) {
			writeToSocketChannel(player.toString());
		}
		writeToSocketChannel();
		writeToSocketChannel("Items:");
		for (Item item : room.getItems()) {
			writeToSocketChannel(item.toString());
		}
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

		Direction direction = Direction.findDirection(command.getSecondWord());

		// Try to leave current room.
		Room nextRoom = room.getExit(direction);

		if (nextRoom == null) {
			writeToSocketChannel("There is no door!");
		} else {
			room.removePlayer(this);
			nextRoom.addPlayer(this);
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
		Command command = parser.getCommand();
		return processCommand(command);
	}

	public String getName() {
		return name;
	}

	public Room getRoom() {
		return room;
	}

	public int getHitPoints() {
		return hitPoints;
	}

	/**
	 * Calculates and applies damage taken from an attack
	 * inspired by Dota 2 (http://dota2.gamepedia.com/Armor#Damage_multiplier)
	 *
	 * @param attack of the enemy attacking
	 */
	void takeHit(int attack) {
		int defense = getEffectiveDefense();
		double damageMultiplier = (1 - 0.06 * defense / 1 + 0.06 * Math.abs(defense));
		int effectiveDamage = (int) (attack * damageMultiplier);
		hitPoints -= effectiveDamage;

		if (hitPoints <= 0) {
			this.toDelete = true;
		}
	}

	/**
	 * @param getter stat of the Player and item that needs to be calculated
	 * @return calculated stat
	 */
	private int getStat(ToIntFunction<HasStats> getter) {
		return items.stream()
				.mapToInt(getter)
				.reduce(getter.applyAsInt(this), (acc, x) -> acc + x);
	}

	/**
	 * @return attack accounting for items the player owns
	 */
	public int getEffectiveAttack() {
		return getStat(HasStats::getAttack);
	}

	/**
	 * @return defense accounting for items the player owns
	 */
	public int getEffectiveDefense() {
		return getStat(HasStats::getDefense);
	}

	/**
	 * @return agility accounting for items the player owns
	 */
	public int getEffectiveAgility() {
		return getStat(HasStats::getAgility);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getAttack() {
		return baseAttack;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getDefense() {
		return baseDefense;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getAgility() {
		return baseAgility;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Player player = (Player) o;

		if (baseAttack != player.baseAttack) return false;
		if (baseDefense != player.baseDefense) return false;
		if (baseAgility != player.baseAgility) return false;
		if (name != null ? !name.equals(player.name) : player.name != null) return false;
		if (room != null ? !room.equals(player.room) : player.room != null) return false;
		if (socketChannel != null ? !socketChannel.equals(player.socketChannel) : player.socketChannel != null)
			return false;
		return items != null ? items.equals(player.items) : player.items == null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		int result = name != null ? name.hashCode() : 0;
		result = 31 * result + (room != null ? room.hashCode() : 0);
		result = 31 * result + (socketChannel != null ? socketChannel.hashCode() : 0);
		result = 31 * result + (items != null ? items.hashCode() : 0);
		result = 31 * result + baseAttack;
		result = 31 * result + baseDefense;
		result = 31 * result + baseAgility;
		return result;
	}

	@Override
	public String toString() {
		return "Player{" +
				"name='" + name + '\'' +
				", Attack=" + getEffectiveAttack() +
				", Defense=" + getEffectiveDefense() +
				", Agility=" + getEffectiveAgility() +
				", hitPoints=" + getHitPoints() +
				'}';
	}

	public boolean isToDelete() {
		return toDelete;
	}
}
