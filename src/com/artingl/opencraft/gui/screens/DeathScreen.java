package com.artingl.opencraft.gui.screens;

import com.artingl.opencraft.OpenCraft;
import com.artingl.opencraft.gui.Button;
import com.artingl.opencraft.gui.Screen;
import org.lwjgl.opengl.GL11;

public class DeathScreen extends Screen
{

    private boolean escapeClick;

    public DeathScreen() {
        super(OpenCraft.getWidth(), OpenCraft.getHeight(), "death_screen");
    }

    public void init() {
        super.init();

        this.addElement(new Button(1, 0, 0, "Respawn", () -> {
            OpenCraft.getLevel().getPlayerEntity().respawn();
            OpenCraft.closeCurrentScreen();
        }));
        this.addElement(new Button(0, 0, 0, "Quit to main menu", OpenCraft::quitToMainMenu));
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

//        drawBackground(VerticesBuffer.instance, screenWidth, screenHeight, 0x808080);

        super.render(screenWidth, screenHeight, scale);
        fill(0, 0, screenWidth, screenHeight, 0x99441111);

        GL11.glScalef(3, 3, 3);
        drawCenteredString("Game over!", screenWidth / 6, 10, 0xFFFFFF);
        GL11.glLoadIdentity();
    }

}
