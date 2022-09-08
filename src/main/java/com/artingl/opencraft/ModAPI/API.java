package com.artingl.opencraft.ModAPI;

public class API {
    private final ModEntry mod;
    protected API(ModEntry mod) {
        this.mod = mod;
    }

    protected void onModDestroy() {}

}
