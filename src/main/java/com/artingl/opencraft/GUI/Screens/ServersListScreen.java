package com.artingl.opencraft.GUI.Screens;

import com.artingl.opencraft.Control.Game.Input;
import com.artingl.opencraft.GUI.Elements.Button;
import com.artingl.opencraft.GUI.Elements.Element;
import com.artingl.opencraft.GUI.GUI;
import com.artingl.opencraft.Opencraft;
import com.artingl.opencraft.Control.Game.VerticesBuffer;
import com.artingl.opencraft.Resources.Lang.Lang;
import org.lwjgl.opengl.GL11;

public class ServersListScreen extends Screen
{

    private int backButton;
    private int joinServerButton;

    public ServersListScreen() {
        super(Opencraft.getWidth(), Opencraft.getHeight(), "servers_select");
    }

    public void init() {
        super.init();

        backButton = this.addElement(new Button(this, 0, 0, Lang.getTranslatedString("opencraft:gui.text.back"), () -> Opencraft.setCurrentScreen(GUI.mainMenu)));
        joinServerButton = this.addElement(new Button(this, 0, 0, Lang.getTranslatedString("opencraft:gui.text.join_server"), () -> {
        }));
    }

    @Override
    public void updateElement(Element element, int screenWidth, int screenHeight, int scale) {
        super.updateElement(element, screenWidth, screenHeight, scale);
        if (element instanceof Button btn)
        {
            btn.setY(screenHeight - (btn.getHeight() / 2f) - 25);

            if (btn.getId() == joinServerButton)
            {
                btn.setX(screenWidth / 2f - btn.getWidth() - 10);
            }
            else if (btn.getId() == backButton) {
                btn.setX(screenWidth / 2f + 10);
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
    protected void keyPressed(Input.KeyInput keyInput) {
        super.keyPressed(keyInput);

        if (keyInput.keyCode == Input.Keys.KEY_ESCAPE) {
            Opencraft.setCurrentScreen(GUI.mainMenu);
        }
    }

}
