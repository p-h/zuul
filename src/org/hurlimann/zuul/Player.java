package org.hurlimann.zuul;

import java.net.Socket;

/**
 * Class representing players in the game
 */
public class Player {
	private String name;
	private Room room;
	private Socket socket;

	public Player(String  name, Room room, Socket socket) {
		setName(name);
		setRoom(room);
		this.socket = socket;
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
