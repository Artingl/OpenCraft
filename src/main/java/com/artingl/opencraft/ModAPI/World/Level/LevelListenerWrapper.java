package com.artingl.opencraft.ModAPI.World.Level;

import com.artingl.opencraft.ModAPI.ModEntry;
import com.artingl.opencraft.Opencraft;
import com.artingl.opencraft.World.Chunk.Chunk;
import com.artingl.opencraft.World.Entity.Entity;
import com.artingl.opencraft.World.EntityData.EntityEvent;
import com.artingl.opencraft.World.Level.ClientLevel;
import com.artingl.opencraft.World.Level.Listener.LevelListener;

public class LevelListenerWrapper implements LevelListener {

    private final ModEntry mod;

    private LevelChunksUpdateListener chunksUpdateListener;
    private LevelEntityUpdateListener entityUpdateListener;
    private LevelSwitchListener levelSwitchListener;

    public LevelListenerWrapper(ModEntry mod) {
        this.mod = mod;
    }

    @Override
    public void chunkUpdate(ClientLevel level, Chunk chunk) {
        if (chunksUpdateListener != null)
            chunksUpdateListener.callback(level, chunk);
    }

    @Override
    public void levelDestroy(ClientLevel level) {

    }

    @Override
    public void levelSwitch(ClientLevel level) {
        if (levelSwitchListener != null)
            levelSwitchListener.callback(level);
    }

    @Override
    public void entityUpdate(ClientLevel level, Entity entity, EntityEvent event) {
        if (entityUpdateListener != null)
            entityUpdateListener.callback(level, entity, event);
    }

    public void setChunksUpdateListener(LevelChunksUpdateListener chunksUpdateListener) {
        this.chunksUpdateListener = chunksUpdateListener;
    }

    public void setLevelSwitchListener(LevelSwitchListener levelSwitchListener) {
        this.levelSwitchListener = levelSwitchListener;
    }

    public void setEntityUpdateListener(LevelEntityUpdateListener entityUpdateListener) {
        this.entityUpdateListener = entityUpdateListener;
    }
}
