package com.pi.client;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import com.pi.collision.Player;
import com.pi.collision.math.Quaternion;
import com.pi.collision.math.TransMatrix;

public class ClientPlayer extends Player {
	private static final long CAMERA_CATCH_ANIMATION = 1000L;

	// Camera animation
	private Quaternion laggedQuaternion = new Quaternion();
	private TransMatrix laggedMatrixT = new TransMatrix();
	public FloatBuffer laggedMatrix = BufferUtils.createFloatBuffer(32);
	public FloatBuffer currentMatrix = BufferUtils.createFloatBuffer(32);

	private byte id;

	public ClientPlayer(byte id2) {
		this.id = id2;
	}

	public void updatePosition(long time, float x, float y, float z, float qW,
			float qX, float qY, float qZ, byte keyState, float f) {
		lastUpdate = System.currentTimeMillis();
		position.x = x;
		position.y = y;
		position.z = z;
		quaternion.w = qW;
		quaternion.x = qX;
		quaternion.y = qY;
		quaternion.z = qZ;
		this.speed = f;
		super.updateKeystate(keyState, time);
	}

	public void update() {
		if (lastUpdate == 0) {
			lastUpdate = System.currentTimeMillis();
		}
		float seconds = ((float) (System.currentTimeMillis() - lastUpdate))
				/ ((float) CAMERA_CATCH_ANIMATION);

		super.update();

		if (seconds > 1) {
			laggedQuaternion.x = quaternion.x;
			laggedQuaternion.y = quaternion.y;
			laggedQuaternion.z = quaternion.z;
			laggedQuaternion.w = quaternion.w;
		} else {
			laggedQuaternion.x += (quaternion.x - laggedQuaternion.x) * seconds;
			laggedQuaternion.y += (quaternion.y - laggedQuaternion.y) * seconds;
			laggedQuaternion.z += (quaternion.z - laggedQuaternion.z) * seconds;
			laggedQuaternion.w += (quaternion.w - laggedQuaternion.w) * seconds;
		}
		laggedMatrixT.setQuaternion(laggedQuaternion, position.x, position.y,
				position.z);
		laggedMatrix = laggedMatrixT.inverse().writeToBuffer(laggedMatrix);
		currentMatrix = matrix.writeToBuffer(currentMatrix);
	}

	public byte getID() {
		return id;
	}

	public Quaternion getAnimatedQuaternion() {
		return laggedQuaternion;
	}
}
