package OpenCraft.Game.gui.screens;

import OpenCraft.Game.Rendering.TextureManager;
import OpenCraft.Game.Rendering.VerticesBuffer;
import OpenCraft.Game.gui.Button;
import OpenCraft.Game.gui.EditArea;
import OpenCraft.Game.gui.Screen;
import OpenCraft.OpenCraft;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;

public class NewWorldConfigurator extends Screen
{

    public String levelName;

    private int levelEditArea;
    private int createNewWorldBtn;

    public NewWorldConfigurator() {
        super(OpenCraft.getWidth(), OpenCraft.getHeight());
    }

    public void init() {
        super.init();

        levelEditArea = this.addElement(new EditArea(0, 0, 0, "New World", () -> {
            if (new File("saves" + File.separator + ((EditArea) getElements().get(levelEditArea)).getText()).exists())
            {
                ((Button)getElements().get(createNewWorldBtn)).enabled = false;
            }
            else {
                ((Button)getElements().get(createNewWorldBtn)).enabled = true;
            }
        }));
        createNewWorldBtn = this.addElement(new Button(0, 0, 0, "Create a new world", () -> {
            setLoadingScreen("Loading world...");
            OpenCraft.getWorldListScreen().levelName = ((EditArea)getElements().get(levelEditArea)).getText();
            OpenCraft.startNewGame(false);
        }));

        if (new File("saves" + File.separator + ((EditArea) getElements().get(levelEditArea)).getText()).exists())
        {
            ((Button)getElements().get(createNewWorldBtn)).enabled = false;
        }
        else {
            ((Button)getElements().get(createNewWorldBtn)).enabled = true;
        }

    }

    public void render(int screenWidth, int screenHeight, int scale)
    {
        getElements().forEach((id, element) -> {
            if (element instanceof EditArea)
            {
                EditArea edit = (EditArea)element;
                edit.setX(screenWidth / 2f - edit.getWidth() / 2f);
                edit.setY(screenHeight / 4f);
            }

            if (element instanceof Button)
            {
                Button btn = (Button)element;
                btn.setX(screenWidth / 2f - btn.getWidth() / 2f);
                btn.setY(screenHeight - (btn.getHeight() * 5));
            }
        });

        if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
        {
            OpenCraft.setCurrentScreen(OpenCraft.getWorldListScreen());
            return;
        }

        VerticesBuffer t = VerticesBuffer.instance;

        //GL11.glClear(16640);
        drawBackground(t, screenWidth, screenHeight, 0x808080);

        OpenCraft.getFont().drawShadow("Create a new world", (screenWidth - OpenCraft.getFont().width("Create a new world")) / 2, 20, 0xAAAAAA);
        super.render(screenWidth, screenHeight, scale);
    }

}
