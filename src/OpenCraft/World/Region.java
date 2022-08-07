package OpenCraft.World;

import OpenCraft.OpenCraft;
import OpenCraft.World.Block.Block;
import OpenCraft.math.Vector2i;
import OpenCraft.math.Vector3i;

public class Region {

    private final Chunk chunk;

    public Region(Chunk chunk) {
        this.chunk = chunk;
    }

    public Block getBlock(Vector3i position) {
        Vector2i chunkPosition = chunk.getPosition();

        return OpenCraft.getLevel().getBlock(
                (chunkPosition.x * 16) + position.x, position.y, (chunkPosition.y * 16) + position.z
        );
    }

    public void setBlock(Block block, Vector3i position) {
        System.out.println("TODO: implement blocks placement in OpenCraft.World.Region");
    }

}
