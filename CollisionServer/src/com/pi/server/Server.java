package com.pi.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;

import com.pi.collision.Player;
import com.pi.collision.debug.PIResourceViewer;
import com.pi.collision.debug.PlayerMonitorPanel;
import com.pi.collision.debug.ThreadMonitorPanel;
import com.pi.collision.util.IDAllocator;
import com.pi.collision.util.ObjectHeap;

public class Server extends Thread {
	private static final long TICK_TIME = 25;
	private static final long NET_UPDATE = 40;
	private DatagramSocket socket;
	private Map<SocketAddress, Integer> mapping = new HashMap<SocketAddress, Integer>();
	private ObjectHeap<ServerClient> clients;
	private ObjectHeap<Player> players;
	private Thread socketListener;
	public final ThreadGroup group;
	private IDAllocator ids = new IDAllocator();

	public Server(ThreadGroup group) throws IOException {
		super(group, "GameLoop");
		this.group = group;
		clients = new ObjectHeap<ServerClient>();
		players = new ObjectHeap<Player>();
		socket = new DatagramSocket(9293);
		start();
		socketListener = new Thread(group, new SocketListener(this));
		socketListener.setName("SocketAcceptor");
		socketListener.start();
		onConnect(null);
	}

	private int onConnect(SocketAddress addr) {
		int id = ids.checkOut();
		clients.set(id, new ServerClient(addr, this, (byte) id));
		players.set(id, clients.get(id).player);
		mapping.put(addr, id);
		System.out.println(id + " connected!");
		return id;
	}

	public void run() {
		int networkTick = 0;
		while (true) {
			long startTick = System.currentTimeMillis();
			for (ServerClient cli : clients) {
				if (cli != null) {
					cli.player.update();
				}
			}
			networkTick++;
			if (networkTick > NET_UPDATE) {
				networkTick = 0;
				try {
					networkUpdate();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			long tickLen = System.currentTimeMillis() - startTick;
			if (tickLen < TICK_TIME) {
				try {
					Thread.sleep(TICK_TIME - tickLen);
				} catch (InterruptedException e) {
				}
			}
		}
	}

	public void networkUpdate() throws IOException {
		for (ServerClient cli : clients) {
			if (cli != null) {
				sendToAll(cli.getPositionPacket());
				try {
					cli.checkPing();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void sendToAll(byte[] packet) throws IOException {
		for (ServerClient cli : clients) {
			if (cli != null) {
				try {
					cli.send(packet);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static class SocketListener implements Runnable {
		private Server server;

		public SocketListener(Server serv) {
			this.server = serv;
		}

		public void run() {
			DatagramPacket p = new DatagramPacket(new byte[1024], 1024);
			while (server.isAlive()) {
				try {
					server.socket.receive(p);
					Integer id = server.mapping.get(p.getSocketAddress());
					if (id == null) {
						id = server.onConnect(p.getSocketAddress());
					}
					if (id != null && p.getLength() > 0) {
						ServerClient c = server.clients.get(id);
						if (c != null) {
							c.process(p.getData(), p.getOffset(), p.getLength());
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void main(String[] args) throws IOException {
		ThreadGroup group = new ThreadGroup("Server");
		Server serv = new Server(group);
		PIResourceViewer view = new PIResourceViewer("Server");
		view.addTab("Threads", new ThreadMonitorPanel(group));
		view.addTab("Players", new PlayerMonitorPanel(serv.players));
		while (true) {
			try {
				Thread.sleep(500L);
			} catch (InterruptedException e) {
			}
		}
	}

	public void disposeClient(ServerClient serverClient) {
		mapping.remove(serverClient.getSocketAddress());
		clients.set(serverClient.getClientID(), null);
		players.set(serverClient.getClientID(), null);
		ids.checkIn(serverClient.getClientID());
		System.out.println(serverClient.getClientID() + " disconnected!");
	}

	public void send(DatagramPacket datagramPacket) throws IOException {
		socket.send(datagramPacket);
	}
}
