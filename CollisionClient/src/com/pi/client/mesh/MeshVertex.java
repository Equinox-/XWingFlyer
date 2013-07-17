package com.pi.client.mesh;

import com.pi.collision.math.Vector3D;

public class MeshVertex {
	private Vector3D position;
	private Vector3D normal;
	private float[] textureUV;
	private MeshMaterial matl;

	public MeshVertex(Vector3D pos, Vector3D normal, float[] tex,
			MeshMaterial matl) {
		this.position = pos;
		this.normal = normal;
		this.textureUV = tex;
		this.matl = matl;
	}

	public MeshMaterial getMaterial() {
		return matl;
	}

	public Vector3D getPosition() {
		return position;
	}

	public Vector3D getNormal() {
		return normal;
	}

	public float[] getTextureUV() {
		return textureUV;
	}
}
