package com.artingl.opencraft.World.Level;

import com.artingl.opencraft.World.Block.BlockRegistry;
import com.artingl.opencraft.World.Level.Generation.LevelGeneration;
import com.artingl.opencraft.World.Level.Listener.LevelListener;
import com.artingl.opencraft.World.Level.Listener.LevelListenerEventChunkUpdate;
import com.artingl.opencraft.World.Tick;
import com.artingl.opencraft.Control.RenderInterface;
import com.artingl.opencraft.Opencraft;
import com.artingl.opencraft.World.Ambient.Ambient;
import com.artingl.opencraft.World.Block.Block;
import com.artingl.opencraft.World.Chunk.Chunk;
import com.artingl.opencraft.World.Chunk.Region;
import com.artingl.opencraft.Math.Vector2i;
import com.artingl.opencraft.Math.Vector3f;
import com.artingl.opencraft.Math.Vector3i;
import com.artingl.opencraft.Phys.AABB;

import java.util.ArrayList;
import java.util.HashMap;

public class ClientLevel implements RenderInterface, Tick
{
    public static final int WATER_LEVEL = 52;
    public static final int MIN_TERRAIN_LEVEL = 64;
    public static final float TREES_MAX_Y_SPAWN = 74;
    public static int MAX_HEIGHT = 256;

    public static class Generation {

        private LevelGeneration levelGeneration;
        private final ClientLevel level;

        public Generation(ClientLevel level) {
            this.level = level;
        }

        public void setSeed(int seed) {
            levelGeneration = LevelRegistry.createLevelGenerationInstance(level.levelType, this);
        }

        public LevelGeneration getLevelGeneration() {
            return levelGeneration;
        }

        public ClientLevel getLevel() {
            return level;
        }

        public int getSeed() {
            return level.seed;
        }

        public void destroy() {
            this.levelGeneration.destroy();
        }
    }


    public final ClientLevel.Generation generation;
    private final LevelTypes levelType;
    private Ambient ambient;
    private int glUpdateEvent;
    private int tickEvent;
    private int seed;
    private HashMap<Vector3i, Block> blocksQueue;

    public ClientLevel()
    {
        this.seed = -1;
        this.levelType = LevelTypes.WORLD;
        this.ambient = new Ambient(this);
        this.generation = new Generation(this);
        this.blocksQueue = new HashMap<>();

        this.glUpdateEvent = Opencraft.registerRenderEvent(this);
        this.tickEvent = Opencraft.registerTickEvent(this);
    }

    public int getSeed() {
        return seed;
    }

    public void setSeed(int seed) {
        this.seed = seed;
        this.generation.setSeed(seed);
    }

    public Vector3f getSkyColor() {
        return new Vector3f(0.5f, 0.7f, 1);
    }

    public LevelTypes getLevelType() {
        return levelType;
    }

    public Chunk getChunkByBlockPos(Vector2i position) {
        return getChunkByBlockPos(position.x, position.y);
    }

    public Chunk getChunkByBlockPos(int x, int z) {
        return Opencraft.getLevelController().getChunkByBlockPos(x, z);
    }

    public Block getBlock(int x, int y, int z)
    {
        Chunk chunk = Opencraft.getLevelController().getChunkByBlockPos(x, z);
        Vector3i blockPos = new Vector3i(x, y, z);

        if (chunk == null) {
            return BlockRegistry.Blocks.air;
        }

        Region region = chunk.getRegion();
        if (region == null) {
            return BlockRegistry.Blocks.air;
        }

        if (blocksQueue.containsKey(blockPos)) {
            Block block = blocksQueue.get(blockPos);
            blocksQueue.remove(blockPos);
            region.setBlock(block,
                    x - (x >> 4) * 16,
                    y,
                    z - (z >> 4) * 16
            );
        }

        return region.getBlock(
                x - (x >> 4) * 16,
                y,
                z - (z >> 4) * 16
        );
    }

    public void setBlockQuietly(Block block, int x, int y, int z) {
        Chunk chunk = Opencraft.getLevelController().getChunkByBlockPos(x, z);
        Vector3i blockPos = new Vector3i(x, y, z);

        if (chunk == null) {
            blocksQueue.put(blockPos, block);
            return;
        }

        Region region = chunk.getRegion();
        if (region == null) {
            blocksQueue.put(blockPos, block);
            return;
        }

        if (blocksQueue.containsKey(blockPos)) {
            block = blocksQueue.get(blockPos);
            blocksQueue.remove(blockPos);
        }

        region.setBlock(block,
                x - (x >> 4) * 16,
                y,
                z - (z >> 4) * 16
        );
    }

    public void setBlock(Block block, int x, int y, int z)
    {
        this.setBlockQuietly(block, x, y, z);

        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                for (int k = -1; k < 2; k++) {
                    Opencraft.getLevelController().sendEvent(
                            LevelListener.Events.CHUNK_UPDATE,
                            this,
                            new LevelListenerEventChunkUpdate(
                                Opencraft.getLevelController().getChunkByPos((x >> 4) + i, (z >> 4) + j),
                                new Vector3f(x, y + k, z)
                            )
                    );
                }
            }
        }
    }

    public void removeBlock(Vector3i position)
    {
        removeBlock(position.x, position.y, position.z);
    }

    public void removeBlock(int x, int y, int z)
    {
        setBlock(BlockRegistry.Blocks.air, x, y, z);
    }

    public void setBlockQuietly(Block block, Vector3i pos) { setBlockQuietly(block, pos.x, pos.y, pos.z); }

    public void setBlock(Block block, Vector3i pos)
    {
        setBlock(block, pos.x, pos.y, pos.z);
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
                    Block block = getBlock(x, y, z);

                    if (block == null) continue;
                    if (block.isCollidable()) {
                        boxes.add(new AABB((float)x, (float)y, (float)z, (float)(x + 1), (float)(y + 1), (float)(z + 1)));
                    }
                }
            }
        }

        return boxes;
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

    public Generation getGeneration() {
        return generation;
    }

    public void destroy()
    {
        // todo: destroy all entities
        this.generation.destroy();
        this.blocksQueue.clear();

        Opencraft.unregisterRenderEvent(this.glUpdateEvent);
        Opencraft.unregisterTickEvent(this.tickEvent);
    }

    @Override
    public void render() {
        this.ambient.update();
    }

    @Override
    public void tick() {
        this.ambient.tick();
    }

    @Override
    public String toString() {
        return "ClientLevel{type=" + levelType + "}";
    }
}
