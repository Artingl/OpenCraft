package com.artingl.opencraft.GUI.Screens;

import com.artingl.opencraft.Control.Game.Input;
import com.artingl.opencraft.GUI.Elements.Button;
import com.artingl.opencraft.GUI.Elements.Element;
import com.artingl.opencraft.GUI.Elements.Slider;
import com.artingl.opencraft.GUI.ScreenRegistry;
import com.artingl.opencraft.Opencraft;
import com.artingl.opencraft.Control.Render.BufferRenderer;
import com.artingl.opencraft.Resources.Lang.Lang;
import com.artingl.opencraft.Resources.Options.OptionsRegistry;

public class SettingsMenuScreen extends Screen
{

    private int soundSlider;
    private int fovSlider;
    private int videoSettingsButton;
    private int controlsButton;

    public SettingsMenuScreen() {
        super(Opencraft.getWidth(), Opencraft.getHeight(), "settings");
        this.renderGameInBackground = true;
    }

    public void init() {
        super.init();

        soundSlider = this.addElement(new Slider(this, 0, 0, 0, 100, Lang.getTranslatedString("opencraft:gui.text.sound"), (progress) -> {
            ((Slider)getElements().get(soundSlider)).setText(Lang.getTranslatedString("opencraft:gui.text.sound") + ": " + progress + "%");
            OptionsRegistry.updateOption(OptionsRegistry.Values.getOption("soundVolume").setValue(progress));
        }));

        fovSlider = this.addElement(new Slider(this, 0, 0, 20, 120, Lang.getTranslatedString("opencraft:gui.text.fov"), (progress) -> {
            ((Slider)getElements().get(fovSlider)).setText("FOV: " + progress);
            OptionsRegistry.updateOption(OptionsRegistry.Values.getOption("FOV").setValue(progress));
        }));

        videoSettingsButton = this.addElement(new Button(this, 0, 0, Lang.getTranslatedString("opencraft:gui.text.video_settings"), () -> {
            Opencraft.setCurrentScreen(ScreenRegistry.videoSettings);
        }));

        controlsButton = this.addElement(new Button(this, 0, 0, Lang.getTranslatedString("opencraft:gui.text.controls"), () -> {
        }));

        ((Slider)getElements().get(soundSlider)).setProgress(OptionsRegistry.Values.getIntOption("soundVolume"));
        ((Slider)getElements().get(fovSlider)).setProgress(Opencraft.getFOV());
    }

    @Override
    public void updateElement(Element element, int screenWidth, int screenHeight, int scale) {
        super.updateElement(element, screenWidth, screenHeight, scale);
        element.setWidth(150);

        if (element instanceof Slider edit)
        {
            if (edit.getId() == soundSlider) {
                edit.setX(screenWidth / 2f - edit.getWidth() - 10);
                edit.setY(screenHeight / 2f - (edit.getHeight() * 2));
            }
            else if (edit.getId() == fovSlider) {
                edit.setX(screenWidth / 2f);
                edit.setY(screenHeight / 2f - (edit.getHeight() * 2));
            }
        }
        else if (element instanceof Button btn) {
            if (btn.getId() == videoSettingsButton) {
                btn.setX(screenWidth / 2f - btn.getWidth() - 10);
                btn.setY(screenHeight / 2f - btn.getHeight() + 5);
            }
            else if (btn.getId() == controlsButton) {
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
            BufferRenderer t = BufferRenderer.getGlobalInstance();
            drawBackground(t, screenWidth, screenHeight, 0x808080);
        }

        super.render(screenWidth, screenHeight, scale);
    }

    @Override
    protected void keyPressed(Input.KeyInput keyInput) {
        super.keyPressed(keyInput);

        if (keyInput.keyCode == Input.Keys.KEY_ESCAPE) {
            if (!Opencraft.isWorldLoaded())
                Opencraft.setCurrentScreen(ScreenRegistry.mainMenu);
            else Opencraft.closeCurrentScreen();
        }
    }

}
