package com.artingl.opencraft.World.Block;

public class BlockGlass extends Block
{

    public BlockGlass(int idi)
    {
        super("opencraft:glass", idi);

    }

    @Override
    public boolean hasTranslucent() {
        return true;
    }

}
