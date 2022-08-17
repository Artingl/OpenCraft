package com.artingl.opencraft.GUI;

import com.artingl.opencraft.OpenCraft;
import org.lwjgl.opengl.GL11;

public class ChatEditArea extends EditArea {
    public ChatEditArea(Screen screen, int id, float x, float y, String text, Runnable onEdit) {
        super(screen, id, x, y, text, onEdit);
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
        fill(0, 0, (int)width, (int)height, 0x55000000);
        OpenCraft.getFont().drawShadow(text, 5, (int)(this.height / 2f) - 5, isHint ? 0xcccccc : 16777215);
        if (selected)
        {
            OpenCraft.getFont().drawShadow("_", isHint ? 5 : 7 + OpenCraft.getFont().getTextWidth(text), (int)(this.height / 2f) - 5, 16777215);
        }
        GL11.glPopMatrix();
        GL11.glLoadIdentity();
        GL11.glTranslatef(0, 0,-200);
    }

}
