package com.artingl.opencraft.GUI;

import com.artingl.opencraft.GL.Controls;
import com.artingl.opencraft.OpenCraft;
import com.artingl.opencraft.Rendering.VerticesBuffer;
import org.lwjgl.opengl.GL11;

public class Element implements IGuiElement
{

    protected float x;
    protected float y;
    protected float width;
    protected float height;
    protected Screen screen;
    protected boolean isHighlighting;
    public boolean selected;

    private final int guiTickEvent;

    public Element(Screen screen, float x, float y, float width, float height)
    {
        this.guiTickEvent = OpenCraft.registerGuiTickEvent(this);

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.isHighlighting = false;
        this.selected = false;
        this.screen = screen;
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

    protected void fill(int x0, int y0, int x1, int y1, int col) {
        float a = (float)(col >> 24 & 255) / 255.0F;
        float r = (float)(col >> 16 & 255) / 255.0F;
        float g = (float)(col >> 8 & 255) / 255.0F;
        float b = (float)(col & 255) / 255.0F;
        VerticesBuffer t = VerticesBuffer.instance;
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glColor4f(r, g, b, a);
        t.begin();
        t.vertex((float)x0, (float)y1, 0.0F);
        t.vertex((float)x1, (float)y1, 0.0F);
        t.vertex((float)x1, (float)y0, 0.0F);
        t.vertex((float)x0, (float)y0, 0.0F);
        t.end();
        GL11.glDisable(3042);
    }

    public void selectHandler(int screenWidth, int screenHeight, int scale) {
        int mx = Controls.getMouseX() * scale / OpenCraft.getHeight();
        int my = Controls.getMouseY() * scale / OpenCraft.getHeight();

        if (mouseHover(mx, my, x, screenHeight - (y + height), width, height))
        {
            if (Controls.getMouseKey(0))
            {
                selected = true;
            }
        }
        else {
            if (Controls.getMouseKey(0)) selected = false;
        }
    }

    @Override
    public void render(int screenWidth, int screenHeight, int scale) {
        this.selectHandler(screenWidth, screenHeight, scale);

        if (this.isHighlighting) {
            fill((int) (x - 2), (int) (y - 2), (int) (x+width + 2), (int) (y+height + 2), 0xFFFFFFFF);
        }

    }

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

    public void keyHandler(Controls.KeyInput keyInput) {
    }

    public void clickHandler() {
    }

    public void destroy() {
        OpenCraft.unregisterGuiTickEvent(this.guiTickEvent);
    }
}
