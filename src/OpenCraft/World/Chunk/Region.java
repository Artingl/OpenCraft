package OpenCraft.World.Chunk;

import OpenCraft.OpenCraft;
import OpenCraft.World.Block.Block;
import OpenCraft.World.Chunk.Chunk;
import OpenCraft.World.Level.Level;
import OpenCraft.math.Vector2i;
import OpenCraft.math.Vector3i;

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
    }

    public void destroy() {
        this.blocks = null;
    }
}
