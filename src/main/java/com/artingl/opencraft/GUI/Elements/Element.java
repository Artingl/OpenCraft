package com.artingl.opencraft.GUI.Elements;

import com.artingl.opencraft.GL.Controls;
import com.artingl.opencraft.GUI.Screens.Screen;
import com.artingl.opencraft.Opencraft;
import com.artingl.opencraft.Rendering.VerticesBuffer;
import org.lwjgl.opengl.GL11;

public class Element
{

    protected float x;
    protected float y;
    protected float width;
    protected float height;
    protected Screen screen;
    public boolean isHighlighting;
    public boolean selected;

    protected int id;

    public Element(){}

    public Element(int id, Screen screen, float x, float y, float width, float height)
    {
        this.id = id;
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

    protected int normalizeX(int x) {
        return x * Opencraft.getGuiScaleValue() / Opencraft.getHeight();
    }

    protected int normalizeY(int y) {
        return y * Opencraft.getGuiScaleValue() / Opencraft.getHeight();
    }

    public boolean isMouseHover() {
        int mx = Controls.getMouseX() * Opencraft.getGuiScaleValue() / Opencraft.getHeight();
        int my = Controls.getMouseY() * Opencraft.getGuiScaleValue() / Opencraft.getHeight();
        return mouseHover(mx, my, x, ((float) Opencraft.getHeight() * Opencraft.getGuiScaleValue() / Opencraft.getHeight()) - (y + height), width, height);
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
        VerticesBuffer t = VerticesBuffer.getGlobalInstance();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(r, g, b, a);
        t.begin();
        t.vertex((float)x0, (float)y1, 0.0F);
        t.vertex((float)x1, (float)y1, 0.0F);
        t.vertex((float)x1, (float)y0, 0.0F);
        t.vertex((float)x0, (float)y0, 0.0F);
        t.end();
        GL11.glDisable(GL11.GL_BLEND);
    }

    public void render(int screenWidth, int screenHeight, int scale) {
        if (this.isHighlighting) {
            fill((int) (x - 2), (int) (y - 2), (int) (x+width + 2), (int) (y+height + 2), 0xFFFFFFFF);
        }

    }

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


    public void setWidth(int w) {
        width = w;
    }

    public void setHeight(int h) {
        height = h;
    }

    public void keyHandler(Controls.KeyInput keyInput) {
    }

    public void mouseHandler(Controls.MouseInput mouseInput) {
    }

    public void clickHandler() {
    }

    public void destroy() {
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        return ((Element)obj).getId() == id;
    }

    public int getTitleColor() {
        return !isMouseHover() ? 0xffffff : 0xeaed8c;
    }
}
