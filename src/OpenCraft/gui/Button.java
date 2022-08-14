package OpenCraft.gui;

import OpenCraft.Controls;
import OpenCraft.Rendering.TextureEngine;
import OpenCraft.sound.Sound;
import OpenCraft.OpenCraft;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

import static org.lwjgl.opengl.GL11.*;

public class Button extends Element
{

    public static int[] BUTTON_TEXTURES = new int[]{
        TextureEngine.load("opencraft:gui/button.png"), TextureEngine.load("opencraft:gui/button_hover.png"), TextureEngine.load("opencraft:gui/button_disabled.png")
    };


    public boolean enabled = true;
    public boolean selected = false;

    private final int id;
    private long alive = -1;
    private String text;
    private Runnable onClick;
    private boolean mouseClicked;

    public Button(int id, float x, float y, String text, Runnable onClick)
    {
        super(x, y, 0, 0);
        this.text = text;
        this.onClick = onClick;
        this.id = id;
    }

    @Override
    public void tick(int screenWidth, int screenHeight, int scale)
    {
        this.width = (200f * Math.min(scale / 100, 1));
        this.height = (20f * Math.min(scale / 100, 1));
    }

    @Override
    public void render(int screenWidth, int screenHeight, int scale)
    {

        int mx = Controls.getMouseX() * scale / OpenCraft.getHeight();
        int my = Controls.getMouseY() * scale / OpenCraft.getHeight();

        int id = BUTTON_TEXTURES[0];
        if (selected) id = BUTTON_TEXTURES[1];

        if (mouseHover(mx, my, x, screenHeight - (y + height), width, height) && enabled)
        {
            if (alive == -1) alive = System.currentTimeMillis();
            id = BUTTON_TEXTURES[1];

            if (alive + 100 < System.currentTimeMillis())
            {
                if (Controls.getMouseKey(0) && !mouseClicked)
                {
                    mouseClicked = true;
                }
                else if(!Controls.getMouseKey(0) && mouseClicked)
                {
                    Sound.loadAndPlay("opencraft:sounds/gui/click1.wav");
                    if (onClick != null && mouseHover(mx, my, x, screenHeight - (y + height), width, height) && enabled) onClick.run();
                    mouseClicked = false;
                }
            }
        }
        else if (!enabled) id = BUTTON_TEXTURES[2];

        if(!Controls.getMouseKey(0) && mouseClicked) mouseClicked = false;

        GL11.glEnable(GL_BLEND);
        GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        GL11.glPushMatrix();
        GL11.glTranslatef(this.x, this.y, 50);
        fillTexture(0, 0, width, height, id);
        OpenCraft.getFont().drawShadow(text, (int)((this.width / 2f) - OpenCraft.getFont().getTextWidth(text) / 2f), (int)(this.height / 2f) - 5, 16777215);
        GL11.glPopMatrix();
        GL11.glLoadIdentity();
        GL11.glTranslatef(0, 0,-200);

        GL11.glDisable(GL_BLEND);

    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getId() {
        return id;
    }
}
