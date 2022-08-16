package com.artingl.opencraft.World.Block;

public class BlockWater extends Block
{

    public BlockWater(int idi)
    {
        super("opencraft:water", idi);

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
