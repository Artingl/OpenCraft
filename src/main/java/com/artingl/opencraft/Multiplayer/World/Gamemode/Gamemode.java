package com.artingl.opencraft.Multiplayer.World.Gamemode;

import com.artingl.opencraft.Math.Vector3f;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Gamemode {

    public static int id = -1;

    public static Gamemode getFromId(int id) {
        if (Creative.id == id) {
            return Creative.instance;
        }

        return Survival.instance;
    }

    public int getId() {
        return Gamemode.id;
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
