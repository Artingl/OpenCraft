package com.artingl.opencraft.World.Level.Listener;

import com.artingl.opencraft.World.Chunk.Chunk;
import com.artingl.opencraft.World.Entity.Entity;
import com.artingl.opencraft.World.EntityData.EntityEvent;
import com.artingl.opencraft.World.Level.ClientLevel;

public interface LevelListener {

    enum Events {
      CHUNK_UPDATE, LEVEL_DESTROY, LEVEL_SWITCH, ENTITY_UPDATE
    };

    void chunkUpdate(ClientLevel level, Chunk chunk);
    void levelDestroy(ClientLevel level);
    void levelSwitch(ClientLevel level);
    void entityUpdate(ClientLevel level, Entity entity, EntityEvent event);

}
