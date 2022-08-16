package com.artingl.opencraft.World.Block;

public class BlockLava extends Block
{

    public BlockLava(int idi)
    {
        super("opencraft:lava", idi);

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
