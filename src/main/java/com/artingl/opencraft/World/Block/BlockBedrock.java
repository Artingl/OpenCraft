package com.artingl.opencraft.World.Block;

import com.artingl.opencraft.World.Item.Tool;

public class BlockBedrock extends Block
{

    public BlockBedrock()
    {
        super("opencraft:bedrock");
        this.setDefaultTool(Tool.UNBREAKABLE);
    }

}
