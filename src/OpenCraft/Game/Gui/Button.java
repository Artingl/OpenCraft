package OpenCraft.Game.Gui;

import OpenCraft.Interfaces.ITick;

public class Button extends GuiElement
{

    private String text;

    public Button(float x, float y, float width, float height, String text)
    {
        super(x, y, width, height);
        this.text = text;
    }

    @Override
    public void tick(int screenWidth, int screenHeight)
    {

    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
