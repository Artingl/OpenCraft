package OpenCraft.World.Chunk;

import OpenCraft.World.Entity.PlayerController;

import java.util.Comparator;

public class ChunksSorter implements Comparator<Chunk> {
    private final PlayerController player;

    public ChunksSorter(PlayerController player) {
        this.player = player;
    }

    public int compare(Chunk c0, Chunk c1) {
        if (c0 == null || c1 == null)
            return 0;

        if (c0.distanceToEntity(player) == c1.distanceToEntity(player)) {
            return 0;
        }

        return c0.distanceToEntity(player) < c1.distanceToEntity(player) ? -1 : 1;
    }
}
