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
    private boolean selected;
    private int backKeyClicks;
    private final int id;
    private String text;
    private final String hint;
    private final Runnable onEdit;
    private final int keyboardEvent;

    public EditArea(int id, float x, float y, String text, Runnable onEdit)
    {
        super(x, y, 0, 0);
        this.hint = text;
        this.text = "";
        this.id = id;
        this.onEdit = onEdit;
        this.backKeyTimer = 0;
        this.keyboardEvent = Controls.registerKeyboardHandler((key) -> {
            if (selected) {
                this.text += Character.toChars(key)[0];
                this.onEdit.run();
            }
        });

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

            if (Controls.isKeyDown(Controls.Keys.KEY_BACKSPACE) && this.backKeyTimer + (this.backKeyClicks < 2 ? 500 : 20) < System.currentTimeMillis() && text.length() != 0) {
                this.text = this.text.substring(0, this.text.length() - 1);
                this.backKeyTimer = System.currentTimeMillis();
                this.backKeyClicks++;
                this.onEdit.run();
            }
            else if (!Controls.isKeyDown(Controls.Keys.KEY_BACKSPACE)) {
                this.backKeyClicks = 0;
                this.backKeyTimer = 0;
            }

            if (OpenCraft.getFont().getTextWidth(text) > width - 20)
            {
                this.text = old;
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
        Controls.unregisterKeyboardHandler(this.keyboardEvent);
    }
}
