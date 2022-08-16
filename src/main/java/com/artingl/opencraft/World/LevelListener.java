package com.artingl.opencraft.World;

import com.artingl.opencraft.World.Chunk.Chunk;
import com.artingl.opencraft.World.Level.Level;

public interface LevelListener {

    enum Events {
      CHUNK_UPDATE, LEVEL_DESTROY, LEVEL_SWITCH,
    };

    void chunkUpdate(Level level, Chunk chunk);
    void levelDestroy(Level level);
    void levelSwitch(Level level);

}
