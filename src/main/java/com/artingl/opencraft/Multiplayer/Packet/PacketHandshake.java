package com.artingl.opencraft.Multiplayer.Packet;

import com.artingl.opencraft.Opencraft;
import com.artingl.opencraft.World.EntityData.Nametag;
import com.artingl.opencraft.World.EntityData.UUID;

import java.net.Socket;

public class PacketHandshake extends Packet {

    public enum Type {
        SUCCESS, BAD_VERSION, BAD_HEADER
    }

    private Nametag playerNametag;
    private UUID playerUUID;

    public PacketHandshake(){}
    public PacketHandshake(Object clientOrServer, Socket connection) {
        super(clientOrServer, connection);
    }

    public Type handshakeConnection() throws Exception {
        int version = inputStream.readInt();
        playerUUID = new UUID(inputStream.readUTF());
        playerNametag = new Nametag(inputStream.readUTF());
        outputStream.writeInt(getServer().ServerSettings.seed);
        outputStream.flush();

        if (version != Opencraft.getVersion().hashCode()) {
            return Type.BAD_VERSION;
        }

        return Type.SUCCESS;
    }

    @Override
    public String getName() {
        return "PacketHandshake";
    }

    public Nametag getPlayerNametag() {
        return playerNametag;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }
}
