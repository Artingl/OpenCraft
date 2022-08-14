package OpenCraft.gui;

import OpenCraft.OpenCraft;

public class Window extends Screen implements IGuiInterface {

    public Window(int w, int h, String window_id)
    {
        super(w, h, window_id);
        OpenCraft.registerGuiInterfaceEvent(this);
    }

    @Override
    public void render(int screenWidth, int screenHeight, int scale)
    {
        super.render(screenWidth, screenHeight, scale);
    }

}
