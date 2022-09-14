package com.artingl.opencraft.World.Chunk;

import com.artingl.opencraft.Math.Vector2i;
import com.artingl.opencraft.Math.Vector3i;
import com.artingl.opencraft.Opencraft;
import com.artingl.opencraft.World.Block.Block;
import com.artingl.opencraft.World.Block.BlockRegistry;
import com.artingl.opencraft.World.Direction;
import com.artingl.opencraft.World.Generation.PerlinNoise;
import com.artingl.opencraft.World.Level.ClientLevel;

public class Region {

    private PerlinNoise noise;
    private long randomSeed;
    private short[] buffer;
    private Chunk chunk;
    private boolean isDestroyed;
    private boolean ready;

    public Region(Chunk chunk) {
        this.chunk = chunk;
        this.ready = false;
        this.randomSeed = (Opencraft.getLevel().getSeed() + ((long) chunk.getPosition().x * chunk.getPosition().y));
        this.noise = new PerlinNoise(3, (int) this.randomSeed);
        this.buffer = new short[16 * 16 * ClientLevel.MAX_HEIGHT];
    }

    public Region(Vector2i pos) {
        this.ready = false;
        this.randomSeed = (Opencraft.getLevel().getSeed() + ((long) pos.x * pos.y));
        this.buffer = new short[16 * 16 * ClientLevel.MAX_HEIGHT];
    }

    public void generate() {
        if (this.ready)
            return;

        this.ready = true;
        Opencraft.getLevel().generation.getLevelGeneration().generateRegion(this);

    }

    public Vector2i getPosition() {
        return chunk.getPosition();
    }

    public Block getBlock(int x, int y, int z) {
        if (x > 15 || x < 0 || z > 15 || z < 0 || y > ClientLevel.MAX_HEIGHT-1 || y < 0 || this.buffer == null) {
            return BlockRegistry.Blocks.air;
        }

        return Block.unpackFromShort(this.buffer[(y * 16 + z) * 16 + x]);
    }

    public void setBlock(Block block, int x, int y, int z) {
        if (x > 15 || x < 0 || z > 15 || z < 0 || y > ClientLevel.MAX_HEIGHT-1 || y < 0 || this.buffer == null) {
            return;
        }

        this.buffer[(y * 16 + z) * 16 + x] = block.packToShort();

        if (y + 1 < ClientLevel.MAX_HEIGHT && chunk != null) {
            getBlock(x, y + 1, z).neighborChanged(
                    Opencraft.getLevel(),
                    chunk.translateToRealCoords(x, y + 1, z),
                    Direction.Values.UP,
                    block
            );
        }
    }

    public void setBlock(Block block, Vector3i pos) {
        setBlock(block, pos.x, pos.y, pos.z);
    }

    public Block getBlock(Vector3i pos) {
        return getBlock(pos.x, pos.y, pos.z);
    }

    public Chunk getChunk() {
        return chunk;
    }

    public void destroy() {
        this.isDestroyed = true;
        this.buffer = null;
        this.noise.destroy();
    }

    public float getNoiseValue(int x, int z, int min ,int max, int iterations, float smoothness) {
        return this.noise.getNoiseValue(iterations, x, z, .5f, smoothness, min, max);
    }

    public int randomInteger(int min, int max)
    {
        randomSeed = randomSeed * 1664525 + 1013904223;
        return (int) ((randomSeed >> 24) % (max + 1 - min) + min);
    }

    public boolean isDestroyed() {
        return isDestroyed;
    }

    public long getSeed() {
        return this.randomSeed;
    }

    public boolean isReady() {
        return ready;
    }
}
