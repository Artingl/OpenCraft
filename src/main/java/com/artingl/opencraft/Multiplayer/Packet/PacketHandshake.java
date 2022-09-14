package com.artingl.opencraft.Multiplayer.Packet;

import com.artingl.opencraft.Logger.Logger;
import com.artingl.opencraft.Opencraft;
import com.artingl.opencraft.World.EntityData.Nametag;
import com.artingl.opencraft.World.EntityData.UUID;

import java.io.IOException;
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

    public Type checkHandshake() throws Exception {
        byte magic_0 = inputStream.readByte();
        byte magic_1 = inputStream.readByte();
        while (magic_0 != Packet.MAGIC_0 || magic_1 != Packet.MAGIC_1) {
            Logger.error("Bad magic " + magic_0 + " " + magic_1);
            magic_0 = inputStream.readByte();
            magic_1 = inputStream.readByte();
        }

        int version = inputStream.readInt();
        playerUUID = new UUID(inputStream.readUTF());
        playerNametag = new Nametag(inputStream.readUTF());
        inputStream.readUTF();


        outputStream.writeByte(Packet.MAGIC_0);
        outputStream.writeByte(Packet.MAGIC_1);

        if (version != Opencraft.getVersion().hashCode()) {
            outputStream.writeInt(0);
            outputStream.flush();
            return Type.BAD_VERSION;
        }

        outputStream.writeInt(1);
        outputStream.writeInt(getServer().ServerSettings.seed);
        outputStream.flush();

        return Type.SUCCESS;
    }

    public void sendHandshake() throws IOException {
        outputStream.writeByte(Packet.MAGIC_0);
        outputStream.writeByte(Packet.MAGIC_1);
        outputStream.writeInt(Opencraft.getVersion().hashCode());
        outputStream.writeUTF(Opencraft.getPlayerEntity().getUUID().getStringUUID());
        outputStream.writeUTF(Opencraft.getPlayerEntity().getNameTag().getNametag());
        outputStream.writeUTF("done");
        outputStream.flush();
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
