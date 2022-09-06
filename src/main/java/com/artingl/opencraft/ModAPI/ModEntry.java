package com.artingl.opencraft.ModAPI;

import com.artingl.opencraft.Opencraft;

public abstract class ModEntry {

    public static final String MOD_ID = "";

    public ModEntry() {
        Opencraft.registerMod(this);
    }

    public abstract void onModInitialization() throws Exception;
}
