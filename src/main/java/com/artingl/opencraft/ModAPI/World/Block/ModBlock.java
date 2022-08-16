package com.artingl.opencraft.ModAPI.World.Block;

import com.artingl.opencraft.World.Block.Block;

public class ModBlock extends Block {
    public ModBlock(BlockProperties properties) {
        super(properties.id, Block.getNextBlockId());

        this.setTexture(properties.texture);
        this.setStrength(properties.strength);
        this.setDefaultTool(properties.tool);
    }
}
