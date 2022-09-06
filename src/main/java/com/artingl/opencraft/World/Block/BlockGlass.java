package com.artingl.opencraft.World.Block;

public class BlockGlass extends Block
{

    public BlockGlass()
    {
        super("opencraft:glass");

    }

    @Override
    public boolean hasTranslucent() {
        return true;
    }

}
