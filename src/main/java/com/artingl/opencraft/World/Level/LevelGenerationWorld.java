package com.artingl.opencraft.World.Level;
import com.artingl.opencraft.World.Block.BlockRegistry;
import com.artingl.opencraft.World.Chunk.Region;

public class LevelGenerationWorld extends LevelGeneration
{

    public LevelGenerationWorld(ClientLevel.Generation generation)
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

//            if (level.generation.randomInteger(10, 20) == level.generation.randomInteger(10, 20)) {
//                region.setBlock(BlockRegistry.Blocks.grass, region_x, y+1, region_z);
//            }
//
//            if (level.generation.randomInteger(10, 25) == level.generation.randomInteger(10, 25)) {
//                region.setBlock(BlockRegistry.Blocks.rose, region_x, y+1, region_z);
//            }
        }

//        if (!wasSand && level.generation.randomInteger(20, 1500) == level.generation.randomInteger(20, 1500))
//        {
//            int treeHeight = level.generation.randomInteger(5, 7);
//            generateTree(region, treeHeight, region_x, y + 1, region_z);
//        }

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

    public void generateTree(Region region, int treeHeight, int x, int y, int z)
    {
        for (int i = x + -2; i < x + 3; i++)
        {
            for (int j = z + -2; j < z + 3; j++)
            {
                for (int k = y + treeHeight - 2; k < y + treeHeight; k++)
                {
                    region.setBlock(BlockRegistry.Blocks.leaves_oak, i, k, j);
                }
            }
        }
        for (int i = treeHeight; i < treeHeight + 1; i++)
        {
            for (int j = -1; j < 2; j++)
            {
                for (int k = -1; k < 2; k++)
                {
                    region.setBlock(BlockRegistry.Blocks.leaves_oak, x + j, y + i, z + k);
                }
            }
        }
        int cl = 2;
        for (int i = treeHeight + 1; i < treeHeight + 2; i++)
        {
            for (int j = -1; j < 2; j++)
            {
                for (int k = -1; k < 2; k++)
                {
                    if (cl % 2 != 0)
                    {
                        region.setBlock(BlockRegistry.Blocks.leaves_oak, x + j, y + i, z + k);
                    }
                    cl++;
                }
            }
        }
        for (int i = y; i < y + treeHeight; i++)
        {
            region.setBlock(BlockRegistry.Blocks.log_oak, x, i, z);
        }

        region.setBlock(BlockRegistry.Blocks.leaves_oak, x, y + treeHeight + 1, z);
    }

    @Override
    public void destroy() {
        super.destroy();
    }

}
