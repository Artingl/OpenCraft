package OpenCraft.Game.gui;

import OpenCraft.Game.Controls;
import OpenCraft.Game.Rendering.TextureManager;
import OpenCraft.Game.sound.Sound;
import OpenCraft.OpenCraft;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class Button extends GuiElement
{

    public static int[] BUTTON_TEXTURES;

    static {
        try {
            BUTTON_TEXTURES = new int[]{
                    TextureManager.load(ImageIO.read(new File("resources/gui/button.png"))),
                    TextureManager.load(ImageIO.read(new File("resources/gui/button_hover.png"))),
                    TextureManager.load(ImageIO.read(new File("resources/gui/button_disabled.png")))
            };
        } catch (IOException e) {}
    }

    public boolean enabled = true;
    private final int id;
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

        if (mouseHover(mx, my, x, screenHeight - (y + height), width, height) && enabled)
        {
            id = BUTTON_TEXTURES[1];

            if (Controls.getMouseKey(0) && !mouseClicked)
            {
                Sound.loadAndPlay("resources/sounds/gui/click1.wav");
                if (onClick != null) onClick.run();
                mouseClicked = true;
            }
            else if(!Controls.getMouseKey(0))
            {
                mouseClicked = false;
            }
        }
        else if (!enabled) id = BUTTON_TEXTURES[2];

        GL11.glPushMatrix();
        GL11.glTranslatef(this.x, this.y, 50);
        fillTexture(0, 0, width, height, id);
        OpenCraft.getFont().drawShadow(text, (int)((this.width / 2f) - OpenCraft.getFont().width(text) / 2f), (int)(this.height / 2f) - 5, 16777215);
        GL11.glPopMatrix();
        GL11.glLoadIdentity();
        GL11.glTranslatef(0, 0,-200);

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
