package com.artingl.opencraft.World.Level.Generation;
import com.artingl.opencraft.Math.Vector3i;
import com.artingl.opencraft.World.Block.BlockRegistry;
import com.artingl.opencraft.World.Chunk.Region;
import com.artingl.opencraft.World.Level.Biomes.*;
import com.artingl.opencraft.World.Level.ClientLevel;

public class LevelGenerationWorld extends LevelGeneration
{

    public LevelGenerationWorld() {
        super();
    }

    @Override
    public void initialize(ClientLevel.Generation generation) {
        super.initialize(generation);
        ClientLevel.MAX_HEIGHT = 256;
    }

    @Override
    public void generateRegion(Region region)
    {
        int chunk_x = region.getPosition().x;
        int chunk_z = region.getPosition().y;

        int x = chunk_x * 16;
        int z = chunk_z * 16;

        int trees_counter = 0;

        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                Biome biome = getBiome(x + i + region.randomInteger(-2, 2), z + j + region.randomInteger(-2, 2));

                float height = this.noise.getNoiseValue(1, (float) i + x, (float) j + z, .5f, .03f, -5, biome.getBiomeHeight());

                float len = 1;

                for (int p = -4; p <= 4; p++) {
                    for (int k = -4; k <= 4; k++) {
                        Biome biome1 = getBiome(x + i + p, z + j + k);

                        height += this.noise.getNoiseValue(1, (float) i + x + p, (float) j + z + k, .5f, .03f, -5, biome1.getBiomeHeight());
                        len += 2;
                    }
                }

                height /= len;
                height += ClientLevel.MIN_TERRAIN_LEVEL;

                height += this.noise.getNoiseValue(30, (float) i + x, (float) j + z, .5f, .05f, -5, 5);

                region.setBlock(BlockRegistry.Blocks.bedrock, i, 0, j);
                biome.createRegionXZ(region, (int) height, i, j);

                for (int y = (int) height; y <= ClientLevel.WATER_LEVEL + biome.getWaterLevelModifier(); y++) {
                    region.setBlock(BlockRegistry.Blocks.water, i, y, j);
                }

                if (region.getBlock(i, (int) (height-1), j).equals(BlockRegistry.Blocks.grass_block)
                        && biome.canTreesSpawn()
                        && region.randomInteger(-20, 20) == region.randomInteger(-20, 20) && trees_counter++ < 10 && height < ClientLevel.TREES_MAX_Y_SPAWN) {
                    spawnTree(region.randomInteger(6, 9), i+x, (int) height, j+z);
                }
            }
        }
    }

    public void spawnTree(int treeHeight, int x, int y, int z)
    {
        for (int i = x + -2; i < x + 3; i++)
        {
            for (int j = z + -2; j < z + 3; j++)
            {
                for (int k = y + treeHeight - 2; k < y + treeHeight; k++)
                {
                    level.setBlock(BlockRegistry.Blocks.leaves_oak, i, k, j);
                }
            }
        }
        for (int i = treeHeight; i < treeHeight + 1; i++)
        {
            for (int j = -1; j < 2; j++)
            {
                for (int k = -1; k < 2; k++)
                {
                    level.setBlock(BlockRegistry.Blocks.leaves_oak, x + j, y + i, z + k);
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
                        level.setBlock(BlockRegistry.Blocks.leaves_oak, x + j, y + i, z + k);
                    }
                    cl++;
                }
            }
        }
        for (int i = y; i < y + treeHeight; i++)
        {
            level.setBlock(BlockRegistry.Blocks.log_oak, x, i, z);
        }

        level.setBlock(BlockRegistry.Blocks.leaves_oak, x, y + treeHeight + 1, z);
    }

    @Override
    public void destroy() {
        super.destroy();
    }

}
