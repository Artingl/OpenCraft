package com.artingl.opencraft.World.Block;

import com.artingl.opencraft.World.Item.Tool;

public class BlockBedrock extends Block
{

    public BlockBedrock(int idi)
    {
        super("opencraft:bedrock", idi);
        this.setDefaultTool(Tool.UNBREAKABLE);
    }

}
