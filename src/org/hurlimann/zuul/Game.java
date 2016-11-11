package org.hurlimann.zuul;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;

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
	private HashMap<SocketChannel, Player> playerMap;
	private ServerSocketChannel serverSocketChannel;
	private Selector selector;

	/**
	 * Create the game and initialise its internal map.
	 */
	public Game() {
		createRooms();
	}

	public void initialize() throws IOException {
		playerMap = new HashMap<>();

		serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.configureBlocking(false);
		serverSocketChannel.bind(new InetSocketAddress("localhost", 7331));

		selector = Selector.open();
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
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
		outside.setExit(Direction.EAST, theater);
		outside.setExit(Direction.SOUTH, lab);
		outside.setExit(Direction.WEST, pub);

		theater.setExit(Direction.WEST, outside);

		pub.setExit(Direction.EAST, outside);

		lab.setExit(Direction.NORTH, outside);
		lab.setExit(Direction.EAST, office);

		office.setExit(Direction.WEST, lab);

		startingRoom = outside; // start game outside
	}

	/**
	 * Main play routine. Loops until end of play.
	 */
	public void play() throws IOException, InterruptedException {

		while (true) {
			selector.selectNow();
			for (SelectionKey selectionKey : selector.selectedKeys()) {
				if (selectionKey.isAcceptable()) {
					ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
					SocketChannel socketChannel = serverSocketChannel.accept();

					socketChannel.configureBlocking(false);
					socketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);

					// TODO: Implement username
					final Player newPlayer = new Player("user123", startingRoom, socketChannel);
					playerMap.put(socketChannel, newPlayer);

					newPlayer.printWelcome();
				} else if (selectionKey.isReadable()) {
					SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
					ByteBuffer buffer = ByteBuffer.allocate(1024);
					int numRead = socketChannel.read(buffer);
					if (numRead == -1) {
						playerMap.remove(socketChannel);
						socketChannel.close();
						selectionKey.cancel();
					}


					Player player = playerMap.get(socketChannel);

					byte[] data = new byte[numRead];
					System.arraycopy(buffer.array(), 0, data, 0, numRead);
					String input = new String(data);

					boolean wantToQuit = player.handleInput(input);
					if (wantToQuit) {
						socketChannel.close();
						playerMap.remove(socketChannel);
					}
				}
			}

			selector.selectedKeys().clear();

			Thread.sleep(500L);
		}
	}
}
