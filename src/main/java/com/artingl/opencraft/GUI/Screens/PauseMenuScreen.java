package com.artingl.opencraft.GUI.Screens;

import com.artingl.opencraft.GL.Controls;
import com.artingl.opencraft.GUI.Elements.Element;
import com.artingl.opencraft.GUI.GUI;
import com.artingl.opencraft.Resources.Lang.Lang;
import com.artingl.opencraft.GUI.Elements.Button;
import com.artingl.opencraft.Opencraft;

public class PauseMenuScreen extends Screen
{

    public PauseMenuScreen() {
        super(Opencraft.getWidth(), Opencraft.getHeight(), "game_menu");
        this.renderGameInBackground = true;
    }

    public void init() {
        super.init();

        this.addElement(new Button(this, 2, 0, 0, Lang.getTranslatedString("opencraft:gui.text.settings"), () -> Opencraft.setCurrentScreen(GUI.settingsScreen)));
        this.addElement(new Button(this, 1, 0, 0, Lang.getTranslatedString("opencraft:gui.text.back_to_game"), Opencraft::closeCurrentScreen));
        this.addElement(new Button(this, 0, 0, 0, Lang.getTranslatedString("opencraft:gui.text.quit_world"), Opencraft::quitToMainMenu));
        this.addElement(new Button(this, 3, 0, 0, "Host game", () -> {
            Opencraft.getInternalServer().hostgame();
            Opencraft.closeCurrentScreen();
        }));
    }

    @Override
    public void updateElement(Element element, int screenWidth, int screenHeight, int scale) {
        super.updateElement(element, screenWidth, screenHeight, scale);
        if (element instanceof Button btn)
        {

            btn.setX(screenWidth / 2f - btn.getWidth() / 2f);
            if (btn.getId() == 1)
            {
                btn.setY(screenHeight / 2f - btn.getHeight() / 2f - 10);
            }
            else if (btn.getId() == 2) {
                btn.setY(screenHeight / 2f + 5);
            }
            else if (btn.getId() == 3) {
                btn.setY(screenHeight / 2f + btn.getHeight() / 2f + 20);
            }
            else if (btn.getId() == 0) {
                btn.setY(screenHeight / 2f + (btn.getHeight() * 2) + 15);
            }

        }
    }

    public void render(int screenWidth, int screenHeight, int scale)
    {
        fill(0, 0, screenWidth, screenHeight, 0x99111111);
        super.render(screenWidth, screenHeight, scale);
    }

    @Override
    protected void keyPressed(Controls.KeyInput keyInput) {
        super.keyPressed(keyInput);

        if (keyInput.keyCode == Controls.Keys.KEY_ESCAPE) {
            Opencraft.closeCurrentScreen();
        }
    }

}
