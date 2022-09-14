package com.artingl.opencraft.Multiplayer.World.Gamemode;

public class Creative extends Gamemode {

    public static final int id = 1;

    public static Creative instance = new Creative();

    private Creative() {}

    public int getId() {
        return Creative.id;
    }

    @Override
    public String toString() {
        return "Gamemode{type=Creative, id=" + getId() + "}";
    }
}
