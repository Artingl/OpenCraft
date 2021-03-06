package OpenCraft.gui.screens;

import OpenCraft.gui.Button;
import OpenCraft.gui.Screen;
import OpenCraft.Rendering.VerticesBuffer;
import OpenCraft.OpenCraft;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

public class WorldList extends Screen
{

    public String levelName;
    private boolean escapeClick;
    private int worldBtnPosition;
    private int loadWorldBtnId;

    public WorldList() {
        super(OpenCraft.getWidth(), OpenCraft.getHeight(), "Select world");
    }

    public void selectWorld(int btn_id, String path)
    {
        ((Button)getElements().get(loadWorldBtnId)).enabled = true;
        getElements().forEach((id, element) -> {
            if (element instanceof Button)
            {
                Button btn = (Button)element;
                if (btn.getId() == 3) {
                    btn.selected = false;
                }

            }
        });
        ((Button)getElements().get(btn_id)).selected = true;
        levelName = path;
    }

    public void init() {
        super.init();

        this.addElement(new Button(0, 0, 0, "Create a new world", () -> OpenCraft.setCurrentScreen(OpenCraft.getNewWorldConfigurator())));
        loadWorldBtnId = this.addElement(new Button(1, 0, 0, "Load world", () -> {
            setLoadingScreen("Loading world...");
            OpenCraft.startNewGame(true);
        }));

        levelName = "";
        ((Button)getElements().get(loadWorldBtnId)).enabled = false;

        File folder = new File("saves");
        folder.mkdir();
        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {
            if (file.isDirectory()) {
                File level = new File("saves" + File.separator + file.getName() + File.separator + "level.data");
                if (level.exists())
                {
                    try {
                        DataInputStream dis = new DataInputStream(new GZIPInputStream(new FileInputStream("saves" + File.separator + file.getName() + File.separator + "level.data")));
                        int header = dis.readInt();
                        if (header <= 78924536)
                        {
                            String worldName = dis.readUTF();
                            String author = dis.readUTF();
                            String creationDate = dis.readUTF();
                            String version = dis.readUTF();
                            int seed = dis.readInt();

                            final int id = this.getElements().size();
                            this.addElement(new Button(3, 0, 0, worldName, () -> selectWorld(id, file.getName())));
                        }

                        dis.close();
                    } catch (IOException e) { }
                }
            }
        }

    }

    public void render(int screenWidth, int screenHeight, int scale)
    {
        worldBtnPosition = 70;
        getElements().forEach((id, element) -> {
            if (element instanceof Button)
            {
                Button btn = (Button)element;

                btn.setY(screenHeight - (btn.getHeight() / 2f) - 25);

                if (btn.getId() == 1)
                {
                    btn.setX(screenWidth / 2f - btn.getWidth() - 10);
                }
                else if (btn.getId() == 0) {
                    btn.setX(screenWidth / 2f + 10);
                }
                else if (btn.getId() == 3) {
                    btn.setY(worldBtnPosition);
                    btn.setX(screenWidth / 2f - btn.getWidth() / 2);
                    worldBtnPosition += btn.getHeight() + 20;
                }

            }
        });

        if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
        {
            escapeClick = true;
        }
        else if (escapeClick)
        {
            OpenCraft.setCurrentScreen(OpenCraft.getMainMenuScreen());
            escapeClick = false;
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

        super.render(screenWidth, screenHeight, scale);
    }

}
