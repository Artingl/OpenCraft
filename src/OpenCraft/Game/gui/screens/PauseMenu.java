package OpenCraft.Game.gui.screens;

import OpenCraft.Game.Rendering.VerticesBuffer;
import OpenCraft.Game.gui.Button;
import OpenCraft.Game.gui.Screen;
import OpenCraft.OpenCraft;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class PauseMenu extends Screen
{

    private boolean escapeClick;

    public PauseMenu() {
        super(OpenCraft.getWidth(), OpenCraft.getHeight(), "Game menu");
    }

    public void init() {
        super.init();

        this.addElement(new Button(1, 0, 0, "Back to game", () -> {
            escapeClick = false;
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

        drawBackground(VerticesBuffer.instance, screenWidth, screenHeight, 0x808080);

        //VerticesBuffer t = VerticesBuffer.instance;
        //drawBackground(t, screenWidth, screenHeight, 0x808080);

        super.render(screenWidth, screenHeight, scale);

    }

}
