package com.artingl.opencraft.GUI.Screens;

import com.artingl.opencraft.Control.Game.Input;
import com.artingl.opencraft.GUI.Elements.Element;
import com.artingl.opencraft.GUI.ScreenRegistry;
import com.artingl.opencraft.Opencraft;
import com.artingl.opencraft.Control.Render.BufferRenderer;
import org.lwjgl.opengl.GL11;

public class ModsListScreen extends Screen
{

    public ModsListScreen() {
        super(Opencraft.getWidth(), Opencraft.getHeight(), "mods_list");
    }

    public void init() {
        super.init();
    }

    @Override
    public void updateElement(Element element, int screenWidth, int screenHeight, int scale) {
        super.updateElement(element, screenWidth, screenHeight, scale);
    }

    public void render(int screenWidth, int screenHeight, int scale)
    {
        BufferRenderer t = BufferRenderer.getGlobalInstance();

        drawBackground(t, screenWidth, screenHeight, 0x808080);
        GL11.glPushMatrix();
        GL11.glTranslatef(0, 50,50);
        drawBackground(t, screenWidth, screenHeight - 100, 0x404040);
        GL11.glPopMatrix();

        GL11.glTranslatef(0, 0,-200);

        super.render(screenWidth, screenHeight, scale);
    }

    @Override
    protected void keyPressed(Input.KeyInput keyInput) {
        super.keyPressed(keyInput);

        if (keyInput.keyCode == Input.Keys.KEY_ESCAPE) {
            Opencraft.setCurrentScreen(ScreenRegistry.mainMenu);
        }
    }

}
