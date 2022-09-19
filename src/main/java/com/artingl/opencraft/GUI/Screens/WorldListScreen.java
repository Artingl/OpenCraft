package com.artingl.opencraft.GUI.Screens;

import com.artingl.opencraft.Control.Game.Input;
import com.artingl.opencraft.GUI.Elements.Element;
import com.artingl.opencraft.GUI.ScreenRegistry;
import com.artingl.opencraft.Resources.Lang.Lang;
import com.artingl.opencraft.GUI.Elements.Button;
import com.artingl.opencraft.Control.Render.BufferRenderer;
import com.artingl.opencraft.Opencraft;
import org.lwjgl.opengl.GL11;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

public class WorldListScreen extends Screen
{

    public String levelName;
    private int worldBtnPosition;

    private int newWorldCreationButton;
    private int loadWorldButton;
    private int worldButton = 3;

    public WorldListScreen() {
        super(Opencraft.getWidth(), Opencraft.getHeight(), "world_select");
    }

    public void selectWorld(int btn_id, String path)
    {
        ((Button)getElements().get(1)).enabled = true;
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

        worldBtnPosition = 70;

        newWorldCreationButton = this.addElement(new Button(this, 0, 0, Lang.getTranslatedString("opencraft:gui.text.new_world_creation"), () -> Opencraft.setCurrentScreen(ScreenRegistry.newWorldConfigurator)));
        loadWorldButton = this.addElement(new Button(this, 0, 0, Lang.getTranslatedString("opencraft:gui.text.load_world"), () -> {
            ScreenRegistry.loadingScreen.setLoadingText(Lang.getTranslatedString("opencraft:gui.text.loading_world"));
            Opencraft.startNewGame(1, -1);
        }));

        levelName = "";
        ((Button)getElements().get(loadWorldButton)).enabled = false;

        File folder = new File(Opencraft.getGameDirectory() + "saves");
        folder.mkdir();
        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {
            if (file.isDirectory()) {
                File level = new File(Opencraft.getGameDirectory() + "saves" + File.separator + file.getName() + File.separator + "level.data");
                if (level.exists())
                {
                    try {
                        DataInputStream dis = new DataInputStream(new GZIPInputStream(new FileInputStream(Opencraft.getGameDirectory() + "saves" + File.separator + file.getName() + File.separator + "level.data")));
                        int header = dis.readInt();
                        if (header == -1)
                        {
                            String worldName = dis.readUTF();
                            String author = dis.readUTF();
                            String creationDate = dis.readUTF();
                            String version = dis.readUTF();
                            int seed = dis.readInt();

                            final int id = this.getElements().size();
                            Element elem = new Button(this, 0, 0, worldName, () -> selectWorld(id, file.getName()));
                            this.addElement(elem);
                            elem.setId(worldButton);
                        }

                        dis.close();
                    } catch (IOException e) { }
                }
            }
        }
    }

    @Override
    public void updateElement(Element element, int screenWidth, int screenHeight, int scale) {
        super.updateElement(element, screenWidth, screenHeight, scale);
        if (element instanceof Button btn)
        {
            btn.setY(screenHeight - (btn.getHeight() / 2f) - 25);

            if (btn.getId() == loadWorldButton)
            {
                btn.setX(screenWidth / 2f - btn.getWidth() - 10);
            }
            else if (btn.getId() == newWorldCreationButton) {
                btn.setX(screenWidth / 2f + 10);
            }
            else if (btn.getId() == worldButton) {
                btn.setY(worldBtnPosition);
                btn.setX(screenWidth / 2f - btn.getWidth() / 2);
                worldBtnPosition += btn.getHeight() + 20;
            }
        }
    }

    public void render(int screenWidth, int screenHeight, int scale)
    {
        BufferRenderer t = BufferRenderer.getGlobalInstance();

        //GL11.glClear(16640);
        drawBackground(t, screenWidth, screenHeight, 0x808080);
        GL11.glPushMatrix();
        GL11.glTranslatef(0, 50,50);
        drawBackground(t, screenWidth, screenHeight - 100, 0x404040);
        GL11.glPopMatrix();

        GL11.glTranslatef(0, 0,-200);

        super.render(screenWidth, screenHeight, scale);
    }

    @Override
    protected void keyPressed(Input.KeyInput keyInput) {
        super.keyPressed(keyInput);

        if (keyInput.keyCode == Input.Keys.KEY_ESCAPE) {
            Opencraft.setCurrentScreen(ScreenRegistry.mainMenu);
        }
    }

}
