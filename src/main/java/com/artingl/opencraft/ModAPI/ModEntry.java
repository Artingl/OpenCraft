package com.artingl.opencraft.ModAPI;

import com.artingl.opencraft.ModAPI.World.Block.BlocksApi;
import com.artingl.opencraft.ModAPI.World.Level.LevelAPI;
import com.artingl.opencraft.Opencraft;

public abstract class ModEntry {

    private String modId;
    private boolean initialized;

    private LevelAPI levelAPI;
    private BlocksApi blocksApi;

    public ModEntry() {
        this.initialized = false;
        this.modId = "";

        this.levelAPI = new LevelAPI(this);
        this.blocksApi = new BlocksApi(this);

        Opencraft.registerMod(this);
    }

    public void initialize(String modId) throws Exception {
        if (this.initialized)
            throw new Exception("Mod '" + modId + "' has been already initialized!");

        this.modId = modId;
        this.initialized = true;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public String getModId() {
        return modId;
    }

    public BlocksApi getBlocksApi() {
        return this.blocksApi;
    }

    public LevelAPI getLevelApi() {
        return this.levelAPI;
    }

    public void onModDestroy() {
        this.levelAPI.onModDestroy();
        this.blocksApi.onModDestroy();
    }

    public abstract void onModInitialization() throws Exception;
}
