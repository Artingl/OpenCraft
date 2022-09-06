package com.artingl.opencraft.World.Level;
import com.artingl.opencraft.World.Block.BlockRegistry;
import com.artingl.opencraft.World.Chunk.Region;

public class LevelGenerationExtremeBiomes extends LevelGenerationWorld
{

    public LevelGenerationExtremeBiomes(ClientLevel.Generation generation)
    {
        super(generation);
    }

    @Override
    public void generateRegion(Region region, int region_x, int region_z, int world_x, int world_z)
    {
        int y = getHeightValue(world_x, world_z);
        boolean wasSand = false;

        region.setBlock(BlockRegistry.Blocks.bedrock, region_x, 0, region_z);

        for (int i = 0; i < level.generation.randomInteger(0, 3); i++) {
            region.setBlock(BlockRegistry.Blocks.bedrock, region_x, i, region_z);
        }

        if (y < ClientLevel.WATER_LEVEL - 1)
        {
            region.setBlock(BlockRegistry.Blocks.sand, region_x, y, region_z);
            wasSand = true;
        }
        else
        {
            region.setBlock(BlockRegistry.Blocks.grass_block, region_x, y, region_z);

            if (level.generation.randomInteger(10, 20) == level.generation.randomInteger(10, 20)) {
                region.setBlock(BlockRegistry.Blocks.grass, region_x, y+1, region_z);
            }

            if (level.generation.randomInteger(10, 25) == level.generation.randomInteger(10, 25)) {
                region.setBlock(BlockRegistry.Blocks.rose, region_x, y+1, region_z);
            }
        }

        if (!wasSand && level.generation.randomInteger(20, 1500) == level.generation.randomInteger(20, 1500))
        {
            int treeHeight = level.generation.randomInteger(5, 7);
            generateTree(region, treeHeight, region_x, y + 1, region_z);
        }

        for (int i = 1; i < y; i++)
        {
            if (!wasSand)
            {
                if (i > y - level.generation.randomInteger(5, 10))
                {
                    region.setBlock(BlockRegistry.Blocks.dirt, region_x, i, region_z);
                }
                else
                {
                    region.setBlock(BlockRegistry.Blocks.stone, region_x, i, region_z);
                }
            }
            else
            {
                if (i > y - level.generation.randomInteger(5, 10))
                {
                    region.setBlock(BlockRegistry.Blocks.sandStone, region_x, i, region_z);
                }
                else
                {
                    region.setBlock(BlockRegistry.Blocks.stone, region_x, i, region_z);
                }
            }

        }
        if (y < ClientLevel.WATER_LEVEL - 3)
        {
            for (int i = y; i < ClientLevel.WATER_LEVEL - 3; i++)
            {
                region.setBlock(BlockRegistry.Blocks.water, region_x, i + 1, region_z);
            }
        }
    }

    @Override
    public int getHeightValue(int x, int z) {
        return this.noise.getNoiseValue(38, (float) x, (float) z, .5f, .01f, -15, 380) + ClientLevel.WATER_LEVEL;
    }

    @Override
    public void destroy() {
        super.destroy();
    }

}
