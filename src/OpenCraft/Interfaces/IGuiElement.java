package OpenCraft.Interfaces;

import OpenCraft.Interfaces.IGuiTick;

public interface IGuiElement extends IGuiTick
{

    void render(int screenWidth, int screenHeight, int scale);

}
