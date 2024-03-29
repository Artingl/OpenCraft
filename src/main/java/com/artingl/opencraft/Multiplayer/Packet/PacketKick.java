package com.artingl.opencraft.Multiplayer.Packet;

import com.artingl.opencraft.GUI.ScreenRegistry;
import com.artingl.opencraft.Multiplayer.Server;
import com.artingl.opencraft.Multiplayer.Side;
import com.artingl.opencraft.Opencraft;

import java.io.IOException;
import java.net.Socket;

public class PacketKick extends Packet {

    public enum KickReason {
        BAD_VERSION, SERVER_ERROR, ALREADY_ON_SERVER
    }

    public PacketKick(){}
    public PacketKick(Object clientOrServer, Socket connection) {
        super(clientOrServer, connection);
    }


    @Side(Server.Side.CLIENT)
    @Override
    public void executeClientSide() throws Exception {
        System.out.println("kick");
        int kickReason = inputStream.readInt();

        Opencraft.runInGLContext(() -> {
            Opencraft.quitToMainMenu();
            Opencraft.setCurrentScreen(ScreenRegistry.loadingScreen);
            ScreenRegistry.loadingScreen.setLoadingText("Bad version", true);
        });

        Opencraft.getClientConnection().close();
    }

    @Side(Server.Side.SERVER)
    public void kick(KickReason reason) throws IOException {
        outputStream.writeByte(Packet.MAGIC_0);
        outputStream.writeByte(Packet.MAGIC_1);
        outputStream.writeInt(getIdByPacketName(getName()));
        outputStream.writeInt(reason.ordinal());
        if (reason == KickReason.BAD_VERSION)
            outputStream.writeUTF(Opencraft.getVersion());
        outputStream.flush();
    }

    @Override
    public String getName() {
        return "PacketKick";
    }
}
