package com.artingl.opencraft.World.NBT;

import com.artingl.opencraft.Multiplayer.World.Gamemode.Gamemode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class EntityPlayerNBT extends NBT {

    private Gamemode gamemode;

    public EntityPlayerNBT() {
        super(NBTFieldId.ENTITY_PLAYER);
    }

    public Gamemode getGamemode() {
        return gamemode;
    }

    public void setGamemode(Gamemode gamemode) {
        this.gamemode = gamemode;
    }

    @Override
    public void writeToStream(DataOutputStream dos) throws IOException {
        gamemode.writeToStream(dos);
    }

    @Override
    public void readFromStream(DataInputStream inputStream) throws IOException {
        gamemode = Gamemode.readFromStream(inputStream);
    }
}
