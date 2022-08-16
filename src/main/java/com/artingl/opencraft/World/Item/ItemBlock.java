package com.artingl.opencraft.World.Item;

import com.artingl.opencraft.World.Block.Block;

public class ItemBlock extends Item {

    private Block block;

    public ItemBlock(Block block, int amount) {
        super(block.getStackAmount(), amount);
        this.block = block;
    }

    public Block getBlock() {
        return block;
    }

}
