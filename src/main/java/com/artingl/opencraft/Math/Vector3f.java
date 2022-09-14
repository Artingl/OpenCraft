package com.artingl.opencraft.Math;

import com.artingl.opencraft.World.Entity.Entity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Vector3f
{

    public float x;
    public float y;
    public float z;

    public Vector3f(float x, float y, float z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3f mult(Vector3f v, float c)
    {
        return new Vector3f(this.x + (v.x - this.x) * c, this.y + (v.y - this.y) * c, this.z + (v.z - this.z) * c);
    }

    public Vector3f addX(float i) {
        return new Vector3f(x + i, y, z);
    }

    public Vector3f addY(float i) {
        return new Vector3f(x, y + i, z);
    }

    public Vector3f addZ(float i) {
        return new Vector3f(x, y, z + i);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Vector3f))
            return false;

        Vector3f v3 = (Vector3f) obj;
        return x ==  v3.x && y == v3.y && z == v3.z;
    }

    @Override
    public String toString() {
        return "Vector3f{x=" + x + ", y=" + y + ", z=" + z + "}";
    }

    public Vector3f add(Vector3f another) {
        return new Vector3f(x + another.x, y + another.y, z + another.z);
    }

    @Override
    public int hashCode() {
        return (x + ";" + y + ";" + z).hashCode();
    }

    public void writeToStream(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeFloat(x);
        dataOutputStream.writeFloat(y);
        dataOutputStream.writeFloat(z);
    }

    public static Vector3f readFromStream(DataInputStream dataInputStream) throws IOException {
        Vector3f vec = new Vector3f(0, 0, 0);

        vec.x = dataInputStream.readFloat();
        vec.y = dataInputStream.readFloat();
        vec.z = dataInputStream.readFloat();

        return vec;
    }

    public Vector3f copy() {
        return new Vector3f(x, y, z);
    }

    public float distanceToEntity(Entity entity) {
        float xd = ((int)entity.getPosition().x) - x;
        float yd = ((int)entity.getPosition().y) - y;
        float zd = ((int)entity.getPosition().z) - z;
        return (float) Math.sqrt(xd * xd + yd * yd + zd * zd);
    }
}
