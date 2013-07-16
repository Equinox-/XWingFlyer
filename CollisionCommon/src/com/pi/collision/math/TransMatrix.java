package com.pi.collision.math;

import java.nio.FloatBuffer;

public class TransMatrix extends Matrix {

	public TransMatrix() {
		super(4, 4);
		identity();
	}

	public TransMatrix(Matrix m) {
		super(4, 4);
		copy(m);
	}

	public TransMatrix copy(Matrix m) {
		for (int i = 0; i < m.getRows(); i++)
			System.arraycopy(m.data[i], 0, data[i], 0, 4);
		return this;
	}

	public TransMatrix(Quaternion q, float x, float y, float z) {
		this();
		setQuaternion(q, x, y, z);
	}

	public FloatBuffer writeToBuffer(FloatBuffer buff) {
		buff.put(data[0][0]);
		buff.put(data[1][0]);
		buff.put(data[2][0]);
		buff.put(data[3][0]);

		buff.put(data[0][1]);
		buff.put(data[1][1]);
		buff.put(data[2][1]);
		buff.put(data[3][1]);

		buff.put(data[0][2]);
		buff.put(data[1][2]);
		buff.put(data[2][2]);
		buff.put(data[3][2]);

		buff.put(data[0][3]);
		buff.put(data[1][3]);
		buff.put(data[2][3]);
		buff.put(data[3][3]);
		
		return (FloatBuffer) buff.rewind();
	}

	public void setQuaternion(Quaternion q, float x, float y, float z) {
		float x2 = q.x + q.x;
		float y2 = q.y + q.y;
		float z2 = q.z + q.z;

		float wx = q.w * x2;
		float wy = q.w * y2;
		float wz = q.w * z2;

		float xx = q.x * x2;
		float xy = q.x * y2;
		float xz = q.x * z2;

		float yy = q.y * y2;
		float yz = q.y * z2;

		float zz = q.z * z2;
		data[0][0] = 1 - (yy + zz);
		data[0][1] = xy - wz;
		data[0][2] = xz + wy;
		data[0][3] = x;

		data[1][0] = xy + wz;
		data[1][1] = 1 - (xx + zz);
		data[1][2] = yz - wx;
		data[1][3] = y;

		data[2][0] = xz - wy;
		data[2][1] = yz + wx;
		data[2][2] = 1 - (xx + yy);
		data[2][3] = z;

		data[3][0] = 0;
		data[3][1] = 0;
		data[3][2] = 0;
		data[3][3] = 1;
	}

	public TransMatrix setRotation(float x, float y, float z, float radians) {
		float c = (float) Math.cos(radians), s = (float) Math.sin(radians);
		set(0, 0, c + (x * x * (1 - c)));
		set(0, 1, x * y * (1 - c) - z * s);
		set(0, 2, x * z * (1 - c) + y * s);

		set(1, 0, y * x * (1 - c) + z * s);
		set(1, 1, c + (y * y * (1 - c)));
		set(1, 2, y * z * (1 - c) - x * s);

		set(2, 0, z * x * (1 - c) - y * s);
		set(2, 1, z * y * (1 - c) + x * s);
		set(2, 2, c + (z * z * (1 - c)));
		return this;
	}

	public TransMatrix identity() {
		set(0, 0, 1);
		set(1, 0, 0);
		set(2, 0, 0);
		set(3, 0, 0);

		set(0, 1, 0);
		set(1, 1, 1);
		set(2, 1, 0);
		set(3, 1, 0);

		set(0, 2, 0);
		set(1, 2, 0);
		set(2, 2, 1);
		set(3, 2, 0);

		set(0, 3, 0);
		set(1, 3, 0);
		set(2, 3, 0);
		set(3, 3, 1);
		return this;
	}

	public TransMatrix setXRotation(float radians) {
		return setRotation(1, 0, 0, radians);
	}

	public TransMatrix setYRotation(float yaw) {
		return setRotation(0, 1, 0, yaw);
	}

	public TransMatrix setZRotation(float radians) {
		return setRotation(0, 0, 1, radians);
	}

	public Vector3D multiply(Vector3D v) {
		float resX = get(0, 3) + (v.x * get(0, 0)) + (v.y * get(0, 1))
				+ (v.z * get(0, 2));
		float resY = get(1, 3) + (v.x * get(1, 0)) + (v.y * get(1, 1))
				+ (v.z * get(1, 2));
		float resZ = get(2, 3) + (v.x * get(2, 0)) + (v.y * get(2, 1))
				+ (v.z * get(2, 2));
		return new Vector3D((float) resX, (float) resY, (float) resZ);
	}

