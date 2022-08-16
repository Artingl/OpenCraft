package com.artingl.opencraft.World.Entity.Gamemode;

public class Survival extends Gamemode {

    public static final int id = 0;

    public static Survival instance = new Survival();

    private Survival() {}

    public int getId() {
        return Survival.id;
    }
}
