package com.artingl.opencraft.World.Level;
import com.artingl.opencraft.World.Block.BlockRegistry;
import com.artingl.opencraft.World.Chunk.Region;

public class LevelGenerationSuperFlat extends LevelGeneration
{

    public LevelGenerationSuperFlat(ClientLevel.Generation generation)
    {
        super(generation);
    }

    @Override
    public void generateRegion(Region region, int region_x, int region_z, int world_x, int world_z)
    {
        region.setBlock(BlockRegistry.Blocks.bedrock, region_x, 0, region_z);
        region.setBlock(BlockRegistry.Blocks.grass_block, region_x, 4, region_z);

        for (int i = 1; i <= 3; i++) {
            region.setBlock(BlockRegistry.Blocks.dirt, region_x, i, region_z);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
    }

}
