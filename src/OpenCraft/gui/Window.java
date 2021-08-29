package OpenCraft.gui;

import OpenCraft.Interfaces.IGuiInterface;
import OpenCraft.OpenCraft;

public class Window extends Screen implements IGuiInterface {

    public Window(int w, int h, String title)
    {
        super(w, h, title);
        OpenCraft.registerGuiInterfaceEvent(this);
    }

    @Override
    public void render(int screenWidth, int screenHeight, int scale)
    {
        super.render(screenWidth, screenHeight, scale);
    }

}
