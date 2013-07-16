package com.pi.collision.math;

public class Quaternion {
	private static final double DELTA = 1e-10;
	public float w, x, y, z;

	public Quaternion() {
		this(1, 0, 0, 0);
	}

	public Quaternion(float w, float x, float y, float z) {
		setRaw(w, x, y, z);
	}

	public Quaternion(float roll, float pitch, float yaw) {
		setRotation(roll, pitch, yaw);
	}

	public void setRaw(float w, float x, float y, float z) {
		this.w = w;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void setRaw(float w, Vector3D v) {
		this.w = w;
		this.x = v.x;
		this.y = v.y;
		this.z = v.z;
	}

	public float magnitude() {
		return (float) Math.sqrt((w * w) + (x * x) + (z * z) + (y * y));
	}

	public Quaternion normalize() {
		float mag = magnitude();
		w /= mag;
		x /= mag;
		y /= mag;
		z /= mag;
		return this;
	}

	public void setRotation(float roll, float pitch, float yaw) {
		float cr = (float) Math.cos(roll / 2);
		float cp = (float) Math.cos(pitch / 2);
		float cy = (float) Math.cos(yaw / 2);

		float sr = (float) Math.sin(roll / 2);
		float sp = (float) Math.sin(pitch / 2);
		float sy = (float) Math.sin(yaw / 2);

		float cpcy = cp * cy;
		float spsy = sp * sy;
		float cpsy = cp * sy;
		float spcy = sp * cy;

		w = cr * cpcy + sr * spsy;
		x = sr * cpcy - cr * spsy;
		y = cr * spcy + sr * cpsy;
		z = cr * cpsy - sr * spcy;
	}

	public void slerp(Quaternion to, float t) {
		slerp(this, to, t);
	}

	public void slerp(Quaternion from, Quaternion to, float t) {
		float[] to1 = new float[4];
		double omega, cosom, sinom;
		double scale0, scale1;

		// calc cosine
		cosom = from.x * to.x + from.y * to.y + from.z * to.z + from.w * to.w;

		// adjust signs (if necessary)
		if (cosom < 0.0) {
			cosom = -cosom;
			to1[0] = -to.x;
			to1[1] = -to.y;
			to1[2] = -to.z;
			to1[3] = -to.w;
		} else {
			to1[0] = to.x;
			to1[1] = to.y;
			to1[2] = to.z;
			to1[3] = to.w;
		}

		// calculate coefficients
		if ((1.0 - cosom) > DELTA) {
			// standard case (slerp)
			omega = Math.acos(cosom);
			sinom = Math.sin(omega);
			scale0 = Math.sin((1.0 - t) * omega) / sinom;
			scale1 = Math.sin(t * omega) / sinom;
		} else {
			// "from" and "to" quaternions are very close
			// ... so we can do a linear interpolation
			scale0 = 1.0f - t;
			scale1 = t;
		}

		// calculate final values
		x = (float) (scale0 * from.x + scale1 * to1[0]);
		y = (float) (scale0 * from.y + scale1 * to1[1]);
		z = (float) (scale0 * from.z + scale1 * to1[2]);
		w = (float) (scale0 * from.w + scale1 * to1[3]);
	}

	public void lerp(Quaternion to, float t) {
		lerp(this, to, t);
	}

	public void lerp(Quaternion from, Quaternion to, float t) {
		float[] to1 = new float[4];
		float cosom;
		float scale0, scale1;

		// calc cosine
		cosom = from.x * to.x + from.y * to.y + from.z * to.z + from.w * to.w;

		// adjust signs (if necessary)
		if (cosom < 0.0) {
			to1[0] = -to.x;
			to1[1] = -to.y;
			to1[2] = -to.z;
			to1[3] = -to.w;
		} else {
			to1[0] = to.x;
			to1[1] = to.y;
			to1[2] = to.z;
			to1[3] = to.w;
		}

		// interpolate linearly
		scale0 = 1.0f - t;
		scale1 = t;

		// calculate final values
		x = scale0 * from.x + scale1 * to1[0];
		y = scale0 * from.y + scale1 * to1[1];
		z = scale0 * from.z + scale1 * to1[2];
		w = scale0 * from.w + scale1 * to1[3];
	}

	public Quaternion clone() {
		return new Quaternion(w, x, y, z);
	}

	public Quaternion multiply(Quaternion q) {
		float E, F, G, H;
		float tw = w;
		float tx = x;
		float ty = y;

		E = (tx + z) * (q.x + q.y);
		F = (tx - z) * (q.x - q.y);
		G = (tw + ty) * (q.w - q.z);
		H = (tw - ty) * (q.w + q.z);

		w = (z - ty) * (q.y - q.z) + (-E - F + G + H) / 2;
		x = (tw + tx) * (q.w + q.x) - (E + F + G + H) / 2;
		y = (tw - tx) * (q.y + q.z) + (E - F + G - H) / 2;
		z = (ty + z) * (q.w - q.x) + (E - F - G + H) / 2;
		return this;
	}
}
