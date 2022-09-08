package com.artingl.opencraft.ModAPI.World.Level;

import com.artingl.opencraft.ModAPI.API;
import com.artingl.opencraft.ModAPI.ModEntry;
import com.artingl.opencraft.Opencraft;

public class LevelAPI extends API {

    private final LevelListenerWrapper listenerWrapper;
    private final int wrapperEvent;

    public LevelAPI(ModEntry mod) {
        super(mod);
        this.listenerWrapper = new LevelListenerWrapper(mod);
        this.wrapperEvent = Opencraft.registerLevelListener(listenerWrapper);
    }

    public void registerLevelSwitchEvent(LevelSwitchListener listener) {
        listenerWrapper.setLevelSwitchListener(listener);
    }

    public void registerChunkUpdateEvent(LevelChunksUpdateListener listener) {
        listenerWrapper.setChunksUpdateListener(listener);
    }

    public void registerEntityUpdateEvent(LevelEntityUpdateListener listener) {
        listenerWrapper.setEntityUpdateListener(listener);
    }

    public void onModDestroy() {
        Opencraft.unregisterLevelListener(this.wrapperEvent);
    }
}
