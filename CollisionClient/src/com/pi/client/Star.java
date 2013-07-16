package com.pi.client;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Sphere;

import com.pi.collision.math.Vector3D;

public class Star {
	static Sphere sphere = new Sphere();
	Vector3D pos;

	public Star(float x, float y, float z) {
		pos = new Vector3D(x, y, z);
	}

	public void render() {
		GL11.glPushMatrix();
		GL11.glTranslatef(pos.x, pos.y, pos.z);
		sphere.draw(0.1f, 4, 4);
		GL11.glPopMatrix();
	}
}
