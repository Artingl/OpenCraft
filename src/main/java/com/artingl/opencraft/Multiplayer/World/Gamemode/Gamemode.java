package com.artingl.opencraft.Multiplayer.World.Gamemode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Gamemode {

    public static int id = -1;

    public static Gamemode getFromId(int id) {
        if (Creative.id == id) {
            return Creative.instance;
        }

        if (Spectator.id == id) {
            return Spectator.instance;
        }

        return Survival.instance;
    }

    public int getId() {
        return Gamemode.id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Integer) {
            return ((Integer)obj) == getId();
        }
        else if (obj instanceof Gamemode) {
            return ((Gamemode)obj).getId() == getId();
        }

        return false;
    }

    @Override
    public String toString() {
        return "Gamemode{type=null, id=" + getId() + "}";
    }

    public void writeToStream(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeInt(getId());
    }

    public static Gamemode readFromStream(DataInputStream dataInputStream) throws IOException {
        return getFromId(dataInputStream.readInt());
    }
}
