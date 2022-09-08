package com.artingl.opencraft.GUI.Screens;

import com.artingl.opencraft.GL.Controls;
import com.artingl.opencraft.GUI.Elements.Button;
import com.artingl.opencraft.GUI.Elements.Element;
import com.artingl.opencraft.GUI.Elements.Slider;
import com.artingl.opencraft.GUI.GUI;
import com.artingl.opencraft.Opencraft;
import com.artingl.opencraft.Rendering.Game.VerticesBuffer;
import com.artingl.opencraft.Resources.Lang.Lang;
import com.artingl.opencraft.Resources.Options.OptionsRegistry;

public class VideoSettingsMenuScreen extends Screen
{

    public VideoSettingsMenuScreen() {
        super(Opencraft.getWidth(), Opencraft.getHeight(), "video_settings");
        this.renderGameInBackground = true;
    }

    public void init() {
        super.init();

        this.addElement(new Slider(this, 0, 0, 0, 10, 50, Lang.getTranslatedString("opencraft:gui.text.gui_scale"), (progress) -> {
            progress /= 10;

            ((Slider)getElements().get(0)).setText(Lang.getTranslatedString("opencraft:gui.text.gui_scale") + ": " + progress);
            OptionsRegistry.updateOption(OptionsRegistry.Values.getOption("guiScale").setValue(progress));
        }));

        this.addElement(new Slider(this, 1, 0, 0, 20, 240, Lang.getTranslatedString("opencraft:gui.text.render_distance"), (progress) -> {
            progress /= 10;

            ((Slider)getElements().get(1)).setText(Lang.getTranslatedString("opencraft:gui.text.render_distance") + ": " + progress);
            OptionsRegistry.updateOption(OptionsRegistry.Values.getOption("renderDistance").setValue(progress));
        }));

        this.addElement(new Button(this, 2, 0, 0, Lang.getTranslatedString("opencraft:gui.text.fullscreen"), () -> {
            Opencraft.getDisplay().setFullscreen(!Opencraft.getDisplay().isFullscreen());
            ((Button)getElements().get(2)).setText(Lang.getTranslatedString("opencraft:gui.text.fullscreen") + ": "
                    + Lang.getTranslatedString("opencraft:gui.text." + (Opencraft.getDisplay().isFullscreen() ? "enabled" : "disabled")));
        }));

        this.addElement(new Button(this, 3, 0, 0, Lang.getTranslatedString("opencraft:gui.text.viewbobbing"), () -> {
            OptionsRegistry.updateOption(
                    OptionsRegistry.Values.getOption("enableViewBobbing").setValue(!OptionsRegistry.Values.getBooleanOption("enableViewBobbing")));

            ((Button)getElements().get(3)).setText(Lang.getTranslatedString("opencraft:gui.text.viewbobbing") + ": "
                    + Lang.getTranslatedString("opencraft:gui.text." + (OptionsRegistry.Values.getBooleanOption("enableViewBobbing") ? "enabled" : "disabled")));
        }));

        this.addElement(new Button(this, 4, 0, 0, Lang.getTranslatedString("opencraft:gui.text.show_information"), () -> {
            OptionsRegistry.updateOption(
                    OptionsRegistry.Values.getOption("showInformation").setValue(!OptionsRegistry.Values.getBooleanOption("showInformation")));

            ((Button)getElements().get(4)).setText(Lang.getTranslatedString("opencraft:gui.text.show_information") + ": "
                    + Lang.getTranslatedString("opencraft:gui.text." + (OptionsRegistry.Values.getBooleanOption("showInformation") ? "enabled" : "disabled")));
        }));

        this.addElement(new Button(this, 5, 0, 0, Lang.getTranslatedString("opencraft:gui.text.fog"), () -> {
            OptionsRegistry.updateOption(
                    OptionsRegistry.Values.getOption("enableFog").setValue(!OptionsRegistry.Values.getBooleanOption("enableFog")));

            ((Button)getElements().get(5)).setText(Lang.getTranslatedString("opencraft:gui.text.fog") + ": "
                    + Lang.getTranslatedString("opencraft:gui.text." + (OptionsRegistry.Values.getBooleanOption("enableFog") ? "enabled" : "disabled")));
        }));

        ((Slider)getElements().get(0)).setProgress(OptionsRegistry.Values.getIntOption("guiScale")*10);;
        ((Slider)getElements().get(1)).setProgress(OptionsRegistry.Values.getIntOption("renderDistance")*10);
        ((Button)getElements().get(2)).setText(Lang.getTranslatedString("opencraft:gui.text.fullscreen") + ": "
                + Lang.getTranslatedString("opencraft:gui.text." + (Opencraft.getDisplay().isFullscreen() ? "enabled" : "disabled")));
        ((Button)getElements().get(3)).setText(Lang.getTranslatedString("opencraft:gui.text.viewbobbing") + ": "
                + Lang.getTranslatedString("opencraft:gui.text." + (OptionsRegistry.Values.getBooleanOption("enableViewBobbing") ? "enabled" : "disabled")));
        ((Button)getElements().get(4)).setText(Lang.getTranslatedString("opencraft:gui.text.show_information") + ": "
                + Lang.getTranslatedString("opencraft:gui.text." + (OptionsRegistry.Values.getBooleanOption("showInformation") ? "enabled" : "disabled")));
        ((Button)getElements().get(5)).setText(Lang.getTranslatedString("opencraft:gui.text.fog") + ": "
                + Lang.getTranslatedString("opencraft:gui.text." + (OptionsRegistry.Values.getBooleanOption("enableFog") ? "enabled" : "disabled")));
    }

    @Override
    public void optionUpdated(OptionsRegistry.Option newValue) {
        super.optionUpdated(newValue);
        this.forceElementsToUpdate();
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
                btn.setX(screenWidth / 2f - btn.getWidth() - 10);
                btn.setY(screenHeight / 2f + 10);
            }
            else if (btn.getId() == 4) {
                btn.setX(screenWidth / 2f);
                btn.setY(screenHeight / 2f - btn.getHeight() + 5);
            }
            else if (btn.getId() == 5) {
                btn.setX(screenWidth / 2f - btn.getWidth() - 10);
                btn.setY(screenHeight / 2f + 15 + btn.getHeight());
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
