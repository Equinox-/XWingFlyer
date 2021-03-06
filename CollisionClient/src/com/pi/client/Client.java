package com.pi.client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.pi.collision.PacketInfo;
import com.pi.collision.debug.PIResourceViewer;
import com.pi.collision.debug.PlayerMonitorPanel;
import com.pi.collision.debug.ThreadMonitorPanel;
import com.pi.collision.util.ObjectHeap;

public class Client extends Thread {
	private DatagramSocket sock;
	private long clockSkew; // Client-Server
	private long trip;
	private ThreadGroup group;
	private ObjectHeap<ClientPlayer> players;
	private byte clientID = -1;
	private static final InetAddress remote;
	static {
		try {
			remote = InetAddress.getLocalHost();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private boolean singlePlayer = false;

	public Client(ThreadGroup group) throws UnknownHostException, IOException {
		super(group, "ClientListener");
		this.group = group;
		this.players = new ObjectHeap<ClientPlayer>();
		if (singlePlayer) {
			players.set(0, new ClientPlayer((byte) 0));
			clientID = 0;
		} else {
			this.sock = new DatagramSocket();
			sock.connect(remote, 9293);
			send(new byte[0]);
			start();
		}
	}

	public void send(byte[] packet) throws IOException {
		if (!singlePlayer) {
			sock.send(new DatagramPacket(packet, packet.length, remote, 9293));
		}
	}

	public void sendSyncClocks(long serverTime) throws IOException {
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		DataOutputStream dOut = new DataOutputStream(bOut);
		dOut.writeByte(PacketInfo.CLIENT_SYNC_CLOCK);
		dOut.writeLong(serverTime);
		dOut.writeLong(System.currentTimeMillis());
		dOut.close();
		byte[] pack = bOut.toByteArray();
		bOut.close();
		send(pack);
	}

	public void sendKeyStates(byte keyStates) throws IOException {
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		DataOutputStream dOut = new DataOutputStream(bOut);
		dOut.writeByte(PacketInfo.CLIENT_SEND_KEYSTATE);
		dOut.writeLong(System.currentTimeMillis() - clockSkew);
		dOut.writeByte(keyStates);
		dOut.close();
		byte[] pack = bOut.toByteArray();
		bOut.close();
		send(pack);
	}

	public void process(byte[] pack, int offset, int length) throws IOException {
		DataInputStream dataIn = new DataInputStream(new ByteArrayInputStream(
				pack, offset, length));
		switch ((int) dataIn.readByte()) {
		case PacketInfo.SERVER_CALC_CLOCK:
			sendSyncClocks(dataIn.readLong());
			break;
		case PacketInfo.SERVER_UPDATE_CLOCK:
			clockSkew = dataIn.readLong();
			trip = dataIn.readLong();
			System.out.println("Skew: " + clockSkew + ", Trip: " + trip);
			break;
		case PacketInfo.SERVER_UPDATE_POS:
			byte id = dataIn.readByte();
			long time = dataIn.readLong() + clockSkew;
			float x = dataIn.readFloat();
			float y = dataIn.readFloat();
			float z = dataIn.readFloat();
			float qW = dataIn.readFloat();
			float qX = dataIn.readFloat();
			float qY = dataIn.readFloat();
			float qZ = dataIn.readFloat();
			ClientPlayer p = players.get(id);
			if (p == null) {
				players.set(id, p = new ClientPlayer(id));
			}
			if (id != getClientID()
					|| p.getLastControlUpdate() + (trip * 4) < time) {
				p.updatePosition(time, x, y, z, qW, qX, qY, qZ,
						dataIn.readByte(), dataIn.readFloat());
			}
			break;
		case PacketInfo.SERVER_LOCAL_CLIENT:
			clientID = dataIn.readByte();
			break;
		}
		dataIn.close();
	}

	public byte getClientID() {
		return clientID;
	}

	@Override
	public void run() {
		DatagramPacket data = new DatagramPacket(new byte[1024], 1024);
		while (sock.isConnected()) {
			try {
				sock.receive(data);
				process(data.getData(), data.getOffset(), data.getLength());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) throws UnknownHostException,
			IOException {
		ThreadGroup group = new ThreadGroup("ClientGroup");
		PIResourceViewer view = new PIResourceViewer("Client");
		view.addTab("Threads", new ThreadMonitorPanel(group));
		Client client = new Client(group);
		view.addTab("Players", new PlayerMonitorPanel(client.players));
		try {
			//while (client.isAlive()) {
			//	Thread.sleep(100L);
			//}
			new ClientGUI(client);
		} catch (Exception e) {
		}
	}

	public ObjectHeap<ClientPlayer> getPlayers() {
		return players;
	}
}
