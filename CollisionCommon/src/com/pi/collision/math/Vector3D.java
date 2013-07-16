package com.pi.collision.math;

public class Vector3D {
	public float x, y, z;

	public Vector3D(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector3D() {
		this(0, 0, 0);
	}

	public Vector3D translate(float x, float y, float z) {
		this.x += x;
		this.y += y;
		this.z += z;
		return this;
	}

	public Vector3D subtract(Vector3D p) {
		this.x -= p.x;
		this.y -= p.y;
		this.z -= p.z;
		return this;
	}

	public Vector3D add(Vector3D p) {
		this.x += p.x;
		this.y += p.y;
		this.z += p.z;
		return this;
	}

	public Vector3D multiply(float scalar) {
		this.x *= scalar;
		this.y *= scalar;
		this.z *= scalar;
		return this;
	}

	public float dist(Vector3D p) {
		float dX = p.x - x, dY = p.y - y, dZ = p.z - z;
		return (float) Math.sqrt((dX * dX) + (dY * dY) + (dZ * dZ));
	}

	public float magnitude() {
		return (float) Math.sqrt((x * x) + (y * y) + (z * z));
	}

	@Override
	public Vector3D clone() {
		return new Vector3D(x, y, z);
	}

	@Override
	public int hashCode() {
		return (int) x << 24 ^ (int) y << 12 ^ (int) z << 6;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Vector3D) {
			Vector3D p = (Vector3D) o;
			float xD = p.x - x, yD = p.y - y, zD = p.z - z;
			return xD == 0 && yD == 0 && zD == 0;
		}
		return false;
	}

	@Override
	public String toString() {
		return "(" + x + "," + y + "," + z + ")";
	}

	public Vector3D translate(Vector3D trans) {
		return translate(trans.x, trans.y, trans.z);
	}

	public Vector3D normalize() {
		float dist = dist(new Vector3D(0, 0, 0));
		if (dist != 0) {
			x /= dist;
			y /= dist;
			z /= dist;
		}
		return this;
	}

	public static Vector3D normalize(Vector3D p) {
		return p.clone().normalize();
	}

	public static float dotProduct(Vector3D u, Vector3D v) {
		return (u.x * v.x) + (u.y * v.y) + (u.z * v.z);
	}

	public static Vector3D crossProduct(Vector3D u, Vector3D v) {
		return new Vector3D((u.y * v.z) - (u.z * v.y), (u.z * v.x)
				- (u.x * v.z), (u.x * v.y) - (u.y * v.x));
	}

	public static Vector3D negative(Vector3D p) {
		return new Vector3D(-p.x, -p.y, -p.z);
	}

	public Vector3D reverse() {
		x = -x;
		y = -y;
		z = -z;
		return this;
	}

	public Vector3D abs() {
		x = Math.abs(x);
		y = Math.abs(y);
		z = Math.abs(z);
		return this;
	}

	public void set(float i, float j, float k) {
		this.x = i;
		this.y = j;
		this.z = k;
	}

	public void set(Vector3D v) {
		set(v.x, v.y, v.z);
	}

	public float mag2() {
		return (x * x) + (y * y) + (z * z);
	}
}
