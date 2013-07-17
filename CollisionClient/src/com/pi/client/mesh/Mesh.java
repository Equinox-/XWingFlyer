package com.pi.client.mesh;

import java.util.List;

import org.lwjgl.opengl.GL11;

import com.pi.collision.math.Vector3D;

public class Mesh {
	private List<MeshVertex> verticies;
	private List<Integer> indicies;
	private int polygonSize;
	private String name;
	private Vector3D minPoint = new Vector3D(Float.MAX_VALUE, Float.MAX_VALUE,
			Float.MAX_VALUE);
	private Vector3D maxPoint = new Vector3D(-Float.MAX_VALUE,
			-Float.MAX_VALUE, -Float.MAX_VALUE);

	public Mesh(String name, List<MeshVertex> verts, List<Integer> indz,
			int polygonSize) {
		this.verticies = verts;
		this.indicies = indz;
		this.polygonSize = polygonSize;
		this.name = name;

		for (int i : indicies) {
			MeshVertex v = verticies.get(i);
			minPoint.x = Math.min(minPoint.x, v.getPosition().x);
			minPoint.y = Math.min(minPoint.y, v.getPosition().y);
			minPoint.z = Math.min(minPoint.z, v.getPosition().z);

			maxPoint.x = Math.max(maxPoint.x, v.getPosition().x);
			maxPoint.y = Math.max(maxPoint.y, v.getPosition().y);
			maxPoint.z = Math.max(maxPoint.z, v.getPosition().z);
		}
	}

	public List<MeshVertex> getVerticies() {
		return verticies;
	}

	public List<Integer> getIndicies() {
		return indicies;
	}

	public int getPolygonSize() {
		return polygonSize;
	}

	public void renderBoundingBox() {
		GL11.glBegin(GL11.GL_LINE_LOOP);
		GL11.glVertex3f(minPoint.x, minPoint.y, minPoint.z);
		GL11.glVertex3f(minPoint.x, maxPoint.y, minPoint.z);
		GL11.glVertex3f(minPoint.x, maxPoint.y, maxPoint.z);
		GL11.glVertex3f(minPoint.x, minPoint.y, maxPoint.z);
		GL11.glEnd();

		GL11.glBegin(GL11.GL_LINE_LOOP);
		GL11.glVertex3f(maxPoint.x, minPoint.y, minPoint.z);
		GL11.glVertex3f(maxPoint.x, maxPoint.y, minPoint.z);
		GL11.glVertex3f(maxPoint.x, maxPoint.y, maxPoint.z);
		GL11.glVertex3f(maxPoint.x, minPoint.y, maxPoint.z);
		GL11.glEnd();

		GL11.glBegin(GL11.GL_LINES);
		GL11.glVertex3f(minPoint.x, minPoint.y, minPoint.z);
		GL11.glVertex3f(maxPoint.x, minPoint.y, minPoint.z);

		GL11.glVertex3f(minPoint.x, maxPoint.y, minPoint.z);
		GL11.glVertex3f(maxPoint.x, maxPoint.y, minPoint.z);

		GL11.glVertex3f(minPoint.x, minPoint.y, maxPoint.z);
		GL11.glVertex3f(maxPoint.x, minPoint.y, maxPoint.z);

		GL11.glVertex3f(minPoint.x, maxPoint.y, maxPoint.z);
		GL11.glVertex3f(maxPoint.x, maxPoint.y, maxPoint.z);
		GL11.glEnd();
	}

	public boolean inBounding(Vector3D v) {
		return v.x > minPoint.x && v.y > minPoint.y && v.z > minPoint.z
				&& v.x < maxPoint.x && v.y < maxPoint.y && v.z < maxPoint.z;
	}

	public void render() {
		GL11.glBegin(getGLType());
		MeshMaterial bound = null;
		for (int i : indicies) {
			MeshVertex vert = verticies.get(i);
			if (vert.getMaterial() != bound) {
				GL11.glEnd();
				bound = vert.getMaterial();
				bound.bindGL();
				GL11.glBegin(getGLType());
			}
			GL11.glTexCoord2f(vert.getTextureUV()[0], vert.getTextureUV()[1]);
			GL11.glNormal3f(vert.getNormal().x, vert.getNormal().y,
					vert.getNormal().z);
			GL11.glVertex3f(vert.getPosition().x, vert.getPosition().y,
					vert.getPosition().z);
		}
		GL11.glEnd();
	}

	public int getGLType() {
		switch (polygonSize) {
		case 1:
			return GL11.GL_POINTS;
		case 2:
			return GL11.GL_LINES;
		case 3:
			return GL11.GL_TRIANGLES;
		case 4:
			return GL11.GL_QUADS;
		default:
			return GL11.GL_POLYGON;
		}
	}
}
