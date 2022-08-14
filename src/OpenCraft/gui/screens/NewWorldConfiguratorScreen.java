package OpenCraft.gui.screens;

import OpenCraft.Rendering.VerticesBuffer;
import OpenCraft.gui.Button;
import OpenCraft.gui.EditArea;
import OpenCraft.gui.Screen;
import OpenCraft.OpenCraft;
import org.lwjgl.input.Keyboard;

import java.io.File;

public class NewWorldConfiguratorScreen extends Screen
{

    public String levelName;
    private boolean escapeClick;
    private int levelEditArea;
    private int seedEditArea;
    private int createNewWorldBtn;

    public NewWorldConfiguratorScreen() {
        super(OpenCraft.getWidth(), OpenCraft.getHeight(), "Create a new world");
    }

    public void init() {
        super.init();

        levelEditArea = this.addElement(new EditArea(0, 0, 0, "World name", () -> {
            if (new File("saves" + File.separator + ((EditArea) getElements().get(levelEditArea)).getText()).exists())
            {
                ((Button)getElements().get(createNewWorldBtn)).enabled = false;
            }
            else {
                ((Button)getElements().get(createNewWorldBtn)).enabled = true;
            }
        }));
        seedEditArea = this.addElement(new EditArea(1, 0, 0, "World seed", () -> {}));
        createNewWorldBtn = this.addElement(new Button(0, 0, 0, "Create a new world", () -> {
            setLoadingScreen("Loading world...");
            OpenCraft.getWorldListScreen().levelName = ((EditArea)getElements().get(levelEditArea)).getText();
            int seed = ((EditArea)getElements().get(seedEditArea)).getText().hashCode();

            if (((EditArea) getElements().get(seedEditArea)).getText().equals("")) {
                // todo: make it random
                seed = 3556498;
            }
            OpenCraft.startNewGame(false, seed);
        }));

        if (new File("saves" + File.separator + ((EditArea) getElements().get(levelEditArea)).getText()).exists())
        {
            ((Button)getElements().get(createNewWorldBtn)).enabled = false;
        }
        else {
            ((Button)getElements().get(createNewWorldBtn)).enabled = true;
        }

        ((EditArea)getElements().get(levelEditArea)).setText("New World");
        ((Button)getElements().get(createNewWorldBtn)).enabled = true;
    }

    public void render(int screenWidth, int screenHeight, int scale)
    {
        getElements().forEach((id, element) -> {
            if (element instanceof EditArea)
            {
                EditArea edit = (EditArea)element;
                if (edit.getId() == levelEditArea) {
                    edit.setX(screenWidth / 2f - edit.getWidth() / 2f);
                    edit.setY(screenHeight / 4f);
                }
                else if (edit.getId() == seedEditArea) {
                    edit.setX(screenWidth / 2f - edit.getWidth() / 2f);
                    edit.setY(screenHeight / 4f + edit.getHeight() + 10);
                }
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
            escapeClick = true;
        }
        else if (escapeClick)
        {
            OpenCraft.setCurrentScreen(OpenCraft.getWorldListScreen());
            escapeClick = false;
            return;
        }

        VerticesBuffer t = VerticesBuffer.instance;

        //GL11.glClear(16640);
        drawBackground(t, screenWidth, screenHeight, 0x808080);

        super.render(screenWidth, screenHeight, scale);
    }

}