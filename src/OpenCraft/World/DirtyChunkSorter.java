package OpenCraft.World;

import OpenCraft.World.Entity.Player;
import java.util.Comparator;

public class DirtyChunkSorter implements Comparator<Chunk> {
    private final Player player;

    public DirtyChunkSorter(Player player) {
        this.player = player;
    }

    public int compare(Chunk c0, Chunk c1) {
        boolean i0 = c0.visible;
        boolean i1 = c1.visible;
        if (i0 && !i1) {
            return -1;
        } else if (i1 && !i0) {
            return 1;
        } else if (c0.distanceToSqr(this.player) == c1.distanceToSqr(this.player)) {
            return 0;
        } else {
            return c0.distanceToSqr(this.player) < c1.distanceToSqr(this.player) ? -1 : 1;
        }
    }
}
