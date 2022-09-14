package com.artingl.opencraft.Control.Game;

public class Texture
{

    private float top_x;
    private float top_y;
    private float bottom_x;
    private float bottom_y;
    private float side_x;
    private float side_y;
    private final int id;

    public Texture(int id, float top_x, float top_y, float bottom_x, float bottom_y, float side_x, float side_y)
    {
        this.top_x = top_x;
        this.top_y = top_y;
        this.bottom_x = bottom_x;
        this.bottom_y = bottom_y;
        this.side_x = side_x;
        this.side_y = side_y;
        this.id = id;
    }

    public float getTopTextureX()
    {
        return this.top_x;
    }

    public float getTopTextureY()
    {
        return this.top_y;
    }

    public float getBottomTextureX()
    {
        return this.bottom_x;
    }

    public float getBottomTextureY()
    {
        return this.bottom_y;
    }

    public float getSideTextureX()
    {
        return this.side_x;
    }

    public float getSideTextureY()
    {
        return this.side_y;
    }


    public void setTopTextureX(int i)
    {
        this.top_x = i;
    }

    public void setTopTextureY(int i)
    {
        this.top_y = i;
    }

    public void setBottomTextureX(int i)
    {
        this.bottom_x = i;
    }

    public void setBottomTextureY(int i)
    {
        this.bottom_y = i;
    }

    public void setSideTextureX(int i)
    {
        this.side_x = i;
    }

    public void setSideTextureY(int i)
    {
        this.side_y = i;
    }

    public int getId() {
        return id;
    }
}
