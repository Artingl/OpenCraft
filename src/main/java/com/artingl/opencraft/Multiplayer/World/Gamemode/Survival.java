package com.artingl.opencraft.Multiplayer.World.Gamemode;

public class Survival extends Gamemode {

    public static final int id = 0;

    public static Survival instance = new Survival();

    private Survival() {}

    public int getId() {
        return Survival.id;
    }

    @Override
    public String toString() {
        return "Gamemode{type=Survival, id=" + getId() + "}";
    }
}
