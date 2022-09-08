package com.artingl.opencraft.World.Chunk;

import com.artingl.opencraft.World.Entity.EntityPlayer;
import com.artingl.opencraft.World.Level.ClientLevel;

import java.util.Comparator;

public class ChunksEntityPositionSorter implements Comparator<Integer> {
    private final EntityPlayer player;

    public ChunksEntityPositionSorter(EntityPlayer player) {
        this.player = player;
    }

    public int compare(Integer c0, Integer c1) {
        int i0 = c0;
        int i1 = c1;

        int yPosition = (int) (player.getPosition().y / (ClientLevel.MAX_HEIGHT / Chunk.CHUNK_LAYERS));

        if (i0 == i1) {
            return 0;
        }

        return Math.abs(yPosition - i0) < Math.abs(yPosition - i1) ? -1 : 1;
    }
}
