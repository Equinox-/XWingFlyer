package com.pi.collision;

public class PacketInfo {
	public static final int SERVER_CALC_CLOCK = 0;
	public static final int SERVER_UPDATE_CLOCK = 1;
	public static final int SERVER_UPDATE_POS = 2;
	public static final int SERVER_LOCAL_CLIENT = 3;
	public static final int[] SERVER_PACKET_LENGTHS = new int[] { 9, 17, 39, 2 };

	public static final int CLIENT_SYNC_CLOCK = 0;
	public static final int CLIENT_SEND_KEYSTATE = 1;
	public static final int[] CLIENT_PACKET_LENGTHS = new int[] { 17, 10 };

	public static final byte KEY_PITCH_UP_MASK = 0b00000001;
	public static final byte KEY_ROLL_LEFT_MASK = 0b00000010;
	public static final byte KEY_PITCH_DOWN_MASK = 0b00000100;
	public static final byte KEY_ROLL_RIGHT_MASK = 0b00001000;
	public static final byte KEY_ACCEL_UP_MASK = 0b00010000;
	public static final byte KEY_ACCEL_DOWN_MASK = 0b00100000;
}
