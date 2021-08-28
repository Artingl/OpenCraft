package OpenCraft.Game.Gui;

import OpenCraft.Interfaces.IGuiElement;
import OpenCraft.OpenCraft;

public class GuiElement implements IGuiElement
{

    protected float x;
    protected float y;
    protected float width;
    protected float height;

    public GuiElement(float x, float y, float width, float height)
    {
        OpenCraft.registerGuiTickEvent(this);

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public void tick(int screenWidth, int screenHeight) { }
}
