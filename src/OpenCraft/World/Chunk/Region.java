package OpenCraft.World.Chunk;

import OpenCraft.OpenCraft;
import OpenCraft.World.Block.Block;
import OpenCraft.World.Direction;
import OpenCraft.World.Level.Level;
import OpenCraft.math.Vector2i;

import java.io.DataOutputStream;
import java.io.IOException;

public class Region {

    private final Chunk chunk;

    private Block[][][] blocks;

    public Region(Chunk chunk) {
        this.chunk = chunk;
        this.blocks = new Block[16][256][16];

        Level level = OpenCraft.getLevel();
        Vector2i chunkPosition = chunk.getPosition();

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                level.getGenerator().generateRegion(this, x, z, chunkPosition.x * 16 + x, chunkPosition.y * 16 + z);
            }
        }
    }

    public Block getBlock(int x, int y, int z) {
        if (x > 15 || x < 0 || z > 15 || z < 0 || y > 255 || y < 0) {
            return Block.air;
        }

        if (this.blocks[x][y][z] == null)
            return Block.air;

        return this.blocks[x][y][z];
    }

    public void setBlock(Block block, int x, int y, int z) {
        if (x > 15 || x < 0 || z > 15 || z < 0 || y > 255 || y < 0) {
            return;
        }

        this.blocks[x][y][z] = block;

        if (y + 1 < 256) {
            getBlock(x, y + 1, z).neighborChanged(
                    OpenCraft.getLevel(),
                    chunk.translateToRealCoords(x, y + 1, z),
                    Direction.Values.UP,
                    block
            );
        }

        OpenCraft.getLevelSaver().appendRegion(this, OpenCraft.getLevel());
    }

    public Chunk getChunk() {
        return chunk;
    }

    public Block[][][] getBlocks() {
        return blocks;
    }

    public void writeBlocksBytes(DataOutputStream local_dos) throws IOException {
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 256; j++) {
                for (int k = 0; k < 16; k++) {
                    Block block = this.blocks[i][j][k];
                    if (block != null) {
                        local_dos.writeInt(i);                // x
                        local_dos.writeInt(j);                // y
                        local_dos.writeInt(k);                // z
                        local_dos.writeInt(block.getIntId()); // block id
                    }
                }
            }
        }
    }

    public void destroy() {
        this.blocks = null;
    }
}
