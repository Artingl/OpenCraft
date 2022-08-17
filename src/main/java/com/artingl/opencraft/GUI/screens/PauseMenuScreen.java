package com.artingl.opencraft.GUI.screens;

import com.artingl.opencraft.GL.Controls;
import com.artingl.opencraft.Resources.Lang;
import com.artingl.opencraft.GUI.Button;
import com.artingl.opencraft.GUI.Screen;
import com.artingl.opencraft.OpenCraft;

public class PauseMenuScreen extends Screen
{

    public PauseMenuScreen() {
        super(OpenCraft.getWidth(), OpenCraft.getHeight(), "game_menu");
    }

    public void init() {
        super.init();

        this.addElement(new Button(this, 1, 0, 0, Lang.getLanguageString("opencraft:gui.text.back_to_game"), OpenCraft::closeCurrentScreen));
        this.addElement(new Button(this, 0, 0, 0, Lang.getLanguageString("opencraft:gui.text.quit_world"), OpenCraft::quitToMainMenu));
    }

    public void render(int screenWidth, int screenHeight, int scale)
    {
        getElements().forEach((id, element) -> {
            if (element instanceof Button)
            {
                Button btn = (Button)element;

                btn.setX(screenWidth / 2f - btn.getWidth() / 2f);
                if (btn.getId() == 1)
                {
                    btn.setY(screenHeight / 2f - btn.getHeight() / 2f - 10);
                }
                else if (btn.getId() == 0) {
                    btn.setY(screenHeight / 2f + 10);
                }

            }
        });

        super.render(screenWidth, screenHeight, scale);
        fill(0, 0, screenWidth, screenHeight, 0x99111111);
    }

    @Override
    protected void keyPressed(Controls.KeyInput keyInput) {
        super.keyPressed(keyInput);

        if (keyInput.keyCode == Controls.Keys.KEY_ESCAPE) {
            OpenCraft.closeCurrentScreen();
        }
    }

}