	public TransMatrix setForwardSystemTranslation(Vector3D origin,
			Vector3D xVec, Vector3D yVec, Vector3D zVec) {
		Vector3D nLoc = origin.clone().reverse();
		set(0, 0, xVec.x);
		set(1, 0, xVec.y);
		set(2, 0, xVec.z);
		set(0, 1, yVec.x);
		set(1, 1, yVec.y);
		set(2, 1, yVec.z);
		set(0, 2, zVec.x);
		set(1, 2, zVec.y);
		set(2, 2, zVec.z);
		setTranslation(Vector3D.dotProduct(nLoc, xVec),
				Vector3D.dotProduct(nLoc, yVec),
				Vector3D.dotProduct(nLoc, zVec));
		return this;
	}

	public TransMatrix setReverseSystemTranslation(Vector3D origin,
			Vector3D xVec, Vector3D yVec, Vector3D zVec) {
		set(0, 0, xVec.x);
		set(0, 1, xVec.y);
		set(0, 2, xVec.z);

		set(1, 0, yVec.x);
		set(1, 1, yVec.y);
		set(1, 2, yVec.z);

		set(2, 0, zVec.x);
		set(2, 1, zVec.y);
		set(2, 2, zVec.z);

		setTranslation(origin.x, origin.y, origin.z);
		return this;
	}

	public TransMatrix setTranslation(float x, float y, float z) {
		set(0, 3, x);
		set(1, 3, y);
		set(2, 3, z);
		set(3, 3, 1);
		return this;
	}

	public TransMatrix multiply(TransMatrix b) {
		Matrix m = Matrix.multiply(this, b);
		for (int i = 0; i < m.getCols(); i++) {
			System.arraycopy(m.data[i], 0, data[i], 0, 4);
		}
		return this;
	}

	public Vector3D rotate(Vector3D norm) {
		Vector3D ret = new Vector3D(0, 0, 0);
		ret.x = get(0) * norm.x + get(1) * norm.y + get(2) * norm.z;
		ret.y = get(4) * norm.x + get(5) * norm.y + get(6) * norm.z;
		ret.z = get(8) * norm.x + get(9) * norm.y + get(10) * norm.z;
		return ret;
	}

	public float determinant() {
		return data[0][0]
				* (data[1][1]
						* (data[2][2] * data[3][3] - data[3][2] * data[2][3])
						- data[1][2]
						* (data[2][1] * data[3][3] - data[3][1] * data[2][3]) + data[1][3]
						* (data[2][1] * data[3][2] - data[3][1] * data[2][2]))
				- data[0][1]
				* (data[1][0]
						* (data[2][2] * data[3][3] - data[3][2] * data[2][3])
						- data[1][2]
						* (data[2][0] * data[3][3] - data[3][0] * data[2][3]) + data[1][3]
						* (data[2][0] * data[3][2] - data[3][0] * data[2][2]))
				+ data[0][2]
				* (data[1][0]
						* (data[2][1] * data[3][3] - data[3][1] * data[2][3])
						- data[1][1]
						* (data[2][0] * data[3][3] - data[3][0] * data[2][3]) + data[1][3]
						* (data[2][0] * data[3][1] - data[3][0] * data[2][1]))
				- data[0][3]
				* (data[1][0]
						* (data[2][1] * data[3][2] - data[3][1] * data[2][2])
						- data[1][1]
						* (data[2][0] * data[3][2] - data[3][0] * data[2][2]) + data[1][2]
						* (data[2][0] * data[3][1] - data[3][0] * data[2][1]));
	}

