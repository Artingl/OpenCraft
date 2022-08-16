package com.artingl.opencraft.GUI;

import com.artingl.opencraft.GL.Controls;
import com.artingl.opencraft.OpenCraft;
import com.artingl.opencraft.Rendering.TextureEngine;
import org.lwjgl.opengl.GL11;

public class EditArea extends Element
{

    public static int[] EDITAREA_TEXTURES = new int[]{
        TextureEngine.load("opencraft:gui/edit.png")
    };

    private long backKeyTimer;
    private int backKeyClicks;
    private final int id;
    private String text;
    private final String hint;
    private final Runnable onEdit;

    public EditArea(Screen screen, int id, float x, float y, String text, Runnable onEdit)
    {
        super(screen, x, y, 0, 0);
        this.hint = text;
        this.text = "";
        this.id = id;
        this.onEdit = onEdit;
        this.backKeyTimer = 0;
    }

    public void keyHandler(Controls.KeyInput keyInput) {
        if (selected) {
            if (keyInput.mod == Controls.Keys.KEY_BACKSPACE) {
                this.text = this.text.substring(0, this.text.length() - 1);
            }
            else {
                this.text += keyInput.character;
                this.onEdit.run();
            }
        }
    }

    public void selectHandler(int screenWidth, int screenHeight, int scale) {
        int mx = Controls.getMouseX() * scale / OpenCraft.getHeight();
        int my = Controls.getMouseY() * scale / OpenCraft.getHeight();

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
        super.render(screenWidth, screenHeight, scale);
        int id = EDITAREA_TEXTURES[0];

        boolean isHint = false;
        String text = this.text;

        if (text.isEmpty()) {
            text = this.hint;
            isHint = true;
        }

        if (OpenCraft.getFont().getTextWidth(text) > width - 20 && width > 0) {
            int i;
            int w = 0;

            for (i = text.length()-1; i >= 0; i--) {
                w += OpenCraft.getFont().getTextWidth(String.valueOf(text.charAt(i)));
                if (w > width - 20) {
                    break;
                }
            }

            text = this.text.substring(i, text.length());
        }

        GL11.glPushMatrix();
        GL11.glTranslatef(this.x, this.y, 50);
        fillTexture(0, 0, width, height, id);
        OpenCraft.getFont().drawShadow(text, 5, (int)(this.height / 2f) - 5, isHint ? 0xcccccc : 16777215);
        if (selected)
        {
            OpenCraft.getFont().drawShadow("_", isHint ? 5 : 7 + OpenCraft.getFont().getTextWidth(text), (int)(this.height / 2f) - 5, 16777215);
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

    public void destroy() {
    }
}
