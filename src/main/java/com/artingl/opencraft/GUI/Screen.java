package com.artingl.opencraft.GUI;

import com.artingl.opencraft.GL.Controls;
import com.artingl.opencraft.OpenCraft;
import com.artingl.opencraft.Rendering.TextureEngine;
import com.artingl.opencraft.Rendering.VerticesBuffer;
import com.artingl.opencraft.Resources.Lang;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Objects;

public class Screen
{
    private static int background_id = TextureEngine.load("opencraft:gui/dirt.png");

    private HashMap<Integer, Element> elements;

    protected float width;
    protected float height;
    protected String title;
    private int keyboardEvent = -1;
    public final String screenId;

    public Screen(int width, int height, String screenId)
    {
        elements = new HashMap<>();

        this.width = width;
        this.height = height;
        this.title = Lang.getLanguageString("opencraft:gui.screen." + screenId);
        this.screenId = screenId;
    }

    public void resize(int width, int height)
    {
        this.width = width;
        this.height = height;
    }

    public void init()
    {
        elements = new HashMap<>();

        if (this.keyboardEvent == -1) {
            this.registerKeyboardHandler();
        }
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

    public void setLoadingScreen(String s) {
        GL11.glTranslatef(0, 0, 50);
        drawBackground(VerticesBuffer.instance, OpenCraft.getScreenScaledWidth(), OpenCraft.getScreenScaledHeight(), 0x808080);
        OpenCraft.getFont().drawShadow(s, (OpenCraft.getScreenScaledWidth() - OpenCraft.getFont().getTextWidth(s)) / 2, (int) (OpenCraft.getScreenScaledHeight() / 2f) - 5, 0xFFFFFF);
        OpenCraft.getDisplay().swapBuffers();
    }

    protected void drawBackground(VerticesBuffer t, int screenWidth, int screenHeight, int clr)
    {
        GL11.glEnable(3553);
        GL11.glBindTexture(3553, background_id);
        t.begin();
        t.color(clr);
        float s = 32.0F;
        t.vertexUV(0.0F, (float)screenHeight, 0.0F, 0.0F, (float)screenHeight / s);
        t.vertexUV((float)screenWidth, (float)screenHeight, 0.0F, (float)screenWidth / s, (float)screenHeight / s);
        t.vertexUV((float)screenWidth, 0.0F, 0.0F, (float)screenWidth / s, 0.0F);
        t.vertexUV(0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        t.end();
        GL11.glEnable(3553);
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

    protected void fillGradient(int x0, int y0, int x1, int y1, int col1, int col2, int a) {
        float a1 = (float)(col1 >> 24 & 255) / 255.0F;
        float r1 = (float)(col1 >> 16 & 255) / 255.0F;
        float g1 = (float)(col1 >> 8 & 255) / 255.0F;
        float b1 = (float)(col1 & 255) / 255.0F;
        float a2 = (float)(col2 >> 24 & 255) / 255.0F;
        float r2 = (float)(col2 >> 16 & 255) / 255.0F;
        float g2 = (float)(col2 >> 8 & 255) / 255.0F;
        float b2 = (float)(col2 & 255) / 255.0F;
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glBegin(7);
        GL11.glColor4f(r1, g1, b1, a / 255f);
        GL11.glVertex3f((float)x1, (float)y0, 0f);
        GL11.glVertex3f((float)x0, (float)y0, 0f);
        GL11.glColor4f(r2, g2, b2, a / 255f);
        GL11.glVertex3f((float)x0, (float)y1, 0f);
        GL11.glVertex3f((float)x1, (float)y1, 0f);
        GL11.glEnd();
        GL11.glDisable(3042);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
    }

    protected void drawCenteredString(String str, int x, int y, int color) {
        Font font = OpenCraft.getFont();
        font.drawShadow(str, x - font.getTextWidth(str) / 2, y, color);
    }

    protected void drawString(String str, int x, int y, int color) {
        Font font = OpenCraft.getFont();
        font.drawShadow(str, x, y, color);
    }

    protected void keyPressed(Controls.KeyInput keyInput) {
        elements.forEach((id, element) -> {
            if (element != null) element.keyHandler(keyInput);
        });

        if (keyInput.keyCode == Controls.Keys.KEY_TAB) {
            var values = new Object() {
                boolean nextHighlight = false;
                boolean skipEverything = false;
            };

            elements.forEach((id, element) -> {
                if (values.skipEverything)
                    return;

                if (element != null) {
                    if (values.nextHighlight) {
                        element.isHighlighting = true;
                        element.selected = true;
                        values.skipEverything = true;
                        return;
                    }

                    element.selected = false;
                    if (element.isHighlighting) {
                        values.nextHighlight = true;
                        element.isHighlighting = false;
                    }
                }
            });

            if (!values.skipEverything && elements.size() > 0) {
                elements.get(0).isHighlighting = true;
                elements.get(0).selected = true;
            }
        }
        else if (keyInput.keyCode == Controls.Keys.KEY_ENTER) {
            elements.forEach((id, element) -> {
                if (element.isHighlighting)
                {
                    element.clickHandler();
                }
            });
        }
    }

    protected void mouseClicked(int x, int y, int button) {
    }

    public void registerKeyboardHandler() {
        this.keyboardEvent = Controls.registerKeyboardHandler(this, this::keyPressed);
    }

    protected int addElement(Element e)
    {
        int id = elements.size();
        elements.put(id, e);
        return id;
    }

    protected HashMap<Integer, Element> getElements()
    {
        return elements;
    }

    public void render(int screenWidth, int screenHeight, int scale)
    {
        OpenCraft.getFont().drawShadow(title, (screenWidth - OpenCraft.getFont().getTextWidth(title)) / 2, 20, 0xFFFFFF);
        elements.forEach((id, element) -> {
            if (element != null)
                element.render(screenWidth, screenHeight, scale);
        });
    }

    public void destroy() {
        Controls.unregisterKeyboardHandler(this.keyboardEvent);
        this.keyboardEvent = -1;
        elements.forEach((id, element) -> {
            if (element != null) {
                element.isHighlighting = false;
                element.selected = false;
                element.destroy();
            }
        });
    }

    @Override
    public boolean equals(Object obj) {
        return Objects.equals(((Screen) obj).screenId, this.screenId);
    }
}
