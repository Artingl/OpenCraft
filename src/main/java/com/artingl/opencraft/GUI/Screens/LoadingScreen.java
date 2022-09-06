package com.artingl.opencraft.GUI.Screens;

import com.artingl.opencraft.GUI.Elements.Button;
import com.artingl.opencraft.GUI.Elements.Element;
import com.artingl.opencraft.GUI.GUI;
import com.artingl.opencraft.Opencraft;
import com.artingl.opencraft.Rendering.VerticesBuffer;
import com.artingl.opencraft.Resources.Lang.Lang;

public class LoadingScreen extends Screen
{

    private boolean showCloseButton = false;

    public String loadingText;

    public LoadingScreen() {
        super(Opencraft.getWidth(), Opencraft.getHeight(), "loading_screen");
        this.loadingText = "";
    }

    public void init() {
        super.init();

        this.addElement(new Button(this, 0, 0, 0, Lang.getLanguageString("opencraft:gui.text.close"), () -> {
            Opencraft.setCurrentScreen(GUI.mainMenu);
        }));
    }

    @Override
    public void updateElement(Element element, int screenWidth, int screenHeight, int scale) {
        super.updateElement(element, screenWidth, screenHeight, scale);
        element.setWidth(90);

        if (element instanceof Button btn) {
            if (btn.getId() == 0) {
                btn.setX(screenWidth / 2f - btn.getWidth() / 2f);
                btn.setY(screenHeight / 2f + 10);
            }
        }
    }

    public void render(int screenWidth, int screenHeight, int scale)
    {
        if (getElements().get(0) != null)
            this.updateElement(getElements().get(0), screenWidth, screenHeight, scale);

        drawBackground(VerticesBuffer.getGlobalInstance(), screenWidth, screenHeight, 0x808080);
        Opencraft.getFont().drawShadow(loadingText,
                (screenWidth - Opencraft.getFont().getTextWidth(loadingText)) / 2, (int) (screenHeight / 2f) - 5, 0xFFFFFF);

        if (showCloseButton) {
            getElements().get(0).render(screenWidth, screenHeight, scale);
        }
    }

    public void showCloseButton(boolean b) {
        this.showCloseButton = b;
    }

    public void setLoadingText(String s) {
        setLoadingText(s, false);
    }

    public void setLoadingText(String s, boolean showBtn) {
        this.loadingText = s;
        GUI.loadingScreen.showCloseButton(showBtn);
        GUI.loadingScreen.updateDisplay();
    }
}
