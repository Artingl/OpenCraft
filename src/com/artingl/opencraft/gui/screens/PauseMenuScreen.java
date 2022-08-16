package com.artingl.opencraft.gui.screens;

import com.artingl.opencraft.Resources.Lang;
import com.artingl.opencraft.gui.Button;
import com.artingl.opencraft.gui.Screen;
import com.artingl.opencraft.OpenCraft;
import org.lwjgl.input.Keyboard;

public class PauseMenuScreen extends Screen
{

    private boolean escapeClick;

    public PauseMenuScreen() {
        super(OpenCraft.getWidth(), OpenCraft.getHeight(), "game_menu");
    }

    public void init() {
        super.init();

        this.addElement(new Button(1, 0, 0, Lang.getLanguageString("opencraft:gui.text.back_to_game"), () -> {
            escapeClick = false;
            OpenCraft.closeCurrentScreen();
        }));
        this.addElement(new Button(0, 0, 0, Lang.getLanguageString("opencraft:gui.text.quit_world"), OpenCraft::quitToMainMenu));
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

        if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
        {
            escapeClick = true;
        }
        else if (escapeClick)
        {
            escapeClick = false;
            OpenCraft.closeCurrentScreen();
            return;
        }

        super.render(screenWidth, screenHeight, scale);
        fill(0, 0, screenWidth, screenHeight, 0x99111111);
    }

}
