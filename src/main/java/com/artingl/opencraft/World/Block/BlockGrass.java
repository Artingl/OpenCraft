package com.artingl.opencraft.World.Block;

import com.artingl.opencraft.World.Direction;
import com.artingl.opencraft.World.Item.Tool;
import com.artingl.opencraft.World.Level.ClientLevel;
import com.artingl.opencraft.Math.Vector3i;

public class BlockGrass extends Block
{

    public BlockGrass()
    {
        super("opencraft:grass");

        this.setStrength(1);
        this.setDefaultTool(Tool.IMMEDIATELY);
    }

    public boolean isTile()
    {
        return true;
    }

    public void createDrop(int x, int y, int z)  {}

    public void neighborChanged(ClientLevel level, Vector3i blockPos, Direction.Values direction, Block newBlock) {
        super.neighborChanged(level, blockPos, direction, newBlock);

        if (newBlock.equals(BlockRegistry.Blocks.air) && direction.equals(Direction.Values.UP)) {
            this.destroy(blockPos);
            level.removeBlock(blockPos);
        }
    }

}
