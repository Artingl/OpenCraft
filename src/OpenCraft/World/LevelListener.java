package OpenCraft.World;

import OpenCraft.World.Chunk.Chunk;
import OpenCraft.World.Level.Level;

public interface LevelListener {

    enum Events {
      CHUNK_UPDATE, LEVEL_DESTROY, LEVEL_SWITCH,
    };

    void chunkUpdate(Level level, Chunk chunk);
    void levelDestroy(Level level);
    void levelSwitch(Level level);

}
