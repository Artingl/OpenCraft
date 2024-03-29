package com.artingl.opencraft.GUI.Screens;

import com.artingl.opencraft.GUI.Elements.Element;
import com.artingl.opencraft.Opencraft;
import com.artingl.opencraft.GUI.Elements.Button;
import com.artingl.opencraft.Resources.Lang.Lang;
import org.lwjgl.opengl.GL11;

public class DeathScreen extends Screen
{

    private int respawnButton;
    private int quitWorldButton;

    public DeathScreen() {
        super(Opencraft.getWidth(), Opencraft.getHeight(), "death_screen");
        this.renderGameInBackground = true;
        this.showTittle = false;
    }

    public void init() {
        super.init();

        respawnButton = this.addElement(new Button(this,0, 0, Lang.getTranslatedString("opencraft:gui.text.respawn"), () -> {
            Opencraft.getPlayerEntity().respawn();
            Opencraft.closeCurrentScreen();
        }));
        quitWorldButton = this.addElement(new Button(this,0, 0, Lang.getTranslatedString("opencraft:gui.text.quit_world"), Opencraft::quitToMainMenu));
    }


    @Override
    public void updateElement(Element element, int screenWidth, int screenHeight, int scale) {
        super.updateElement(element, screenWidth, screenHeight, scale);
        if (element instanceof Button btn)
        {
            btn.setX(screenWidth / 2f - btn.getWidth() / 2f);
            if (btn.getId() == respawnButton)
            {
                btn.setY(screenHeight / 2f - btn.getHeight() / 2f - 10);
            }
            else if (btn.getId() == quitWorldButton) {
                btn.setY(screenHeight / 2f + 10);
            }

        }
    }

    public void render(int screenWidth, int screenHeight, int scale)
    {
//        drawBackground(VerticesBuffer.getGlobalInstance(), screenWidth, screenHeight, 0x808080);

        super.render(screenWidth, screenHeight, scale);
        fill(0, 0, screenWidth, screenHeight, 0x99441111);

        GL11.glScalef(3, 3, 3);
        drawCenteredString("Game over!", screenWidth / 6, 10, 0xFFFFFF);
        GL11.glLoadIdentity();
    }

}
