package org.hurlimann.zuul;

import java.net.Socket;

/**
 * Class representing players in the game
 */
public class Player {
	private String name;
	private Room room;
	private Socket Socket;

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

	public java.net.Socket getSocket() {
		return Socket;
	}

	public void setSocket(java.net.Socket socket) {
		Socket = socket;
	}
}
