package com.artingl.opencraft.World.Block;

import com.artingl.opencraft.World.Direction;
import com.artingl.opencraft.World.Item.Tool;
import com.artingl.opencraft.World.Level.Level;
import com.artingl.opencraft.math.Vector3i;

public class BlockGrass extends Block
{

    public BlockGrass(int idi)
    {
        super("opencraft:grass", idi);

        this.setStrength(1);
        this.setDefaultTool(Tool.IMMEDIATELY);

        super.makeRandomization();
    }

    public boolean isTile()
    {
        return true;
    }

    public void createDrop(int x, int y, int z)  {}

    public void neighborChanged(Level level, Vector3i blockPos, Direction.Values direction, Block newBlock) {
        super.neighborChanged(level, blockPos, direction, newBlock);

        if (newBlock.equals(Block.air) && direction.equals(Direction.Values.UP)) {
            this.destroy(blockPos);
            level.removeBlock(blockPos);
        }
    }

}
