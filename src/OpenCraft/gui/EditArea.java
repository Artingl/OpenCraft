package OpenCraft.gui;

import OpenCraft.Controls;
import OpenCraft.Rendering.TextureEngine;
import OpenCraft.OpenCraft;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class EditArea extends Element
{

    public static int[] EDITAREA_TEXTURES;

    static {
        try {
            EDITAREA_TEXTURES = new int[]{
                    TextureEngine.load(ImageIO.read(new File("resources/gui/edit.png")))
            };
        } catch (IOException e) {}
    }

    private boolean spaceKeyUp = true;
    private boolean backKeyUp = true;
    private HashMap<String, Boolean> keyDown;
    private boolean selected = false;
    private final int id;
    private String text;
    private String hint;
    private boolean mouseClicked;
    private Runnable onEdit;

    public EditArea(int id, float x, float y, String text, Runnable onEdit)
    {
        super(x, y, 0, 0);
        keyDown = new HashMap<>();
        this.hint = text;
        this.text = "";
        this.id = id;
        this.onEdit = onEdit;
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
        if (selected)
        {
            String old = text;
            if (Keyboard.isKeyDown(Keyboard.KEY_BACK) && text.length() != 0 && backKeyUp)
            {
                this.text = this.text.substring(0, this.text.length() - 1);
                backKeyUp = false;
                onEdit.run();
            }
            else if (!Keyboard.isKeyDown(Keyboard.KEY_BACK)) backKeyUp = true;
            if (Keyboard.isKeyDown(Keyboard.KEY_SPACE) && spaceKeyUp)
            {
                this.text += " ";
                spaceKeyUp = false;
                onEdit.run();
            }
            else if (!Keyboard.isKeyDown(Keyboard.KEY_SPACE)) spaceKeyUp = true;

            for(int i = 32; i <= 126; i++)
            {
                String s = String.valueOf((char)i);

                if (!keyDown.containsKey(String.valueOf((char)i)) || (Keyboard.isKeyDown(Keyboard.getKeyIndex(s)) && !keyDown.get(String.valueOf((char)i))))
                {
                    if (!Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_CAPS_LOCK)) s = s.toLowerCase();

                    text += s;
                    keyDown.put(String.valueOf((char)i), true);
                    onEdit.run();
                }
                else if (!Keyboard.isKeyDown(Keyboard.getKeyIndex(s)))
                    keyDown.put(String.valueOf((char)i), false);
            }
            if (OpenCraft.getFont().getTextWidth(text) > width - 20)
            {
                text = old;
            }

        }

        int mx = Controls.getMouseX() * scale / OpenCraft.getHeight();
        int my = Controls.getMouseY() * scale / OpenCraft.getHeight();

        int id = EDITAREA_TEXTURES[0];

        if (mouseHover(mx, my, x, screenHeight - (y + height), width, height))
        {
            if (Controls.getMouseKey(0))
            {
                onEdit.run();
                selected = true;
            }
        }
        else {
            if (Controls.getMouseKey(0)) selected = false;
        }

        boolean isHint = false;
        String text = this.text;

        if (text.isEmpty()) {
            text = this.hint;
            isHint = true;
        }

        GL11.glPushMatrix();
        GL11.glTranslatef(this.x, this.y, 50);
        fillTexture(0, 0, width, height, id);
        OpenCraft.getFont().drawShadow(text, (int)5, (int)(this.height / 2f) - 5, isHint ? 0xcccccc : 16777215);
        if (selected)
        {
            OpenCraft.getFont().drawShadow("_", (int)(isHint ? 5 : 7 + OpenCraft.getFont().getTextWidth(text)), (int)(this.height / 2f) - 5, 16777215);
        }
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
