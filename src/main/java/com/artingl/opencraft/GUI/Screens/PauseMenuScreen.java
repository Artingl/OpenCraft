package com.artingl.opencraft.GUI.Screens;

import com.artingl.opencraft.Control.Game.Input;
import com.artingl.opencraft.GUI.Elements.Element;
import com.artingl.opencraft.GUI.ScreenRegistry;
import com.artingl.opencraft.Resources.Lang.Lang;
import com.artingl.opencraft.GUI.Elements.Button;
import com.artingl.opencraft.Opencraft;

public class PauseMenuScreen extends Screen
{

    private int settingsButton;
    private int backToGameButton;
    private int quitButton;
    private int hostGameButton;

    public PauseMenuScreen() {
        super(Opencraft.getWidth(), Opencraft.getHeight(), "game_menu");
        this.renderGameInBackground = true;
    }

    public void init() {
        super.init();

        settingsButton = this.addElement(new Button(this, 0, 0, Lang.getTranslatedString("opencraft:gui.text.settings"), () -> Opencraft.setCurrentScreen(ScreenRegistry.settingsScreen)));
        backToGameButton = this.addElement(new Button(this, 0, 0, Lang.getTranslatedString("opencraft:gui.text.back_to_game"), Opencraft::closeCurrentScreen));
        quitButton = this.addElement(new Button(this, 0, 0, Lang.getTranslatedString("opencraft:gui.text.quit_world"), Opencraft::quitToMainMenu));
        hostGameButton = this.addElement(new Button(this, 0, 0, "Host game", () -> {
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
            if (btn.getId() == backToGameButton)
            {
                btn.setY(screenHeight / 2f - btn.getHeight() / 2f - 10);
            }
            else if (btn.getId() == settingsButton) {
                btn.setY(screenHeight / 2f + 5);
            }
            else if (btn.getId() == hostGameButton) {
                btn.setY(screenHeight / 2f + btn.getHeight() / 2f + 20);
            }
            else if (btn.getId() == quitButton) {
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
    protected void keyPressed(Input.KeyInput keyInput) {
        super.keyPressed(keyInput);

        if (keyInput.keyCode == Input.Keys.KEY_ESCAPE) {
            Opencraft.closeCurrentScreen();
        }
    }

}