	public TransMatrix adjugate() {
		TransMatrix adj = new TransMatrix();
		adj.data[0][0] = (data[1][1] * data[2][2] * data[3][3])
				+ (data[1][2] * data[2][3] * data[3][1])
				+ (data[1][3] * data[2][1] * data[3][2])
				- (data[1][1] * data[2][3] * data[3][2])
				- (data[1][2] * data[2][1] * data[3][3])
				- (data[1][3] * data[2][2] * data[3][1]);
		adj.data[0][1] = (data[0][1] * data[2][3] * data[3][2])
				+ (data[0][2] * data[2][1] * data[3][3])
				+ (data[0][3] * data[2][2] * data[3][1])
				- (data[0][1] * data[2][2] * data[3][3])
				- (data[0][2] * data[2][3] * data[3][1])
				- (data[0][3] * data[2][1] * data[3][2]);
		adj.data[0][2] = (data[0][1] * data[1][2] * data[3][3])
				+ (data[0][2] * data[1][3] * data[3][1])
				+ (data[0][3] * data[1][1] * data[3][2])
				- (data[0][1] * data[1][3] * data[3][2])
				- (data[0][2] * data[1][1] * data[3][3])
				- (data[0][3] * data[1][2] * data[3][1]);
		adj.data[0][3] = (data[0][1] * data[1][3] * data[2][2])
				+ (data[0][2] * data[1][1] * data[2][3])
				+ (data[0][3] * data[1][2] * data[2][1])
				- (data[0][1] * data[1][2] * data[2][3])
				- (data[0][2] * data[1][3] * data[2][1])
				- (data[0][3] * data[1][1] * data[2][2]);

		adj.data[1][0] = (data[1][0] * data[2][3] * data[3][2])
				+ (data[1][2] * data[2][0] * data[3][3])
				+ (data[1][3] * data[2][2] * data[3][0])
				- (data[1][0] * data[2][2] * data[3][3])
				- (data[1][2] * data[2][3] * data[3][0])
				- (data[1][3] * data[2][0] * data[3][2]);
		adj.data[1][1] = (data[0][0] * data[2][2] * data[3][3])
				+ (data[0][2] * data[2][3] * data[3][0])
				+ (data[0][3] * data[2][0] * data[3][2])
				- (data[0][0] * data[2][3] * data[3][2])
				- (data[0][2] * data[2][0] * data[3][3])
				- (data[0][3] * data[2][2] * data[3][0]);
		adj.data[1][2] = (data[0][0] * data[1][3] * data[3][2])
				+ (data[0][2] * data[1][0] * data[3][3])
				+ (data[0][3] * data[1][2] * data[3][0])
				- (data[0][0] * data[1][2] * data[3][3])
				- (data[0][2] * data[1][0] * data[3][0])
				- (data[0][3] * data[1][0] * data[3][2]);
		adj.data[1][3] = (data[0][0] * data[1][2] * data[2][3])
				+ (data[0][2] * data[1][3] * data[2][0])
				+ (data[0][3] * data[1][0] * data[2][2])
				- (data[0][0] * data[1][3] * data[2][2])
				- (data[0][2] * data[1][0] * data[2][3])
				- (data[0][3] * data[1][2] * data[2][0]);

		adj.data[2][0] = (data[1][0] * data[2][1] * data[3][3])
				+ (data[1][1] * data[2][3] * data[3][0])
				+ (data[1][3] * data[2][0] * data[3][1])
				- (data[1][0] * data[2][3] * data[3][1])
				- (data[1][1] * data[2][0] * data[3][3])
				- (data[1][3] * data[2][1] * data[3][0]);
		adj.data[2][1] = (data[0][0] * data[2][3] * data[3][1])
				+ (data[0][1] * data[2][0] * data[3][3])
				+ (data[0][3] * data[2][1] * data[3][0])
				- (data[0][0] * data[2][1] * data[3][3])
				- (data[0][1] * data[2][3] * data[3][0])
				- (data[0][3] * data[2][0] * data[3][1]);
		adj.data[2][2] = (data[0][0] * data[1][1] * data[3][3])
				+ (data[0][1] * data[1][3] * data[3][0])
				+ (data[0][3] * data[1][0] * data[3][1])
				- (data[0][0] * data[1][3] * data[3][1])
				- (data[0][1] * data[1][0] * data[3][3])
				- (data[0][3] * data[1][1] * data[3][0]);
		adj.data[2][3] = (data[0][0] * data[1][3] * data[2][1])
				+ (data[0][1] * data[1][0] * data[2][3])
				+ (data[0][3] * data[1][1] * data[2][0])
				- (data[0][0] * data[1][1] * data[2][3])
				- (data[0][1] * data[1][3] * data[2][0])
				- (data[0][3] * data[1][0] * data[2][1]);

		adj.data[3][0] = (data[1][0] * data[2][2] * data[3][1])
				+ (data[1][1] * data[2][0] * data[3][2])
				+ (data[1][2] * data[2][1] * data[3][0])
				- (data[1][0] * data[2][1] * data[3][2])
				- (data[1][1] * data[2][2] * data[3][0])
				- (data[1][2] * data[2][0] * data[3][1]);
		adj.data[3][1] = (data[0][0] * data[2][1] * data[3][1])
				+ (data[0][1] * data[2][2] * data[3][0])
				+ (data[0][2] * data[2][0] * data[3][1])
				- (data[0][0] * data[2][2] * data[3][1])
				- (data[0][1] * data[2][0] * data[3][2])
				- (data[0][2] * data[2][1] * data[3][0]);
		adj.data[3][2] = (data[0][0] * data[1][2] * data[3][1])
				+ (data[0][1] * data[1][0] * data[3][2])
				+ (data[0][2] * data[1][1] * data[3][0])
				- (data[0][0] * data[1][1] * data[3][2])
				- (data[0][1] * data[1][2] * data[3][0])
				- (data[0][2] * data[1][0] * data[3][1]);
		adj.data[3][3] = (data[0][0] * data[1][1] * data[2][2])
				+ (data[0][1] * data[1][2] * data[2][0])
				+ (data[0][2] * data[1][0] * data[2][1])
				- (data[0][0] * data[1][2] * data[2][1])
				- (data[0][1] * data[1][0] * data[2][2])
				- (data[0][2] * data[1][1] * data[2][0]);
		return adj;
	}

	public TransMatrix inverse() {
		TransMatrix m = adjugate();
		m.multiply(1f / determinant());
		return m;
	}
}
