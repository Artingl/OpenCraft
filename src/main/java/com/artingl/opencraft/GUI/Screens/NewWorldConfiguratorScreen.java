package com.artingl.opencraft.GUI.Screens;

import com.artingl.opencraft.GL.Controls;
import com.artingl.opencraft.GUI.Elements.Element;
import com.artingl.opencraft.GUI.GUI;
import com.artingl.opencraft.Rendering.Game.VerticesBuffer;
import com.artingl.opencraft.Resources.Lang.Lang;
import com.artingl.opencraft.GUI.Elements.Button;
import com.artingl.opencraft.GUI.Elements.EditArea;
import com.artingl.opencraft.Opencraft;
import com.artingl.opencraft.World.Level.LevelType;

import java.io.File;

public class NewWorldConfiguratorScreen extends Screen
{

    public String levelName;

    public NewWorldConfiguratorScreen() {
        super(Opencraft.getWidth(), Opencraft.getHeight(), "new_world_configurer");
    }

    public void init() {
        super.init();

        this.addElement(new EditArea(this, 0, 0, 0, Lang.getTranslatedString("opencraft:gui.text.world_name"), () -> {
            if (new File("saves" + File.separator + ((EditArea) getElements().get(0)).getText()).exists())
            {
                ((Button)getElements().get(3)).enabled = false;
            }
            else {
                ((Button)getElements().get(3)).enabled = true;
            }
        }));
        this.addElement(new EditArea(this, 1, 0, 0, Lang.getTranslatedString("opencraft:gui.text.world_seed"), () -> {}));
        this.addElement(new Button(this, 3, 0, 0, Lang.getTranslatedString("opencraft:gui.text.new_world_configurer"), () -> {
            GUI.loadingScreen.setLoadingText(Lang.getTranslatedString("opencraft:gui.text.loading_world"));
            GUI.worldList.levelName = ((EditArea)getElements().get(0)).getText();
            int seed = ((EditArea)getElements().get(1)).getText().hashCode();

            if (((EditArea) getElements().get(1)).getText().equals("")) {
                // todo: make it random
                seed = 3556498;
            }
            Opencraft.startNewGame(0, seed);
        }));

        if (new File("saves" + File.separator + ((EditArea) getElements().get(0)).getText()).exists())
        {
            ((Button)getElements().get(3)).enabled = false;
        }

        ((EditArea)getElements().get(0)).setText("New World");
        ((Button)getElements().get(2)).setText(Lang.getTranslatedString("opencraft:gui.text.world_type") + ": World");
    }

    @Override
    public void updateElement(Element element, int screenWidth, int screenHeight, int scale) {
        super.updateElement(element, screenWidth, screenHeight, scale);
        if (element instanceof EditArea edit) {
            if (edit.getId() == 0) {
                edit.setX(screenWidth / 2f - edit.getWidth() / 2f);
                edit.setY(screenHeight / 4f);
            } else if (edit.getId() == 1) {
                edit.setX(screenWidth / 2f - edit.getWidth() / 2f);
                edit.setY(screenHeight / 4f + edit.getHeight() + 10);
            }
        }
        else if (element instanceof Button btn)
        {
            if (btn.getId() == 3) {
                btn.setX(screenWidth / 2f - btn.getWidth() / 2f);
                btn.setY(screenHeight - (btn.getHeight() * 5));
            }
            else if (btn.getId() == 2) {
                btn.setX(screenWidth / 2f - btn.getWidth() / 2f);
                btn.setY(screenHeight / 4f + btn.getHeight() + btn.getHeight() + 20);
            }
        }
    }

    public void render(int screenWidth, int screenHeight, int scale)
    {
        VerticesBuffer t = VerticesBuffer.getGlobalInstance();

        //GL11.glClear(16640);
        drawBackground(t, screenWidth, screenHeight, 0x808080);

        super.render(screenWidth, screenHeight, scale);
    }

    @Override
    protected void keyPressed(Controls.KeyInput keyInput) {
        super.keyPressed(keyInput);

        if (keyInput.keyCode == Controls.Keys.KEY_ESCAPE) {
            Opencraft.setCurrentScreen(GUI.worldList);
        }
    }
}
