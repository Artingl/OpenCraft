package com.artingl.opencraft.GUI.Screens;

import com.artingl.opencraft.GL.Controls;
import com.artingl.opencraft.GUI.Elements.Button;
import com.artingl.opencraft.GUI.Elements.Element;
import com.artingl.opencraft.GUI.GUI;
import com.artingl.opencraft.Opencraft;
import com.artingl.opencraft.Rendering.Game.VerticesBuffer;
import com.artingl.opencraft.Resources.Lang.Lang;
import org.lwjgl.opengl.GL11;

public class ServersListScreen extends Screen
{
    private int serverBtnPosition;

    public ServersListScreen() {
        super(Opencraft.getWidth(), Opencraft.getHeight(), "servers_select");
    }

    public void init() {
        super.init();

        serverBtnPosition = 70;

        this.addElement(new Button(this, 0, 0, 0, Lang.getTranslatedString("opencraft:gui.text.back"), () -> Opencraft.setCurrentScreen(GUI.mainMenu)));
        this.addElement(new Button(this, 1, 0, 0, Lang.getTranslatedString("opencraft:gui.text.join_server"), () -> {
        }));
    }

    @Override
    public void updateElement(Element element, int screenWidth, int screenHeight, int scale) {
        super.updateElement(element, screenWidth, screenHeight, scale);
        if (element instanceof Button btn)
        {
            btn.setY(screenHeight - (btn.getHeight() / 2f) - 25);

            if (btn.getId() == 1)
            {
                btn.setX(screenWidth / 2f - btn.getWidth() - 10);
            }
            else if (btn.getId() == 0) {
                btn.setX(screenWidth / 2f + 10);
            }
            else if (btn.getId() == 3) {
                btn.setY(serverBtnPosition);
                btn.setX(screenWidth / 2f - btn.getWidth() / 2);
                serverBtnPosition += btn.getHeight() + 20;
            }
        }
    }

    public void render(int screenWidth, int screenHeight, int scale)
    {
        VerticesBuffer t = VerticesBuffer.getGlobalInstance();

        drawBackground(t, screenWidth, screenHeight, 0x808080);
        GL11.glPushMatrix();
        GL11.glTranslatef(0, 50,50);
        drawBackground(t, screenWidth, screenHeight - 100, 0x404040);
        GL11.glPopMatrix();

        GL11.glTranslatef(0, 0,-200);

        super.render(screenWidth, screenHeight, scale);
    }

    @Override
    protected void keyPressed(Controls.KeyInput keyInput) {
        super.keyPressed(keyInput);

        if (keyInput.keyCode == Controls.Keys.KEY_ESCAPE) {
            Opencraft.setCurrentScreen(GUI.mainMenu);
        }
    }

}
