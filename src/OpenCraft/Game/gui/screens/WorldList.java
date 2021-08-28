package OpenCraft.Game.gui.screens;

import OpenCraft.Game.gui.Button;
import OpenCraft.Game.gui.Screen;
import OpenCraft.Game.Rendering.TextureManager;
import OpenCraft.Game.Rendering.VerticesBuffer;
import OpenCraft.OpenCraft;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class WorldList extends Screen
{

    private final int background_id;

    public WorldList() throws IOException {
        super(OpenCraft.getWidth(), OpenCraft.getHeight());
        background_id = TextureManager.load(ImageIO.read(new File("resources/gui/dirt.png")));

        this.addElement(new Button(0, 0, 0, "Create a new world", OpenCraft::startNewGame));
        this.addElement(new Button(1, 0, 0, "Load world", () -> {

        }));
    }

    public void render(int screenWidth, int screenHeight, int scale)
    {
        getElements().forEach((id, element) -> {
            if (element instanceof Button)
            {
                Button btn = (Button)element;

                btn.setY(screenHeight - (btn.getHeight() / 2f) - 25);

                if (btn.getId() == 1)
                {
                    btn.setX(screenWidth / 2f - btn.getWidth() - 10);
                }
                else {
                    btn.setX(screenWidth / 2f + 10);
                }

            }
        });

        if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
        {
            OpenCraft.setCurrentScreen(OpenCraft.getMainMenuScreen());
            return;
        }

        VerticesBuffer t = VerticesBuffer.instance;

        //GL11.glClear(16640);
        drawBackground(t, screenWidth, screenHeight, 0x808080);
        GL11.glPushMatrix();
        GL11.glTranslatef(0, 50,50);
        drawBackground(t, screenWidth, screenHeight - 100, 0x404040);
        GL11.glPopMatrix();

        GL11.glTranslatef(0, 0,-200);

        OpenCraft.getFont().drawShadow("Select world", (screenWidth - OpenCraft.getFont().width("Select world")) / 2, 20, 0xAAAAAA);
        super.render(screenWidth, screenHeight, scale);
    }

    private void drawBackground(VerticesBuffer t, int screenWidth, int screenHeight, int clr)
    {
        GL11.glEnable(3553);
        GL11.glBindTexture(3553, background_id);
        t.begin();
        t.color(clr);
        float s = 32.0F;
        t.vertexUV(0.0F, (float)screenHeight, 0.0F, 0.0F, (float)screenHeight / s);
        t.vertexUV((float)screenWidth, (float)screenHeight, 0.0F, (float)screenWidth / s, (float)screenHeight / s);
        t.vertexUV((float)screenWidth, 0.0F, 0.0F, (float)screenWidth / s, 0.0F);
        t.vertexUV(0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        t.end();
        GL11.glEnable(3553);
    }

}
