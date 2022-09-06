package com.artingl.opencraft.World.Block;

import com.artingl.opencraft.World.Item.Tool;

public class BlockStone extends Block
{

    public BlockStone()
    {
        super("opencraft:stone");

        this.setStrength(2);
        this.setDefaultTool(Tool.PICKAXE);

    }

}
