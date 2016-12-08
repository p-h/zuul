package org.hurlimann.zuul;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class Room - a room in an adventure game.
 * <p>
 * This class is part of the "World of Zuul" application. "World of Zuul" is a
 * very simple, text based adventure game.
 * <p>
 * A "Room" represents one location in the scenery of the game. It is connected
 * to other rooms via exits. For each existing exit, the room stores a reference
 * to the neighboring room.
 *
 * @author Michael Kölling and David J. Barnes
 * @version 2011.08.10
 */

public class Room {
	private final String description;
	private final Map<Direction, Room> exits;
	private List<Item> items;

	/**
	 * Create a room described "description". Initially, it has no exits.
	 * "description" is something like "a kitchen" or "an open court yard".
	 *
	 * @param description The room's description.
	 */
	public Room(String description) {
		this.description = description;
		exits = new HashMap<>();
		items = new ArrayList<>();
	}

	/**
	 * Define an exit from this room.
	 *
	 * @param direction The direction of the exit.
	 * @param neighbor  The room to which the exit leads.
	 */
	public void setExit(Direction direction, Room neighbor) {
		exits.put(direction, neighbor);
	}

	/**
	 * @return The short description of the room (the one that was defined in
	 * the constructor).
	 */
	public String getShortDescription() {
		return description;
	}

	/**
	 * Return a description of the room in the form: You are in the kitchen.
	 * Exits: north west
	 *
	 * @return A long description of this room
	 */
	public String getLongDescription() {
		return "You are " + description + ".\n" + getExitString();
	}

	/**
	 * Return a string describing the room's exits, for example "Exits: north
	 * west".
	 *
	 * @return Details of the room's exits.
	 */
	private String getExitString() {
		return Stream.concat(Stream.of("Exits:"), exits.keySet().stream()
				.map(Direction::toString))
				.collect(Collectors.joining(" "));
	}

	/**
	 * Return the room that is reached if we go from this room in direction
	 * "direction". If there is no room in that direction, return null.
	 *
	 * @param direction The exit's direction.
	 * @return The room in the given direction.
	 */
	public Room getExit(Direction direction) {
		return exits.get(direction);
	}

	public List<Item> getContents() {
		return Collections.unmodifiableList(items);
	}
}
