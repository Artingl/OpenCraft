package com.artingl.opencraft.World.Chunk;

import com.artingl.opencraft.Math.Vector2i;
import com.artingl.opencraft.Opencraft;
import com.artingl.opencraft.World.Block.Block;
import com.artingl.opencraft.World.Block.BlockRegistry;
import com.artingl.opencraft.World.Level.ClientLevel;

import java.util.ArrayList;

public class Region {

    private final Chunk chunk;

    private Block[][][] blocks;

    private boolean isDestroyed;

    public Region(Chunk chunk) {
        this.chunk = chunk;
        this.blocks = new Block[16][ClientLevel.MAX_HEIGHT][16];
    }

    public void generate() {
        Vector2i pos = chunk.getPosition();

        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                Opencraft.getLevel().generation.getLevelGeneration().generateRegion(
                        this,
                        i,
                        j,
                        (pos.x * 16) + i, (pos.y * 16) + j);
            }
        }
    }

    public Block getBlock(int x, int y, int z) {
        if (x > 15 || x < 0 || z > 15 || z < 0 || y > ClientLevel.MAX_HEIGHT-1 || y < 0 || this.blocks == null) {
            return BlockRegistry.Blocks.air;
        }

        if (this.blocks[x][y][z] == null)
            return BlockRegistry.Blocks.air;

        return this.blocks[x][y][z];
    }

    public void setBlock(Block block, int x, int y, int z) {
        if (x > 15 || x < 0 || z > 15 || z < 0 || y > ClientLevel.MAX_HEIGHT-1 || y < 0 || this.blocks == null) {
            return;
        }

        this.blocks[x][y][z] = block;

//        if (y + 1 < Chunk.MAX_HEIGHT) {
//            getBlock(x, y + 1, z).neighborChanged(
//                    OpenCraft.getLevel(),
//                    chunk.translateToRealCoords(x, y + 1, z),
//                    Direction.Values.UP,
//                    block
//            );
//        }
    }

    public Chunk getChunk() {
        return chunk;
    }

    public void destroy() {
        this.blocks = null;
        this.isDestroyed = true;
    }

    public Block[][][] getBlocks() {
        return blocks;
    }


    public boolean isDestroyed() {
        return isDestroyed;
    }

}
