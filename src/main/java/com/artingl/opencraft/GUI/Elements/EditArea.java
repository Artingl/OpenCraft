package com.artingl.opencraft.GUI.Elements;

import com.artingl.opencraft.GL.Controls;
import com.artingl.opencraft.GUI.Screens.Screen;
import com.artingl.opencraft.Opencraft;
import com.artingl.opencraft.Rendering.TextureEngine;
import org.lwjgl.opengl.GL11;

public class EditArea extends Element
{

    public static int[] EDITAREA_TEXTURES = new int[]{
        TextureEngine.load("opencraft:gui/edit.png")
    };

    protected int bracketPosOffset;
    protected String text;
    protected final String hint;
    protected final Runnable onEdit;
    protected long bracketTimer;
    protected boolean bracketTimerWait;

    public EditArea(Screen screen, int id, float x, float y, String text, Runnable onEdit)
    {
        super(id, screen, x, y, 200, 20);
        this.hint = text;
        this.text = "";
        this.onEdit = onEdit;
        this.bracketTimer = System.currentTimeMillis();
        this.bracketTimerWait = false;
    }

    public void keyHandler(Controls.KeyInput keyInput) {
        if (selected) {
            if (keyInput.keyCode == Controls.Keys.KEY_BACKSPACE && this.text.length() - bracketPosOffset - 1 >= 0) {
                this.text = this.text.substring(0, this.text.length()-1-bracketPosOffset) + this.text.substring(this.text.length()-1-bracketPosOffset+1);
            }
            else if (keyInput.keyCode == Controls.Keys.KEY_DELETE && this.text.length() - 1 >= 0 && bracketPosOffset > 0) {
                this.text = this.text.substring(0, this.text.length()-1-bracketPosOffset+1) + this.text.substring(this.text.length()-1-bracketPosOffset+2);
                bracketPosOffset--;
            }
            else if (keyInput.keyCode == Controls.Keys.KEY_LEFT) {
                if (this.text.length() > bracketPosOffset)
                    bracketPosOffset++;
            }
            else if (keyInput.keyCode == Controls.Keys.KEY_RIGHT) {
                if (bracketPosOffset-1 >= 0)
                    bracketPosOffset--;
            }
            else {
                this.text = this.text.substring(0, this.text.length()-bracketPosOffset) + keyInput.character + this.text.substring(this.text.length()-bracketPosOffset);
                this.onEdit.run();
            }
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

        if (Opencraft.getFont().getTextWidth(text) > width - 20 && width > 0) {
            int i;
            int w = 0;

            for (i = text.length()-1; i >= 0; i--) {
                w += Opencraft.getFont().getTextWidth(String.valueOf(text.charAt(i)));
                if (w > width - 20) {
                    break;
                }
            }

            text = this.text.substring(i, text.length());
        }

        GL11.glPushMatrix();
        GL11.glTranslatef(this.x, this.y, 50);
        fillTexture(0, 0, width, height, id);
        Opencraft.getFont().drawShadow(text, 5, (int)(this.height / 2f) - 5, isHint ? 0xcccccc : 16777215);
        if (selected && this.bracketTimer + 500 > System.currentTimeMillis() && !this.bracketTimerWait)
        {
            if (bracketPosOffset == 0)
            {
                Opencraft.getFont().drawShadow("_", isHint ? 5 : 7 + Opencraft.getFont().getTextWidth(text), (int)(this.height / 2f) - 5, 0xFFFFFFF);
            }
            else {
                int offset = 0;

                for (int i = 0; i < bracketPosOffset; i++) {
                    offset += Opencraft.getFont().getTextWidth(String.valueOf(text.charAt(text.length()-1-i)));
                }

                int ix = Opencraft.getFont().getTextWidth(text) - offset + 4;
                int iy = (int)(this.height / 2f) - 5;

                fill(ix, iy-1, ix+1, iy+9, 0xFFe8e8e8);
            }
        }
        else if (selected) {
            if (!this.bracketTimerWait) {
                this.bracketTimer = System.currentTimeMillis();
                this.bracketTimerWait = true;
            }
            else if (this.bracketTimer + 500 < System.currentTimeMillis()) {
                this.bracketTimer = System.currentTimeMillis();
                this.bracketTimerWait = false;
            }
        }
        GL11.glPopMatrix();
        GL11.glLoadIdentity();
        GL11.glTranslatef(0, 0,-200);
    }

    @Override
    public void mouseHandler(Controls.MouseInput mouseInput) {
        super.mouseHandler(mouseInput);

        if (selected) {
            if (mouseInput.state == Controls.MouseState.UP) {
                if (mouseInput.button == Controls.Buttons.BUTTON_LEFT) {
                    int pos = (int) (normalizeX(mouseInput.mousePosition.x) - this.x);
                    int w = 0;
                    int j = 0;

                    for (int i = text.length()-1; i >= 0; i--) {
                        w += Opencraft.getFont().getTextWidth(String.valueOf(text.charAt(text.length()-1-i)));

                        if (w > pos) {
                            bracketPosOffset = text.length()-j;
                            return;
                        }

                        j++;
                    }

                }
            }
        }
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void destroy() {
    }

    protected void parentRender(int screenWidth, int screenHeight, int scale) {
        super.render(screenWidth, screenHeight, scale);
    }
}
