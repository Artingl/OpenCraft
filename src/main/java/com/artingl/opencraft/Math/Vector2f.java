package com.artingl.opencraft.Math;

import com.artingl.opencraft.World.Entity.Entity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Vector2f
{

    public float x;
    public float y;

    public Vector2f(float x, float y)
    {
        this.x = x;
        this.y = y;
    }

    public Vector2f mult(Vector2f v, float c)
    {
        return new Vector2f(this.x + (v.x - this.x) * c, this.y + (v.y - this.y) * c);
    }

    public Vector2f addX(float i) {
        return new Vector2f(x + i, y);
    }

    public Vector2f addY(float i) {
        return new Vector2f(x, y + i);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Vector2f))
            return false;

        Vector2f v3 = (Vector2f) obj;
        return x ==  v3.x && y == v3.y;
    }

    @Override
    public int hashCode() {
        return (x + ";" + y).hashCode();
    }

    @Override
    public String toString() {
        return "Vector2f{x=" + x + ", y=" + y + "}";
    }

    public Vector2f add(Vector2f another) {
        return new Vector2f(x + another.x, y + another.y);
    }

    public void writeToStream(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeFloat(x);
        dataOutputStream.writeFloat(y);
    }

    public static Vector2f readFromStream(DataInputStream dataInputStream) throws IOException {
        Vector2f vec = new Vector2f(0, 0);

        vec.x = dataInputStream.readFloat();
        vec.y = dataInputStream.readFloat();

        return vec;
    }

    public Vector2f copy() {
        return new Vector2f(x, y);
    }

    public float distanceToEntity(Entity entity) {
        float xd = ((int)entity.getPosition().x) - x;
        float yd = ((int)entity.getPosition().y) - y;
        return (float) Math.sqrt(xd * xd + yd * yd);
    }
}
