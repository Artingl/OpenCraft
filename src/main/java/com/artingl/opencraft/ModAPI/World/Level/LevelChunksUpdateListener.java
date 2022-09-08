package com.artingl.opencraft.ModAPI.World.Level;

import com.artingl.opencraft.World.Chunk.Chunk;
import com.artingl.opencraft.World.Level.ClientLevel;

@FunctionalInterface
public interface LevelChunksUpdateListener {
    void callback(ClientLevel level, Chunk chunk);
}
