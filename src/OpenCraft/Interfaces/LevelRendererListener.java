package OpenCraft.Interfaces;

import OpenCraft.World.Chunk;
import OpenCraft.World.Level;

public interface LevelRendererListener {

    enum Events {
      CHUNK_UPDATE, LEVEL_DESTROY, LEVEL_SWITCH,
    };

    void chunkUpdate(Level level, Chunk chunk);
    void levelDestroy(Level level);
    void levelSwitch(Level level);

}
