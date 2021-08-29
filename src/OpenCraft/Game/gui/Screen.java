package OpenCraft.Game.gui;

import OpenCraft.Game.Rendering.TextureManager;
import OpenCraft.Game.Rendering.VerticesBuffer;
import OpenCraft.OpenCraft;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class Screen
{

    private static int background_id;

    static
    {
        try {
            background_id = TextureManager.load(ImageIO.read(new File("resources/gui/dirt.png")));
        } catch (IOException e) {}
    }

    private HashMap<Integer, GuiElement> elements;

    protected int width;
    protected int height;

    public Screen(int width, int height)
    {
        elements = new HashMap<>();

        this.width = width;
        this.height = height;
    }

    public void resize(int width, int height)
    {
        this.width = width;
        this.height = height;
    }

    public void init()
    {
        elements = new HashMap<>();
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

    protected void setLoadingScreen(String s) {
        GL11.glTranslatef(0, 0, 50);
        drawBackground(VerticesBuffer.instance, OpenCraft.getScreenScaledWidth(), OpenCraft.getScreenScaledHeight(), 0x808080);
        OpenCraft.getFont().drawShadow(s, (OpenCraft.getScreenScaledWidth() - OpenCraft.getFont().width(s)) / 2, (int) (OpenCraft.getScreenScaledHeight() / 2f) - 5, 0xFFFFFF);
        Display.update();
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

    protected void fillGradient(int x0, int y0, int x1, int y1, int col1, int col2) {
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
        GL11.glColor4f(r1, g1, b1, a1);
        GL11.glVertex2f((float)x1, (float)y0);
        GL11.glVertex2f((float)x0, (float)y0);
        GL11.glColor4f(r2, g2, b2, a2);
        GL11.glVertex2f((float)x0, (float)y1);
        GL11.glVertex2f((float)x1, (float)y1);
        GL11.glEnd();
        GL11.glDisable(3042);
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

    protected void drawCenteredString(String str, int x, int y, int color) {
        Font font = OpenCraft.getFont();
        font.drawShadow(str, x - font.width(str) / 2, y, color);
    }

    protected void drawString(String str, int x, int y, int color) {
        Font font = OpenCraft.getFont();
        font.drawShadow(str, x, y, color);
    }

    protected void updateEvents() {
        while(Mouse.next()) {
            if (Mouse.getEventButtonState()) {
                int xm = Mouse.getEventX() * this.width / OpenCraft.getWidth();
                int ym = this.height - Mouse.getEventY() * this.height / OpenCraft.getHeight() - 1;
                this.mouseClicked(xm, ym, Mouse.getEventButton());
            }
        }

        while(Keyboard.next()) {
            if (Keyboard.getEventKeyState()) {
                this.keyPressed(Keyboard.getEventCharacter(), Keyboard.getEventKey());
            }
        }

    }

    protected void keyPressed(char eventCharacter, int eventKey) {
    }

    protected void mouseClicked(int x, int y, int button) {
    }

    protected int addElement(GuiElement e)
    {
        int id = elements.size();
        elements.put(id, e);
        return id;
    }

    protected HashMap<Integer, GuiElement> getElements()
    {
        return elements;
    }

    public void render(int screenWidth, int screenHeight, int scale)
    {
        elements.forEach((id, element) -> {
            if (element != null) element.render(screenWidth, screenHeight, scale);
        });

    }

}
