package com.pi.client.mesh;

import java.io.File;
import java.io.FileInputStream;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

public class MeshMaterial {
	public static enum Illumination {
		C1A0("Color on and Ambient off"), C1A1("Color on and Ambient on"), H1(
				"Highlight on"), RRa1("Reflection on and Ray trace on"), G1RRa1(
				"Transparency: Glass on, Reflection: Ray trace on"), RF1Ra1(
				"Reflection: Fresnel on and Ray trace on"), RacRecFres0Ra1(
				"Transparency: Refraction on, Reflection: Fresnel off and Ray trace on"), RacRecFres1Ra1(
				"Transparency: Refraction on, Reflection: Fresnel on and Ray trace on"), RecRa0(
				"Reflection on and Ray trace off"), G1Ra0(
				"Transparency: Glass on, Reflection: Ray trace off"), Shads(
				"Casts shadows onto invisible surfaces;");
		private final String desc;

		private Illumination(final String d) {
			this.desc = d;
		}

		public String getDescription() {
			return desc;
		}
	}

	FloatBufferColor ambient = new FloatBufferColor(1f, 1f, 1f);
	FloatBufferColor diffuse = new FloatBufferColor(1f, 1f, 1f);
	FloatBufferColor specular = new FloatBufferColor(1f, 1f, 1f);
	float specularCoefficient = 1f;
	float transparency;
	Illumination illum;
	File diffuseMap;
	Texture diffuseTexture;

	public void bindGL() {
		/*GL11.glMaterial(GL11.GL_FRONT_AND_BACK, GL11.GL_SPECULAR,
				specular.getBuffer());
		GL11.glMaterial(GL11.GL_FRONT_AND_BACK, GL11.GL_AMBIENT,
				ambient.getBuffer());
		GL11.glMaterial(GL11.GL_FRONT_AND_BACK, GL11.GL_DIFFUSE,
				diffuse.getBuffer());
		GL11.glMaterialf(GL11.GL_FRONT_AND_BACK, GL11.GL_SPECULAR, specularCoefficient);
		if (diffuseMap != null) {
			if (diffuseTexture == null) {
				String ext = diffuseMap.getAbsolutePath().substring(
						diffuseMap.getAbsolutePath().lastIndexOf('.') + 1);
				try {
					FileInputStream in = new FileInputStream(diffuseMap);
					diffuseTexture = TextureLoader.getTexture(ext, in);
					in.close();
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			}
			if (diffuseTexture != null) {
				diffuseTexture.bind();
			}
		}*/
	}
}
