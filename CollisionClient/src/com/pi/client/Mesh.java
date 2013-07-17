package com.pi.client;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.pi.collision.math.TransMatrix;
import com.pi.collision.math.Vector3D;

public class Mesh {
	private List<Solid> solids = new ArrayList<Solid>();

	private static class Facet {
		private List<Vector3D> verticies = new ArrayList<Vector3D>();
		private Vector3D normal;
		private Vector3D center;

		public void closeFacet() {
			center = new Vector3D();
			for (Vector3D v : verticies) {
				center.add(v);
			}
			center.multiply(1f / (float) verticies.size());
		}
	}

	private static class Solid {
		private List<Facet> facets = new ArrayList<Facet>();
		private Vector3D minPoint = new Vector3D(Float.MAX_VALUE,
				Float.MAX_VALUE, Float.MAX_VALUE);
		private Vector3D maxPoint = new Vector3D(Float.MIN_VALUE,
				Float.MIN_VALUE, Float.MIN_VALUE);
		private Vector3D center;

		public void closeSolid() {
			center = new Vector3D();
			for (Facet f : facets) {
				center.add(f.center);
			}
			center.multiply(1f / (float) facets.size());
		}

		boolean inBounding(Vector3D v) {
			return v.x > minPoint.x && v.y > minPoint.y && v.z > minPoint.z
					&& v.x < maxPoint.x && v.y < maxPoint.y && v.z < maxPoint.z;
		}
	}

	public Mesh() {
		// Lets load!
		try {
			BufferedReader reader = new BufferedReader(new FileReader(
					"xwing.stl"));
			TransMatrix mat = new TransMatrix();
			mat.setYRotation(2.5649745665360704f);
			while (true) {
				String s = reader.readLine();
				if (s == null) {
					break;
				}
				String[] parts = s.split(" ");
				if (parts[0].equalsIgnoreCase("solid")) {
					solids.add(new Solid());
				} else if (parts[0].equalsIgnoreCase("endsolid")) {
					solids.get(solids.size() - 1).closeSolid();
				} else {
					Solid solid = solids.get(solids.size() - 1);
					if (parts[0].equalsIgnoreCase("facet")) {
						solid.facets.add(new Facet());
						solid.facets.get(solid.facets.size() - 1).normal = mat
								.multiply(new Vector3D(Float.valueOf(parts[2]),
										Float.valueOf(parts[4]), Float
												.valueOf(parts[3])));
						solid.facets.get(solid.facets.size()-1).normal.normalize();
					} else if (parts[0].equalsIgnoreCase("endfacet")) {
						solid.facets.get(solid.facets.size() - 1).closeFacet();
					} else if (parts[0].equalsIgnoreCase("vertex")) {
						Vector3D v = mat.multiply(new Vector3D(Float
								.valueOf(parts[1]), Float.valueOf(parts[3]),
								Float.valueOf(parts[2])));
						solid.facets.get(solid.facets.size() - 1).verticies
								.add(v);
						solid.minPoint.x = Math.min(solid.minPoint.x, v.x);
						solid.minPoint.y = Math.min(solid.minPoint.y, v.y);
						solid.minPoint.z = Math.min(solid.minPoint.z, v.z);

						solid.maxPoint.x = Math.max(solid.maxPoint.x, v.x);
						solid.maxPoint.y = Math.max(solid.maxPoint.y, v.y);
						solid.maxPoint.z = Math.max(solid.maxPoint.z, v.z);
					}
				}
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void draw() {
		GL11.glBegin(GL11.GL_TRIANGLES);
		for (Solid solid : solids) {
			for (Facet f : solid.facets) {
				if (f.verticies.size() != 3) {
					System.err.println("Not a triangle");
				}
				GL11.glNormal3f(f.normal.x, f.normal.y, f.normal.z);
				for (Vector3D v : f.verticies) {
					GL11.glVertex3f(v.x, v.y, v.z);
				}
			}
		}
		GL11.glEnd();
	}

	public boolean collidesBounding(Vector3D v) {
		for (int i = 0; i < solids.size(); i++) {
			if (i != 2) {
				if (solids.get(i).inBounding(v)) {
					return true;
				}
			}
		}
		return false;
	}

	public void drawNormals() {
		GL11.glBegin(GL11.GL_LINES);
		for (Solid solid : solids) {
			for (Facet f : solid.facets) {
				GL11.glVertex3f(f.center.x, f.center.y, f.center.z);
				GL11.glVertex3f(f.center.x + f.normal.x, f.center.y
						+ f.normal.y, f.center.z + f.normal.z);
			}
		}
		GL11.glEnd();
	}

	public void drawBounding() {
		for (Solid solid : solids) {
			GL11.glBegin(GL11.GL_LINE_LOOP);
			GL11.glVertex3f(solid.minPoint.x, solid.minPoint.y,
					solid.minPoint.z);
			GL11.glVertex3f(solid.minPoint.x, solid.maxPoint.y,
					solid.minPoint.z);
			GL11.glVertex3f(solid.minPoint.x, solid.maxPoint.y,
					solid.maxPoint.z);
			GL11.glVertex3f(solid.minPoint.x, solid.minPoint.y,
					solid.maxPoint.z);
			GL11.glEnd();

			GL11.glBegin(GL11.GL_LINE_LOOP);
			GL11.glVertex3f(solid.maxPoint.x, solid.minPoint.y,
					solid.minPoint.z);
			GL11.glVertex3f(solid.maxPoint.x, solid.maxPoint.y,
					solid.minPoint.z);
			GL11.glVertex3f(solid.maxPoint.x, solid.maxPoint.y,
					solid.maxPoint.z);
			GL11.glVertex3f(solid.maxPoint.x, solid.minPoint.y,
					solid.maxPoint.z);
			GL11.glEnd();

			GL11.glBegin(GL11.GL_LINES);
			GL11.glVertex3f(solid.minPoint.x, solid.minPoint.y,
					solid.minPoint.z);
			GL11.glVertex3f(solid.maxPoint.x, solid.minPoint.y,
					solid.minPoint.z);

			GL11.glVertex3f(solid.minPoint.x, solid.maxPoint.y,
					solid.minPoint.z);
			GL11.glVertex3f(solid.maxPoint.x, solid.maxPoint.y,
					solid.minPoint.z);

			GL11.glVertex3f(solid.minPoint.x, solid.minPoint.y,
					solid.maxPoint.z);
			GL11.glVertex3f(solid.maxPoint.x, solid.minPoint.y,
					solid.maxPoint.z);

			GL11.glVertex3f(solid.minPoint.x, solid.maxPoint.y,
					solid.maxPoint.z);
			GL11.glVertex3f(solid.maxPoint.x, solid.maxPoint.y,
					solid.maxPoint.z);
			GL11.glEnd();
		}
	}
}
