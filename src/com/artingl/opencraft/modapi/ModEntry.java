package com.artingl.opencraft.modapi;

import com.artingl.opencraft.OpenCraft;

public abstract class ModEntry {

    public static final String MOD_ID = "";

    public ModEntry() {
        OpenCraft.registerMod(this);
    }

    public abstract void onModInitialization() throws Exception;
}
