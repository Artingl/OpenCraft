package com.artingl.opencraft.GUI;

import com.artingl.opencraft.GL.Controls;
import com.artingl.opencraft.OpenCraft;
import org.lwjgl.opengl.GL11;

public class Element implements IGuiElement
{

    protected float x;
    protected float y;
    protected float width;
    protected float height;

    public Element(float x, float y, float width, float height)
    {
        OpenCraft.registerGuiTickEvent(this);

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    protected void fillTexture(float x0, float y0, float x1, float y1, int texture) {
        GL11.glEnable(3553);
        GL11.glBindTexture(3553, texture);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glBegin(7);
        GL11.glTexCoord2f(1, 0);
        GL11.glVertex2f((float)x1, (float)y0);
        GL11.glTexCoord2f(0, 0);
        GL11.glVertex2f((float)x0, (float)y0);
        GL11.glTexCoord2f(0, 1);
        GL11.glVertex2f((float)x0, (float)y1);
        GL11.glTexCoord2f(1, 1);
        GL11.glVertex2f((float)x1, (float)y1);
        GL11.glEnd();
        GL11.glDisable(3042);
        GL11.glDisable(3553);
    }

    protected boolean mouseHover(float mx, float my, float x, float y, float width, float height)
    {
        return mx >= x && mx <= x + width && my >= y && my <= y + height;
    }

    @Override
    public void render(int screenWidth, int screenHeight, int scale) { }

    @Override
    public void tick(int screenWidth, int screenHeight, int scale) { }

    public void setX(float x)
    {
        this.x = x;
    }

    public void setY(float y)
    {
        this.y = y;
    }

    public float getX()
    {
        return x;
    }

    public float getY()
    {
        return y;
    }

    public float getHeight()
    {
        return height;
    }

    public float getWidth()
    {
        return width;
    }


    public void destroy() {
    }
}
