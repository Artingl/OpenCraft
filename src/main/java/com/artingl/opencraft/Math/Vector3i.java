package com.artingl.opencraft.Math;

import com.artingl.opencraft.World.Entity.Entity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Vector3i
{

    public int x;
    public int y;
    public int z;

    public Vector3i(int x, int y, int z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3i mult(Vector3i v, int c)
    {
        return new Vector3i(this.x + (v.x - this.x) * c, this.y + (v.y - this.y) * c, this.z + (v.z - this.z) * c);
    }

    public Vector3i addX(int i) {
        return new Vector3i(x + i, y, z);
    }

    public Vector3i addY(int i) {
        return new Vector3i(x, y + i, z);
    }

    public Vector3i addZ(int i) {
        return new Vector3i(x, y, z + i);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Vector3i))
            return false;

        Vector3i v3 = (Vector3i) obj;
        return x ==  v3.x && y == v3.y && z == v3.z;
    }

    @Override
    public String toString() {
        return "Vector3i{x=" + x + ", y=" + y + ", z=" + z + "}";
    }

    public Vector3i add(Vector3i another) {
        return new Vector3i(x + another.x, y + another.y, z + another.z);
    }

    @Override
    public int hashCode() {
        return (x + ";" + y + ";" + z).hashCode();
    }

    public void writeToStream(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeInt(x);
        dataOutputStream.writeInt(y);
        dataOutputStream.writeInt(z);
    }

    public static Vector3i readFromStream(DataInputStream dataInputStream) throws IOException {
        Vector3i vec = new Vector3i(0, 0, 0);

        vec.x = dataInputStream.readInt();
        vec.y = dataInputStream.readInt();
        vec.z = dataInputStream.readInt();

        return vec;
    }

    public Vector3i copy() {
        return new Vector3i(x, y, z);
    }

    public float distanceToEntity(Entity entity) {
        float xd = ((int)entity.getPosition().x) - x;
        float yd = ((int)entity.getPosition().y) - y;
        float zd = ((int)entity.getPosition().z) - z;
        return (float) Math.sqrt(xd * xd + yd * yd + zd * zd);
    }

}
