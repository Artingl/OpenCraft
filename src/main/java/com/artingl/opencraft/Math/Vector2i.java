package com.artingl.opencraft.Math;

import com.artingl.opencraft.World.Entity.Entity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Vector2i
{

    public int x;
    public int y;

    public Vector2i(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    public Vector2i mult(Vector2i v, int c)
    {
        return new Vector2i(this.x + (v.x - this.x) * c, this.y + (v.y - this.y) * c);
    }


    public Vector2i addX(int i) {
        return new Vector2i(x + i, y);
    }

    public Vector2i addY(int i) {
        return new Vector2i(x, y + i);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Vector2i))
            return false;

        Vector2i v3 = (Vector2i) obj;
        return x ==  v3.x && y == v3.y;
    }

    @Override
    public String toString() {
        return "Vector2i{x=" + x + ", y=" + y + "}";
    }

    @Override
    public int hashCode() {
        return (x + ";" + y).hashCode();
    }

    public Vector2i add(Vector2i another) {
        return new Vector2i(x + another.x, y + another.y);
    }

    public void writeToStream(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeInt(x);
        dataOutputStream.writeInt(y);
    }

    public static Vector2i readFromStream(DataInputStream dataInputStream) throws IOException {
        Vector2i vec = new Vector2i(0, 0);

        vec.x = dataInputStream.readInt();
        vec.y = dataInputStream.readInt();

        return vec;
    }

    public Vector2i copy() {
        return new Vector2i(x, y);
    }

    public float distanceToEntity(Entity entity) {
        float xd = ((int)entity.getPosition().x) - x;
        float yd = ((int)entity.getPosition().y) - y;
        return (float) Math.sqrt(xd * xd + yd * yd);
    }
}
