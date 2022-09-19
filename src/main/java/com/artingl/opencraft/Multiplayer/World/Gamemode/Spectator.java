package com.artingl.opencraft.Multiplayer.World.Gamemode;

public class Spectator extends Gamemode {

    public static final int id = 3;

    public static Spectator instance = new Spectator();

    private Spectator() {}

    public int getId() {
        return Spectator.id;
    }

    @Override
    public String toString() {
        return "Gamemode{type=Spectator, id=" + getId() + "}";
    }
}
