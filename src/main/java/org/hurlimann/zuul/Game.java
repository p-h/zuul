package org.hurlimann.zuul;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

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

class Game {
	/**
	 * Item spawn chance in tenths of a percent
	 */
	private static final long MAX_ITEM_COUNT = 15;

	private Room startingRoom;
	private Map<SocketChannel, Player> playerMap = new HashMap<>();
	private final List<Room> rooms;
	private Selector selector;

	/**
	 * Create the game, initialise its internal map and opens sets up the network connection.
	 */
	public Game() throws IOException {
		Room outside, theater, pub, lab, office, cafeteria;

		// create the rooms
		outside = new Room("outside the main entrance of the university");
		theater = new Room("in a lecture theater");
		pub = new Room("in the campus pub");
		lab = new Room("in a computing lab");
		office = new Room("in the computing admin office");
		cafeteria = new Room("in the cafeteria. There's lots of delicious food.");

		outside.setExit(Direction.EAST, theater);
		outside.setExit(Direction.SOUTH, lab);
		outside.setExit(Direction.WEST, pub);

		theater.setExit(Direction.WEST, outside);

		pub.setExit(Direction.EAST, outside);
		pub.setExit(Direction.WEST, cafeteria);

		cafeteria.setExit(Direction.EAST, pub);

		lab.setExit(Direction.NORTH, outside);
		lab.setExit(Direction.EAST, office);

		office.setExit(Direction.WEST, lab);

		startingRoom = outside; // start game outside

		List<Room> rooms = new ArrayList<>();
		rooms.add(outside);
		rooms.add(theater);
		rooms.add(theater);
		rooms.add(pub);
		rooms.add(lab);
		rooms.add(office);
		rooms.add(cafeteria);

		this.rooms = Collections.unmodifiableList(rooms);

		final ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.configureBlocking(false);
		// TODO: Make port configurable
		serverSocketChannel.bind(new InetSocketAddress(InetAddress.getLocalHost(), 7331));

		selector = Selector.open();
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
	}

	/**
	 * Main play routine. Loops until end of play.
	 */
	public void play() throws IOException, InterruptedException {

		//noinspection InfiniteLoopStatement
		while (true) {
			triggerPotentialSpawns();

			Iterator<Map.Entry<SocketChannel, Player>> it = playerMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<SocketChannel, Player> entry = it.next();
				if (entry.getValue().isToDelete()) {
					SocketChannel socketChannel = entry.getKey();
					it.remove();
					removeAndCleanupPlayer(socketChannel);
				}
			}

			selector.selectNow();
			for (SelectionKey selectionKey : selector.selectedKeys()) {
				if (selectionKey.isAcceptable()) {
					acceptNewPlayer(selectionKey);
				} else if (selectionKey.isReadable()) {
					readPlayerInput(selectionKey);
				}
			}

			selector.selectedKeys().clear();

			// TODO: Use elapsed time instead
			Thread.sleep(500L);
		}
	}

	/**
	 * Handles the cleanup of a player that is slated to be removed
	 *
	 * @param socketChannel
	 */
	private void removeAndCleanupPlayer(SocketChannel socketChannel) {
		playerMap.remove(socketChannel);
		try {
			socketChannel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Handles the cleanup of a player that is slated to be removed
	 *
	 * @param socketChannel
	 * @param selectionKey
	 */
	private void removeAndCleanupPlayer(SocketChannel socketChannel, SelectionKey selectionKey) {
		selectionKey.cancel();
		removeAndCleanupPlayer(socketChannel);
	}

	/**
	 * Triggers spawnings every cycle
	 */
	private void triggerPotentialSpawns() {
		long itemsCount = rooms
				.stream()
				.flatMap(r -> r.getItems().stream())
				.count();

		if (itemsCount < MAX_ITEM_COUNT) {
			rooms.forEach(Room::updateRoom);
		}
	}

	/**
	 * Reads player input and decided what to do with it.
	 * Removes players that want to quit or abruptly disconnect.
	 * @param selectionKey
	 * @throws IOException
	 */
	private void readPlayerInput(SelectionKey selectionKey) throws IOException {
		SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		int numRead = socketChannel.read(buffer);
		if (numRead > -1) {
			Player player = playerMap.get(socketChannel);

			byte[] data = new byte[numRead];
			System.arraycopy(buffer.array(), 0, data, 0, numRead);
			String input = new String(data);

			boolean wantToQuit = player.handleInput(input);
			if (wantToQuit) {
				socketChannel.close();
				playerMap.remove(socketChannel);
			}
		} else {
			removeAndCleanupPlayer(socketChannel, selectionKey);
		}
	}

	/**
	 * Creates a new player and adds him to the appropriate collections.
	 * @param selectionKey
	 */
	private void acceptNewPlayer(SelectionKey selectionKey) {
		SocketChannel socketChannel = null;
		try {
			ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
			socketChannel = serverSocketChannel.accept();

			socketChannel.configureBlocking(false);
			socketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);

			long newUserId = ThreadLocalRandom.current().nextLong(0, Long.MAX_VALUE);
			final Player newPlayer = new Player("player" + newUserId,
					startingRoom, socketChannel);
			playerMap.put(socketChannel, newPlayer);

			startingRoom.addPlayer(newPlayer);

			newPlayer.printWelcome();
		} catch (IOException e) {
			removeAndCleanupPlayer(socketChannel, selectionKey);
		}
	}
}
