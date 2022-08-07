package OpenCraft.World;

import OpenCraft.Interfaces.LevelRendererListener;
import OpenCraft.OpenCraft;
import OpenCraft.World.Block.Block;
import OpenCraft.World.Entity.Entity;
import OpenCraft.math.Vector3i;
import OpenCraft.phys.AABB;
import java.util.ArrayList;

public class Level
{

    public enum LevelType {
        OVERWORLD, NETHER
    };

    public static long maxWorldSize = 3000000; // 3 000 000 blocks

    private LevelArrayList blocks;
    private ArrayList<Entity> entities = new ArrayList<>();
    private final LevelType levelType;
    private int seed;
    private int initialSeed;

    public Level(LevelType levelType, int seed)
    {
        blocks = new LevelArrayList();
        this.levelType = levelType;
        this.seed = seed;
        this.initialSeed = seed;
    }

    public void addEntity(Entity entity)
    {
        entities.add(entity);
    }

    public ArrayList<Entity> getEntities()
    {
        return entities;
    }

    public int getRandomNumber(int min, int max)
    {
        seed = seed * 1664525 + 1013904223;
        return (seed >> 24) % (max + 1 - min) + min;
    }

    public Block getBlock(Vector3i pos)
    {
        return getBlock(pos.x, pos.y, pos.z);
    }

    public Block getBlock(int x, int y, int z)
    {
        long coords = ((long)y * maxWorldSize + (long)z) * maxWorldSize + (long)x;

        if (!blocks.contains(coords)) {
            if (y < 64) {
                blocks.set(((long)y * maxWorldSize + (long)z) * maxWorldSize + (long)x, levelType == LevelType.OVERWORLD ? Block.grass_block : Block.bedrock);

                int rnd = getRandomNumber(1, 5);
                for (int i = y; i < y + rnd; i++)
                    blocks.set(((long)i * maxWorldSize + (long)z) * maxWorldSize + (long)x, levelType == LevelType.OVERWORLD ? Block.grass_block : Block.bedrock);
            }
            else {
                blocks.set(coords, Block.air);
            }
        }

        return blocks.get(coords);
    }

    public void removeBlock(int x, int y, int z)
    {
        setBlock(x, y, z, Block.air);
    }

    public void setBlock(int x, int y, int z, Block block)
    {
        blocks.set(((long)y * maxWorldSize + (long)z) * maxWorldSize + (long)x, block);

        OpenCraft.getLevelRenderer().sendEvent(
                LevelRendererListener.Events.CHUNK_UPDATE,
                this,
                OpenCraft.getLevelRenderer().getChunkByBlockPos(x, z)
        );
    }

    public ArrayList<AABB> getCubes(AABB box) {
        ArrayList<AABB> boxes = new ArrayList<>();
        int x0 = (int)Math.floor((double)box.x0);
        int x1 = (int)Math.floor((double)(box.x1 + 1.0F));
        int y0 = (int)Math.floor((double)box.y0);
        int y1 = (int)Math.floor((double)(box.y1 + 1.0F));
        int z0 = (int)Math.floor((double)box.z0);
        int z1 = (int)Math.floor((double)(box.z1 + 1.0F));

        for(int x = x0; x < x1; ++x) {
            for(int y = y0; y < y1; ++y) {
                for(int z = z0; z < z1; ++z) {
                    if (getBlock(x, y, z) == null) continue;
                    if (getBlock(x, y, z).isVisible() && !getBlock(x, y, z).isLiquid()) {
                        boxes.add(new AABB((float)x, (float)y, (float)z, (float)(x + 1), (float)(y + 1), (float)(z + 1)));
                    }
                }
            }
        }

        return boxes;
    }

    public boolean isLit(int x, int y, int z) {
        return true;
    }

    public boolean containsLiquid(AABB box) {
        int x0 = (int)Math.floor(box.x0);
        int x1 = (int)Math.floor((box.x1 + 1.0F));
        int y0 = (int)Math.floor(box.y0);
        int y1 = (int)Math.floor((box.y1 + 1.0F));
        int z0 = (int)Math.floor(box.z0);
        int z1 = (int)Math.floor((box.z1 + 1.0F));

        if (x0 < 0) {
            x0 = 0;
        }

        if (y0 < 0) {
            y0 = 0;
        }

        if (z0 < 0) {
            z0 = 0;
        }

        for(int x = x0; x < x1; ++x) {
            for(int y = y0; y < y1; ++y) {
                for(int z = z0; z < z1; ++z) {
                    if (getBlock(x, y, z) == null) continue;
                    if (getBlock(x, y, z).isLiquid()) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
    public int getSeed() {
        return this.initialSeed;
    }

    public void destroy()
    {
        blocks.clear();
        blocks = null;
    }
}
