package com.artingl.opencraft.Math;

public class MathUtils {

    public static float calculateLength(float x0, float y0, float z0, float x1, float y1, float z1) {
        float xd = x0 - x1;
        float yd = y0 - y1;
        float zd = z0 - z1;
        return (float) Math.sqrt(xd * xd + yd * yd + zd * zd);
    }

    public static float calculateLength(Vector3f vec0, Vector3f vec1) {
        return calculateLength(vec0.x, vec0.y, vec0.z, vec1.x, vec1.y, vec1.z);
    }

    public static int calculateLength(Vector3i vec0, Vector3i vec1) {
        return Math.round(calculateLength(vec0.x, vec0.y, vec0.z, vec1.x, vec1.y, vec1.z));
    }

}
