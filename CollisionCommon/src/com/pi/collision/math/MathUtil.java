package com.pi.collision.math;

public class MathUtil {
	/**
	 * [distance from segment, distance on line, distance from infinite line]
	 */
	public static float[] getRelationToLine(Vector3D point, Vector3D lineA,
			Vector3D lineB) {
		Vector3D lineNormal = lineB.clone().subtract(lineA);
		Vector3D pointNormal = point.clone().subtract(lineA);
		float lineMag = lineNormal.magnitude();
		float pointMag = pointNormal.magnitude();
		float baseLen = Vector3D.dotProduct(lineNormal, pointNormal) / lineMag;
		float angle = (float) Math.acos(baseLen / pointMag);
		float thickness = (float) (Math.sin(angle) * pointMag);
		if (baseLen > lineMag) {
			return new float[] { lineB.dist(point), baseLen, thickness };
		} else if (angle > Math.PI / 2) {
			return new float[] { pointMag, baseLen, thickness };
		} else {
			return new float[] { thickness, baseLen, thickness };
		}
	}
}
