package com.artingl.opencraft.GUI.Windows;

import com.artingl.opencraft.GUI.GuiInterface;
import com.artingl.opencraft.GUI.Screens.Screen;
import com.artingl.opencraft.Opencraft;

public class Window extends Screen implements GuiInterface {

    private int guiInterfaceEvent;

    public Window(int w, int h, String window_id)
    {
        super(w, h, window_id);
        this.showTittle = false;
        this.guiInterfaceEvent = Opencraft.registerGuiInterfaceEvent(this);
    }

    @Override
    public void render(int screenWidth, int screenHeight, int scale)
    {
        super.render(screenWidth, screenHeight, scale);
    }

    @Override
    public void destroy() {
        super.destroy();
        Opencraft.unregisterGuiInterfaceEvent(this.guiInterfaceEvent);
        this.guiInterfaceEvent = -1;
    }
}
