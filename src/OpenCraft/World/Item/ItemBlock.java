package OpenCraft.World.Item;

import OpenCraft.World.Block.Block;

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
