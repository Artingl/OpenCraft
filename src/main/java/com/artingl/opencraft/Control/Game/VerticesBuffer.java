package com.artingl.opencraft.Control.Game;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.nio.FloatBuffer;

public class VerticesBuffer
{
    public static int MAX_VALUE = 65535;

    private final FloatBuffer buffer = BufferUtils.createFloatBuffer(MAX_VALUE);
    private final float[] array = new float[MAX_VALUE];
    private int vertices = 0;
    private float u;
    private float v;
    private float r;
    private float g;
    private float b;
    private boolean hasColor = false;
    private boolean hasTexture = false;
    private int len = 3;
    private int p = 0;
    private boolean noColor = false;

    private final static VerticesBuffer instance = new VerticesBuffer();

    public static VerticesBuffer getGlobalInstance() {
        return instance;
    }

    public static VerticesBuffer getInstance() {
        return new VerticesBuffer();
    }

    private VerticesBuffer() {
    }

    public void end() {
        if (this.vertices > 0) {
            this.buffer.clear();
            this.buffer.put(this.array, 0, this.p);
            this.buffer.flip();
            if (this.hasTexture && this.hasColor) {
                GL11.glInterleavedArrays(10794, 0, this.buffer);
            } else if (this.hasTexture) {
                GL11.glInterleavedArrays(10791, 0, this.buffer);
            } else if (this.hasColor) {
                GL11.glInterleavedArrays(10788, 0, this.buffer);
            } else {
                GL11.glInterleavedArrays(10785, 0, this.buffer);
            }

            GL11.glEnableClientState(32884);
            if (this.hasTexture) {
                GL11.glEnableClientState(32888);
            }

            if (this.hasColor) {
                GL11.glEnableClientState(32886);
            }

            GL11.glDrawArrays(7, 0, this.vertices);
            GL11.glDisableClientState(32884);
            if (this.hasTexture) {
                GL11.glDisableClientState(32888);
            }

            if (this.hasColor) {
                GL11.glDisableClientState(32886);
            }
        }

        this.clear();
    }

    public void clear() {
        this.vertices = 0;
        this.buffer.clear();
        this.p = 0;
    }

    public void begin() {
        this.clear();
        this.hasColor = false;
        this.hasTexture = false;
        this.noColor = false;
    }

    public void tex(float u, float v) {
        if (!this.hasTexture) {
            this.len += 2;
        }

        this.hasTexture = true;
        this.u = u;
        this.v = v;
    }

    public void color(int r, int g, int b) {
        this.color((byte)r, (byte)g, (byte)b);
    }

    public void color(byte r, byte g, byte b) {
        if (!this.noColor) {
            if (!this.hasColor) {
                this.len += 3;
            }

            this.hasColor = true;
            this.r = (float)(r & 255) / 255.0F;
            this.g = (float)(g & 255) / 255.0F;
            this.b = (float)(b & 255) / 255.0F;
        }
    }

    public void vertexUV(float x, float y, float z, float u, float v) {
        this.tex(u, v);
        this.vertex(x, y, z);
    }

    public void vertex(float x, float y, float z) {
        if (this.hasTexture) {
            this.array[this.p++] = this.u;
            this.array[this.p++] = this.v;
        }

        if (this.hasColor) {
            this.array[this.p++] = this.r;
            this.array[this.p++] = this.g;
            this.array[this.p++] = this.b;
        }

        this.array[this.p++] = x;
        this.array[this.p++] = y;
        this.array[this.p++] = z;
        ++this.vertices;
        if (this.vertices % 4 == 0 && this.p >= MAX_VALUE - this.len * 4) {
            this.end();
        }

    }

    public void setVertexCoord(float x, float y, float z)
    {
        vertex(x, y, z);
    }

    public void setTexCoord(float u, float v)
    {
        tex(u, v);
    }

    public void setColori(int r, int g, int b)
    {
        this.color(r, g, b);
    }

    public void setColorf(float r, float g, float b)
    {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public void color(int c) {
        int r = c >> 16 & 255;
        int g = c >> 8 & 255;
        int b = c & 255;
        this.color(r, g, b);
    }

    public void noColor() {
        this.noColor = true;
    }
}