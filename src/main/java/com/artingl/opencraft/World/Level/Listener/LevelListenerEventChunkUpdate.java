package com.artingl.opencraft.World.Level.Listener;

import com.artingl.opencraft.Math.Vector3f;
import com.artingl.opencraft.World.Chunk.Chunk;

public class LevelListenerEventChunkUpdate extends LevelListenerEvent {

    private final Chunk chunk;

    public LevelListenerEventChunkUpdate(Chunk chunk, Vector3f position) {
        super(position);
        this.chunk = chunk;
    }

    public Chunk getChunk() {
        return chunk;
    }
}
