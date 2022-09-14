package com.artingl.opencraft.GUI.Elements;

import com.artingl.opencraft.GUI.Screens.Screen;
import com.artingl.opencraft.Opencraft;
import org.lwjgl.opengl.GL11;

public class ChatEditArea extends EditArea {
    public ChatEditArea(Screen screen, float x, float y, String text, Runnable onEdit) {
        super(screen, x, y, text, onEdit);
        selected = true;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public void render(int screenWidth, int screenHeight, int scale)
    {
        super.parentRender(screenWidth, screenHeight, scale);

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
        fill(0, 0, (int)width, (int)height, 0x55000000);
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

}
