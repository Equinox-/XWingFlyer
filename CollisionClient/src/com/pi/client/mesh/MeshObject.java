package com.pi.client.mesh;

import java.util.List;

import org.lwjgl.opengl.GL11;

import com.pi.collision.math.Vector3D;

public class MeshObject {
	private List<Mesh> components;
	private Vector3D center;

	public MeshObject(List<Mesh> comps) {
		this.components = comps;
		center = new Vector3D();
		float count = 0;
		for (Mesh m : comps) {
			for (MeshVertex v : m.getVerticies()) {
				count++;
				center.add(v.getPosition());
			}
		}
		center.multiply(1f / count);
	}

	public void render() {
		GL11.glTranslatef(-center.x, -center.y, -center.z);
		for (Mesh m : components) {
			m.render();
		}
	}

	public void renderBoundingBox() {
		for (Mesh m : components) {
			m.renderBoundingBox();
		}
	}
}
