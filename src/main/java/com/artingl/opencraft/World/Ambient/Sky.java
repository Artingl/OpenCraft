package com.artingl.opencraft.World.Ambient;

import com.artingl.opencraft.Control.Game.TextureEngine;
import com.artingl.opencraft.Control.Render.BufferRenderer;
import com.artingl.opencraft.Opencraft;
import org.lwjgl.opengl.GL11;

public class Sky {

    private int clouds = TextureEngine.load("opencraft:environment/clouds.png");
    private int cloudsSize = 1024;
    private float[] cloudPos = {-clouds, 0};

    private BufferRenderer t = BufferRenderer.getGlobalInstance();

    public void update() {
        // draw clouds
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        Opencraft.getShaderProgram().bindTexture(clouds);

        float y = 150;
        float x = 0;

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        t.begin();
        t.setTexCoord(0, 0);
        t.setVertexCoord(cloudsSize + cloudPos[0] + x, y, cloudsSize);
        t.setTexCoord(1, 0);
        t.setVertexCoord(-cloudsSize + cloudPos[0] + x, y, cloudsSize);
        t.setTexCoord(1, 1);
        t.setVertexCoord(-cloudsSize + cloudPos[0] + x, y, -cloudsSize);
        t.setTexCoord(0, 1);
        t.setVertexCoord(cloudsSize + cloudPos[0] + x, y, -cloudsSize);
        t.end();

        Opencraft.getShaderProgram().bindTexture(0);
    }

    public void tick() {
        cloudPos[0] += 0.1f;
//        cloudPos[1] += 0.1f;

        if (cloudPos[0] > cloudsSize) cloudPos[0] = -cloudsSize;
        if (cloudPos[1] > cloudsSize) cloudPos[1] = -cloudsSize;
    }
}
