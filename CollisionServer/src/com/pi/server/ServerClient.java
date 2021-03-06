package com.pi.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketAddress;

import com.pi.collision.PacketInfo;
import com.pi.collision.Player;

public class ServerClient {
	private static final long PING_TIMEOUT = 10000L;
	private static final long CLIENT_TIMEOUT = 60000L;
	private SocketAddress addr;
	Player player = new Player();
	private long clockSkew; // Client-Server
	private long trip;
	private Server server;
	private byte id;

	private long lastPing = 0;
	private long lastPingSent = 0;

	public ServerClient(SocketAddress s, Server server, byte id) {
		this.server = server;
		this.addr = s;
		this.id = id;
		try {
			sendCalculateClockSkew();
			sendLocalClient();
		} catch (IOException e) {
		}
	}

	public SocketAddress getSocketAddress() {
		return addr;
	}

	public void checkPing() throws IOException {
		long lastPingS = Math.max(lastPing, lastPingSent);
		if (lastPingS + PING_TIMEOUT < System.currentTimeMillis()) {
			sendCalculateClockSkew();
		}
		if (lastPing > 0
				&& lastPing + CLIENT_TIMEOUT < System.currentTimeMillis()) {
			server.disposeClient(this);
		}
	}

	public void sendLocalClient() throws IOException {
		send(new byte[] { PacketInfo.SERVER_LOCAL_CLIENT, id });
	}

	public void sendCalculateClockSkew() throws IOException {
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		DataOutputStream dOut = new DataOutputStream(bOut);
		dOut.writeByte(PacketInfo.SERVER_CALC_CLOCK);
		dOut.writeLong(System.currentTimeMillis());
		dOut.close();
		byte[] pack = bOut.toByteArray();
		bOut.close();
		send(pack);
		this.lastPingSent = System.currentTimeMillis();
	}

	public void sendUpdateClockSkew() throws IOException {
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		DataOutputStream dOut = new DataOutputStream(bOut);
		dOut.writeByte(PacketInfo.SERVER_UPDATE_CLOCK);
		dOut.writeLong(clockSkew);
		dOut.writeLong(trip);
		dOut.close();
		byte[] pack = bOut.toByteArray();
		bOut.close();
		send(pack);
	}

	public byte[] getPositionPacket() throws IOException {
		if (player.getPositionUpdate() == 0) {
			player.update();
		}
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		DataOutputStream dOut = new DataOutputStream(bOut);
		dOut.writeByte(PacketInfo.SERVER_UPDATE_POS);
		dOut.writeByte(id);
		dOut.writeLong(player.getPositionUpdate()); // System.currentTimeMillis()
		dOut.writeFloat(player.position.x);
		dOut.writeFloat(player.position.y);
		dOut.writeFloat(player.position.z);
		dOut.writeFloat(player.quaternion.w);
		dOut.writeFloat(player.quaternion.x);
		dOut.writeFloat(player.quaternion.y);
		dOut.writeFloat(player.quaternion.z);
		dOut.writeByte(player.getKeyState());
		dOut.writeFloat(player.speed);
		dOut.close();
		byte[] pack = bOut.toByteArray();
		bOut.close();
		return pack;
	}

	public void send(byte[] packet) throws IOException {
		if (addr != null) {
			server.send(new DatagramPacket(packet, packet.length, addr));
		}
	}

	public void process(byte[] pack, int offset, int length) throws IOException {
		long tick = System.currentTimeMillis();
		DataInputStream dataIn = new DataInputStream(new ByteArrayInputStream(
				pack, offset, length));
		switch ((int) dataIn.readByte()) {
		case PacketInfo.CLIENT_SYNC_CLOCK:
			long trip = tick - dataIn.readLong();
			long server = tick - (trip / 2);
			long client = dataIn.readLong();
			clockSkew = (client - server);
			if (clockSkew > 0) {
				clockSkew -= (trip / 2);
			} else {
				clockSkew += (trip / 2);
			}
			this.trip = trip;
			sendUpdateClockSkew();
			System.out.println("Skew: " + clockSkew + ", Trip: " + trip);
			lastPing = System.currentTimeMillis();
			break;
		case PacketInfo.CLIENT_SEND_KEYSTATE:
			long time = dataIn.readLong();
			byte keyState = dataIn.readByte();
			if (time > player.getLastControlUpdate()) {
				player.updateKeystate(keyState, time);
			}
			break;
		}
		dataIn.close();
	}

	public byte getClientID() {
		return id;
	}
}
