package com.artingl.opencraft.GUI.Screens;

import com.artingl.opencraft.GL.Controls;
import com.artingl.opencraft.GUI.Elements.Button;
import com.artingl.opencraft.GUI.Elements.Element;
import com.artingl.opencraft.GUI.Elements.Slider;
import com.artingl.opencraft.GUI.GUI;
import com.artingl.opencraft.Opencraft;
import com.artingl.opencraft.Rendering.VerticesBuffer;
import com.artingl.opencraft.Resources.Lang.Lang;
import com.artingl.opencraft.Resources.Options.OptionsRegistry;

public class SettingsMenuScreen extends Screen
{

    public SettingsMenuScreen() {
        super(Opencraft.getWidth(), Opencraft.getHeight(), "settings");
        this.renderGameInBackground = true;
    }

    public void init() {
        super.init();

        this.addElement(new Slider(this, 0, 0, 0, 0, 100, Lang.getLanguageString("opencraft:gui.text.sound"), (progress) -> {
            ((Slider)getElements().get(0)).setText(Lang.getLanguageString("opencraft:gui.text.sound") + ": " + progress + "%");
            OptionsRegistry.updateOption(OptionsRegistry.Values.getOption("soundVolume").setValue(progress));
        }));

        this.addElement(new Slider(this, 1, 0, 0, 20, 120, Lang.getLanguageString("opencraft:gui.text.fov"), (progress) -> {
            ((Slider)getElements().get(1)).setText("FOV: " + progress);
            OptionsRegistry.updateOption(OptionsRegistry.Values.getOption("FOV").setValue(progress));
        }));

        this.addElement(new Button(this, 2, 0, 0, Lang.getLanguageString("opencraft:gui.text.video_settings"), () ->
                Opencraft.setCurrentScreen(GUI.videoSettings)));

        this.addElement(new Button(this, 3, 0, 0, Lang.getLanguageString("opencraft:gui.text.controls"), () -> {

        }));



        ((Slider)getElements().get(0)).setProgress(OptionsRegistry.Values.getIntOption("soundVolume"));
        ((Slider)getElements().get(1)).setProgress(Opencraft.getFOV());
    }

    @Override
    public void updateElement(Element element, int screenWidth, int screenHeight, int scale) {
        super.updateElement(element, screenWidth, screenHeight, scale);
        element.setWidth(150);

        if (element instanceof Slider edit)
        {
            if (edit.getId() == 0) {
                edit.setX(screenWidth / 2f - edit.getWidth() - 10);
                edit.setY(screenHeight / 2f - (edit.getHeight() * 2));
            }
            else if (edit.getId() == 1) {
                edit.setX(screenWidth / 2f);
                edit.setY(screenHeight / 2f - (edit.getHeight() * 2));
            }
        }
        else if (element instanceof Button btn) {
            if (btn.getId() == 2) {
                btn.setX(screenWidth / 2f - btn.getWidth() - 10);
                btn.setY(screenHeight / 2f - btn.getHeight() + 5);
            }
            else if (btn.getId() == 3) {
                btn.setX(screenWidth / 2f);
                btn.setY(screenHeight / 2f - btn.getHeight() + 5);
            }
        }
    }

    public void render(int screenWidth, int screenHeight, int scale)
    {
        if (Opencraft.isWorldLoaded()) {
            fill(0, 0, screenWidth, screenHeight, 0x99111111);
        }
        else {
            VerticesBuffer t = VerticesBuffer.getGlobalInstance();
            drawBackground(t, screenWidth, screenHeight, 0x808080);
        }


        super.render(screenWidth, screenHeight, scale);
    }

    @Override
    protected void keyPressed(Controls.KeyInput keyInput) {
        super.keyPressed(keyInput);

        if (keyInput.keyCode == Controls.Keys.KEY_ESCAPE) {
            if (!Opencraft.isWorldLoaded())
                Opencraft.setCurrentScreen(GUI.mainMenu);
            else Opencraft.closeCurrentScreen();
        }
    }

}
