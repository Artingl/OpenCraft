package com.artingl.opencraft.World.Block;

public class BlockLava extends Block
{

    public BlockLava()
    {
        super("opencraft:lava");

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
