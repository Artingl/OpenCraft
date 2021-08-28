package OpenCraft.World;

import OpenCraft.World.Entity.Player;

import java.util.Comparator;

public class DistanceSorter implements Comparator<Chunk> {
    private final Player player;

    public DistanceSorter(Player player) {
        this.player = player;
    }

    public int compare(Chunk c0, Chunk c1) {
        if (c0.distanceToSqr(player) == c1.distanceToSqr(player)) {
            return 0;
        }

        return c0.distanceToSqr(player) < c1.distanceToSqr(player) ? -1 : 1;
    }
}
