package com.artingl.opencraft.World.Block;

public class BlockLeavesOak extends Block
{

    public BlockLeavesOak(int idi)
    {
        super("opencraft:leaves_oak", idi);

    }

    @Override
    public boolean hasTranslucent() {
        return true;
    }

}
