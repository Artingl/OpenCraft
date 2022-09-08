package com.artingl.opencraft.GUI.Screens;

import com.artingl.opencraft.GL.Controls;
import com.artingl.opencraft.GUI.Elements.Element;
import com.artingl.opencraft.GUI.GUI;
import com.artingl.opencraft.Resources.Lang.Lang;
import com.artingl.opencraft.GUI.Elements.Button;
import com.artingl.opencraft.Rendering.Game.VerticesBuffer;
import com.artingl.opencraft.Opencraft;
import com.artingl.opencraft.World.Level.LevelType;
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

        this.addElement(new Button(this, 0, 0, 0, Lang.getTranslatedString("opencraft:gui.text.new_world_configurer"), () -> Opencraft.setCurrentScreen(GUI.newWorldConfigurator)));
        this.addElement(new Button(this, 1, 0, 0, Lang.getTranslatedString("opencraft:gui.text.load_world"), () -> {
            GUI.loadingScreen.setLoadingText(Lang.getTranslatedString("opencraft:gui.text.loading_world"));
            Opencraft.startNewGame(1, -1);
        }));

        levelName = "";
        ((Button)getElements().get(1)).enabled = false;

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
                            this.addElement(new Button(this, 3, 0, 0, worldName, () -> selectWorld(id, file.getName())));
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
    }

    public void render(int screenWidth, int screenHeight, int scale)
    {
        VerticesBuffer t = VerticesBuffer.getGlobalInstance();

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
    protected void keyPressed(Controls.KeyInput keyInput) {
        super.keyPressed(keyInput);

        if (keyInput.keyCode == Controls.Keys.KEY_ESCAPE) {
            Opencraft.setCurrentScreen(GUI.mainMenu);
        }
    }

}
