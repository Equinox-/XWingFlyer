package com.pi.client;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

import com.pi.collision.PacketInfo;

public class ClientGUI {
	static FloatBuffer light = (FloatBuffer) BufferUtils.createFloatBuffer(4)
			.put(new float[] { 1000, 1000, 1000, 1 }).rewind();
	static FloatBuffer lightDiffuse = (FloatBuffer) BufferUtils
			.createFloatBuffer(4).put(new float[] { .5f, .5f, .5f, 1f })
			.rewind();
	static FloatBuffer lightAmbient = (FloatBuffer) BufferUtils
			.createFloatBuffer(4).put(new float[] { .25f, .25f, .25f, 1f })
			.rewind();

	private static double horizontalTan = Math.tan(Math.toRadians(25));
	private Client client;
	private Mesh mesh;
	private List<Star> stars = new ArrayList<Star>();

	public ClientGUI(Client c) throws LWJGLException {
		this.client = c;
		this.mesh = new Mesh();
		for (int i = 0; i < 100; i++) {
			stars.add(new Star((float) (Math.random() - .5f) * 20f,
					(float) (Math.random() - .5f) * 20f,
					(float) (Math.random() - .5f) * 20f));
		}
		Display.setDisplayMode(new DisplayMode(768, 768));
		Display.create();
		Display.setLocation(0, 1050);

		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		double aspect = 1f;
		GL11.glFrustum(-horizontalTan, horizontalTan, aspect * -horizontalTan,
				aspect * horizontalTan, 1, 100000);
		GL11.glEnable(GL11.GL_DEPTH_TEST);

		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_COLOR_MATERIAL);
		GL11.glEnable(GL11.GL_LIGHT0);
		GL11.glEnable(GL11.GL_BLEND);

		while (!Display.isCloseRequested()) {
			for (ClientPlayer p : client.getPlayers()) {
				p.update();
			}

			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			GL11.glLoadIdentity();

			GL11.glTranslatef(0, 0, -3);
			GL11.glRotatef(6, 1, 0, 0);
			ClientPlayer me = client.getPlayers().get(client.getClientID());
			GL11.glMultMatrix(me.laggedMatrix);

			GL11.glLight(GL11.GL_LIGHT0, GL11.GL_POSITION, light);
			GL11.glLight(GL11.GL_LIGHT0, GL11.GL_AMBIENT, lightAmbient);
			GL11.glLight(GL11.GL_LIGHT0, GL11.GL_DIFFUSE, lightDiffuse);

			GL11.glColor3f(1f, 1f, 0f);
			for (Star s : stars) {
				s.render();
			}

			for (ClientPlayer p : client.getPlayers()) {
				GL11.glPushMatrix();
				GL11.glMultMatrix(p.currentMatrix);
				GL11.glRotatef(180, 0, 1, 0);
				GL11.glColor3f(1f, 1f, 1f);
				mesh.draw();
				GL11.glColor3f(1f, 0f, 0f);
				mesh.drawBounding();

				GL11.glPopMatrix();
			}

			Display.update();
			Display.sync(60);
			byte keystate = 0;
			if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
				keystate |= PacketInfo.KEY_ROLL_RIGHT_MASK;
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
				keystate |= PacketInfo.KEY_ROLL_LEFT_MASK;
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
				keystate |= PacketInfo.KEY_PITCH_UP_MASK;
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
				keystate |= PacketInfo.KEY_PITCH_DOWN_MASK;
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_Q)) {
				keystate |= PacketInfo.KEY_ACCEL_UP_MASK;
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_E)) {
				keystate |= PacketInfo.KEY_ACCEL_DOWN_MASK;
			}
			me.updateKeystate(keystate, System.currentTimeMillis());
			try {
				client.sendKeyStates(keystate);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
