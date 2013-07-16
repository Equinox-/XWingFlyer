package com.pi.collision;

import com.pi.collision.math.Quaternion;
import com.pi.collision.math.TransMatrix;
import com.pi.collision.math.Vector3D;

public class Player {
	private static final float ROLL_SECOND = (float) Math.PI / 4f;
	private static final float PITCH_SECOND = (float) Math.PI / 4f;
	private static final float ACCEL_SECOND = 1.5f;

	public Vector3D position = new Vector3D();
	public float speed = 0.1f;
	public Quaternion quaternion = new Quaternion();
	protected TransMatrix matrix = new TransMatrix();

	protected long lastUpdate;
	private byte keyState;
	private byte laggedKeyState;
	private long controlUpdate;
	private boolean rolledBack = true;

	public void updateKeystate(byte keys, long time) {
		this.laggedKeyState = keyState;
		this.keyState = keys;
		this.controlUpdate = time;
		rolledBack = false;
	}

	public long getPositionUpdate() {
		return lastUpdate;
	}

	public Vector3D getLocation() {
		return position;
	}

	public Quaternion getQuaternion() {
		return quaternion;
	}

	public float getSpeed() {
		return speed;
	}

	public void update() {
		if (lastUpdate != 0) {
			Vector3D velocity = matrix.multiply(new Vector3D(0, 0, -speed))
					.subtract(position);
			if (!rolledBack) {
				float seconds = ((float) (controlUpdate - lastUpdate)) / 1000f;
				System.out.println("Rollback: " + seconds);
				// Rollback position
				position.x += velocity.x * seconds;
				position.y += velocity.y * seconds;
				position.z += velocity.z * seconds;

				// Rollback velocity
				applyKeystate(laggedKeyState, -seconds);
				lastUpdate = System.currentTimeMillis();
				rolledBack = true;
			}
			float seconds = ((float) (System.currentTimeMillis() - lastUpdate)) / 1000f;
			// Update velocity based on keystate
			applyKeystate(keyState, seconds);
			matrix.setQuaternion(quaternion, position.x, position.y,
					position.z);
			velocity = matrix.multiply(new Vector3D(0, 0, speed)).subtract(position);
			// Update position based on keystate
			position.x += velocity.x * seconds;
			position.y += velocity.y * seconds;
			position.z += velocity.z * seconds;
		}
		lastUpdate = System.currentTimeMillis();
	}

	TransMatrix tmp = new TransMatrix();

	private void applyKeystate(byte keystate, float seconds) {
		if ((keystate & PacketInfo.KEY_ACCEL_UP_MASK) == PacketInfo.KEY_ACCEL_UP_MASK) {
			speed *= 1f + (ACCEL_SECOND * seconds);
		} else if ((keystate & PacketInfo.KEY_ACCEL_DOWN_MASK) == PacketInfo.KEY_ACCEL_DOWN_MASK) {
			speed *= 1f - (1f / ACCEL_SECOND * seconds);
		}

		float pitchMod = 0;
		float rollMod = 0;
		if ((keystate & PacketInfo.KEY_ROLL_LEFT_MASK) == PacketInfo.KEY_ROLL_LEFT_MASK) {
			rollMod = -seconds * ROLL_SECOND;
		} else if ((keystate & PacketInfo.KEY_ROLL_RIGHT_MASK) == PacketInfo.KEY_ROLL_RIGHT_MASK) {
			rollMod = seconds * ROLL_SECOND;
		}

		if ((keystate & PacketInfo.KEY_PITCH_UP_MASK) == PacketInfo.KEY_PITCH_UP_MASK) {
			pitchMod = -seconds * PITCH_SECOND;
		} else if ((keystate & PacketInfo.KEY_PITCH_DOWN_MASK) == PacketInfo.KEY_PITCH_DOWN_MASK) {
			pitchMod = seconds * PITCH_SECOND;
		}
		if (pitchMod != 0 || rollMod != 0) {
			quaternion.multiply(new Quaternion(pitchMod, 0, rollMod));
		}
	}

	boolean lol = true;

	public byte getKeyState() {
		return keyState;
	}
}
