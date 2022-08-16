package com.artingl.opencraft.World.Block;

import com.artingl.opencraft.World.Item.Tool;

public class BlockStone extends Block
{

    public BlockStone(int idi)
    {
        super("opencraft:stone", idi);

        this.setStrength(2);
        this.setDefaultTool(Tool.PICKAXE);

    }

}
