package com.pi.client.mesh;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.pi.client.mesh.MeshMaterial.Illumination;
import com.pi.collision.math.Vector3D;

public class WavefrontLoader {
	public static MeshObject loadWavefrontObject(File f) throws IOException {
		Map<String, MeshMaterial> matlLibrary = new HashMap<String, MeshMaterial>();

		List<Mesh> objects = new ArrayList<Mesh>();

		List<Vector3D> positions = new ArrayList<Vector3D>();
		List<Vector3D> normals = new ArrayList<Vector3D>();
		List<float[]> textures = new ArrayList<float[]>();
		List<MeshVertex> verticies = new ArrayList<MeshVertex>();
		List<Integer> indicies = new ArrayList<Integer>();

		BufferedReader r = new BufferedReader(new FileReader(f));
		int polygonSize = -1;

		MeshMaterial material = null;
		String objectName = null;
		while (true) {
			String line = r.readLine();
			if (line == null) {
				break;
			}
			String[] parts = line.split(" ");
			try {
				if (parts[0].equalsIgnoreCase("v") && parts.length >= 4) {
					positions.add(new Vector3D(Float.valueOf(parts[1]) * 2 + 1,
							Float.valueOf(parts[2]) * 2 - 3.2f, Float
									.valueOf(parts[3]) * 2));
				} else if (parts[0].equalsIgnoreCase("o")
						|| parts[0].equalsIgnoreCase("g")) {
					if (objectName != null) {
						objects.add(new Mesh(objectName, verticies, indicies,
								polygonSize));
					}
					polygonSize = -1;
					objectName = parts[1];
					// positions = new ArrayList<Vector3D>();
					// normals = new ArrayList<Vector3D>();
					// textures = new ArrayList<float[]>();
					verticies = new ArrayList<MeshVertex>();
					indicies = new ArrayList<Integer>();
				} else if (parts[0].equalsIgnoreCase("vn") && parts.length >= 4) {
					normals.add(new Vector3D(Float.valueOf(parts[1]), Float
							.valueOf(parts[2]), Float.valueOf(parts[3])));
				} else if (parts[0].equalsIgnoreCase("vt") && parts.length >= 3) {
					textures.add(new float[] { Float.valueOf(parts[1]),
							Float.valueOf(parts[2]) });
				} else if (parts[0].equalsIgnoreCase("f")) {
					if (polygonSize == -1) {
						polygonSize = parts.length - 1;
					}
					if (polygonSize != parts.length - 1) {
						throw new RuntimeException(
								"Inconsistent polygon size: "
										+ (parts.length - 1) + " expected "
										+ polygonSize);
					}
					for (int i = 1; i < parts.length; i++) {
						String[] vParts = parts[i].split("\\/");
						int vI = Integer.valueOf(vParts[0]);
						int nI = Integer.valueOf(vParts[2]);
						int tI = Integer.valueOf(vParts[1]);
						verticies.add(new MeshVertex(positions
								.get(vI < 0 ? positions.size() + vI : vI - 1),
								normals.get(nI < 0 ? normals.size() + nI
										: nI - 1), textures
										.get(tI < 0 ? textures.size() + tI
												: tI - 1), material));
						indicies.add(verticies.size() - 1);
					}
				} else if (parts[0].equalsIgnoreCase("mtllib")) {
					String fileName = "";
					for (int i = 1; i < parts.length; i++) {
						if (i != 1) {
							fileName += " ";
						}
						fileName += parts[i];
					}
					Map<String, MeshMaterial> mAdd = loadWavefrontMaterials(new File(
							f.getParentFile(), fileName));
					for (Entry<String, MeshMaterial> m : mAdd.entrySet()) {
						matlLibrary.put(m.getKey(), m.getValue());
					}
				} else if (parts[0].equalsIgnoreCase("usemtl")) {
					material = matlLibrary.get(parts[1]);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (objectName != null) {
			objects.add(new Mesh(objectName, verticies, indicies, polygonSize));
		}
		r.close();
		return new MeshObject(objects);
	}

	public static Map<String, MeshMaterial> loadWavefrontMaterials(File f)
			throws IOException {
		Map<String, MeshMaterial> library = new HashMap<String, MeshMaterial>();
		BufferedReader r = new BufferedReader(new FileReader(f));
		MeshMaterial active = null;
		String activeName = null;
		while (true) {
			String line = r.readLine();
			if (line == null) {
				break;
			}
			String[] parts = line.split(" ");
			if (parts[0].equalsIgnoreCase("newmtl")) {
				if (activeName != null && active != null) {
					library.put(activeName, active);
				}
				activeName = parts[1];
				active = new MeshMaterial();
			} else if (parts[0].equalsIgnoreCase("Ns")) {
				active.specularCoefficient = Float.valueOf(parts[1]);
			} else if (parts[0].equalsIgnoreCase("Ka")) {
				active.ambient.set(Float.valueOf(parts[1]),
						Float.valueOf(parts[2]), Float.valueOf(parts[3]));
			} else if (parts[0].equalsIgnoreCase("Kd")) {
				active.diffuse.set(Float.valueOf(parts[1]),
						Float.valueOf(parts[2]), Float.valueOf(parts[3]));
			} else if (parts[0].equalsIgnoreCase("Ks")) {
				active.specular.set(Float.valueOf(parts[1]),
						Float.valueOf(parts[2]), Float.valueOf(parts[3]));
			} else if (parts[0].equalsIgnoreCase("Ni")) {

			} else if (parts[0].equalsIgnoreCase("d")
					|| parts[0].equalsIgnoreCase("Tr")) {
				active.transparency = Float.valueOf(parts[1]);
			} else if (parts[0].equalsIgnoreCase("illum")) {
				active.illum = Illumination.values()[Integer.valueOf(parts[1])];
			} else if (parts[0].equalsIgnoreCase("map_Kd")) {
				String fileName = "";
				for (int i = 1; i < parts.length; i++) {
					if (i != 1) {
						fileName += " ";
					}
					fileName += parts[i];
				}
				active.diffuseMap = new File(f.getParentFile(), fileName);
			}
		}
		r.close();
		return library;
	}
}
