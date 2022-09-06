package com.artingl.opencraft.World.Block;

public class BlockWater extends Block
{

    public BlockWater()
    {
        super("opencraft:water");

    }

    @Override
    public boolean isLiquid()
    {
        return true;
    }

    @Override
    public boolean hasTranslucent() {
        return true;
    }

}
