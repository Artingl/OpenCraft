package OpenCraft.World.Level;

import OpenCraft.Interfaces.LevelRendererListener;
import OpenCraft.OpenCraft;
import OpenCraft.World.Block.Block;
import OpenCraft.World.Chunk.Chunk;
import OpenCraft.World.Chunk.Region;
import OpenCraft.World.Entity.Entity;
import OpenCraft.World.Entity.EntityPlayer;
import OpenCraft.math.Vector3f;
import OpenCraft.math.Vector3i;
import OpenCraft.phys.AABB;
import java.util.ArrayList;

public class Level
{
    public static final int WATER_LEVEL = 64;

    public enum LevelType {
        WORLD, HELL
    };

    private ArrayList<Entity> entities = new ArrayList<>();
    private final LevelType levelType;
    private LevelGeneration levelGeneration;
    private EntityPlayer entityPlayer;
    private int entityPlayerId;
    private int seed;
    private int initialSeed;

    public Level(LevelType levelType, int seed)
    {
        this.levelType = levelType;
        this.seed = seed;
        this.initialSeed = seed;

        if (this.levelType == LevelType.WORLD) {
            this.levelGeneration = new LevelGenerationWorld(this);
        }
        else {
            this.levelGeneration = new LevelGenerationHell(this);
        }
    }

    public void setPlayerEntity(EntityPlayer entityPlayer) {
        addEntity(entityPlayer);
        this.entityPlayerId = entities.size()-1;
        this.entityPlayer = entityPlayer;
    }

    public void addEntity(Entity entity)
    {
        entities.add(entity);
    }

    public ArrayList<Entity> getEntities()
    {
        return entities;
    }
    public EntityPlayer getPlayerEntity()
    {
        return entityPlayer;
    }

    public LevelGeneration getGenerator() {
        return levelGeneration;
    }

    public Vector3f getSkyColor() {
        if (this.levelType == LevelType.HELL) {
            return new Vector3f(0.34f, 0.05f, 0.06f);
        }

        return new Vector3f(0.5f, 0.7f, 1);
    }

    public LevelType getLevelType() {
        return levelType;
    }

    public int getRandomNumber(int min, int max)
    {
        seed = seed * 1664525 + 1013904223;
        return (seed >> 24) % (max + 1 - min) + min;
    }

    public void movePlayerEntity(Level level) {
        this.entities.remove(entityPlayerId);
        level.setPlayerEntity(entityPlayer);
        level.getGenerator().setPlayerSpawnPoint();
        level.getPlayerEntity().teleportToSpawnPoint();
        this.entityPlayerId = -1;
        this.entityPlayer = null;
        this.seed = this.initialSeed;
    }

    public Block getBlock(int x, int y, int z)
    {
        Chunk chunk = OpenCraft.getLevelRenderer().getChunkByBlockPos(x, z);
        if (chunk == null) {
            return Block.air;
        }

        Region region = chunk.getRegion();
        if (region == null) {
            return Block.air;
        }

        return region.getBlock(
                x - (x >> 4) * 16,
                y,
                z - (z >> 4) * 16
        );
    }

    public void setBlockWithoutRendering(int x, int y, int z, Block block) {
        Chunk chunk = OpenCraft.getLevelRenderer().getChunkByBlockPos(x, z);
        if (chunk == null) {
            return;
        }

        Region region = chunk.getRegion();
        if (region == null) {
            return;
        }

        region.setBlock(block,
                x - (x >> 4) * 16,
                y,
                z - (z >> 4) * 16
        );
    }

    public void setBlock(int x, int y, int z, Block block)
    {
        this.setBlockWithoutRendering(x, y, z, block);

        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                for (int k = -1; k < 2; k++) {
                    OpenCraft.getLevelRenderer().sendEvent(
                            LevelRendererListener.Events.CHUNK_UPDATE,
                            this,
                            OpenCraft.getLevelRenderer().getChunkByBlockPos(x + i, z + j),
                            new Vector3i(x, y + k, z)
                    );
                }
            }
        }
    }

    public void removeBlock(int x, int y, int z)
    {
        setBlock(x, y, z, Block.air);
    }

    public void setBlock(Vector3i pos, Block block)
    {
        setBlock(pos.x, pos.y, pos.z, block);
    }

    public Block getBlock(Vector3i pos)
    {
        return getBlock(pos.x, pos.y, pos.z);
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
        // todo: destroy all entities
        this.levelGeneration.destroy();
    }
}
