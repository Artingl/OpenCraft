package OpenCraft.gui.screens;

import OpenCraft.OpenCraft;
import OpenCraft.Rendering.VerticesBuffer;
import OpenCraft.gui.Button;
import OpenCraft.gui.Screen;
import org.lwjgl.input.Keyboard;

public class DeadScreen extends Screen
{

    private boolean escapeClick;

    public DeadScreen() {
        super(OpenCraft.getWidth(), OpenCraft.getHeight(), "");
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

        drawBackground(VerticesBuffer.instance, screenWidth, screenHeight, 0x808080);

        super.render(screenWidth, screenHeight, scale);

    }

}
