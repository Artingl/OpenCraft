package com.artingl.opencraft.World.Level;

import com.artingl.opencraft.World.Block.BlockRegistry;
import com.artingl.opencraft.World.Tick;
import com.artingl.opencraft.Rendering.RenderHandler;
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

public class ClientLevel implements RenderHandler, Tick
{
    public static final int WATER_LEVEL = 64;
    public static int MAX_HEIGHT = 256;

    public static class Generation {
        private int randomSeed;

        private LevelGeneration levelGeneration = null;
        private final ClientLevel level;

        public Generation(ClientLevel level) {
            this.randomSeed = level.seed;
            this.level = level;

            MAX_HEIGHT = 256;

            if (level.levelType == LevelType.WORLD) {
                levelGeneration = new LevelGenerationWorld(this);
            }
        }

        public int randomInteger(int min, int max)
        {
            randomSeed = randomSeed * 1664525 + 1013904223;
            return (randomSeed >> 24) % (max + 1 - min) + min;
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
    }


    public final ClientLevel.Generation generation;
    private final LevelType levelType;
    private Ambient ambient;
    private int glUpdateEvent;
    private int tickEvent;
    private int seed;

    public ClientLevel()
    {
        this.seed = -1;
        this.levelType = LevelType.WORLD;
        this.ambient = new Ambient(this);
        this.generation = new Generation(this);

        this.glUpdateEvent = Opencraft.registerRenderEvent(this);
        this.tickEvent = Opencraft.registerTickEvent(this);
    }

    public int getSeed() {
        return seed;
    }

    public void setSeed(int seed) {
        this.seed = seed;
    }

    public boolean isReady() {
        return seed != -1;
    }

    public Vector3f getSkyColor() {
        return new Vector3f(0.5f, 0.7f, 1);
    }

    public LevelType getLevelType() {
        return levelType;
    }

    public Chunk getChunkByBlockPos(Vector2i position) {
        return getChunkByBlockPos(position.x, position.y);
    }

    public Chunk getChunkByBlockPos(int x, int z) {
        return Opencraft.getLevelRenderer().getChunkByBlockPos(x, z);
    }

    public Block getBlock(int x, int y, int z)
    {
        Chunk chunk = Opencraft.getLevelRenderer().getChunkByBlockPos(x, z);
        if (chunk == null) {
            return BlockRegistry.Blocks.air;
        }

        Region region = chunk.getRegion();
        if (region == null) {
            return BlockRegistry.Blocks.air;
        }

        return region.getBlock(
                x - (x >> 4) * 16,
                y,
                z - (z >> 4) * 16
        );
    }

    public void setBlockWithoutRendering(int x, int y, int z, Block block) {
        Chunk chunk = Opencraft.getLevelRenderer().getChunkByBlockPos(x, z);
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
                    Opencraft.getLevelRenderer().sendEvent(
                            LevelListener.Events.CHUNK_UPDATE,
                            this,
                            Opencraft.getLevelRenderer().getChunkByPos((x >> 4) + i, (z >> 4) + j),
                            new Vector3i(x, y + k, z)
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
        setBlock(x, y, z, BlockRegistry.Blocks.air);
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
                    Block block = getBlock(x, y, z);

                    if (block == null) continue;
                    if (block.isVisible() && !block.isLiquid() && !block.isTile()) {
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

    public void destroy()
    {
        // todo: destroy all entities
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
