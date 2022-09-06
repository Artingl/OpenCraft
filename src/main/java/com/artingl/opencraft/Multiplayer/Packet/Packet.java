package com.artingl.opencraft.Multiplayer.Packet;

import com.artingl.opencraft.Logger.Logger;
import com.artingl.opencraft.Multiplayer.Client;
import com.artingl.opencraft.Multiplayer.Server;
import com.artingl.opencraft.Multiplayer.Side;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;

public class Packet {

    public static final int MAGIC = 0xFFCA;
    public static Packet[] packets = {
            new PacketHandshake(),
            new PacketUpdateEntity(),
            new PacketKick()
    };

    protected Socket connection;
    protected DataInputStream inputStream;
    protected DataOutputStream outputStream;
    private Object clientOrServer;

    public Packet(){}

    public Packet(Object clientOrServer, Socket connection) {
        this.setConnection(clientOrServer, connection);
    }

    @Side(Server.Side.SERVER)
    public void executeServerSide() throws Exception {}

    @Side(Server.Side.CLIENT)
    public void executeClientSide() throws Exception {}

    public void setConnection(Object clientOrServer, Socket s) {
        try {
            this.connection = s;
            this.inputStream = new DataInputStream(s.getInputStream());
            this.outputStream = new DataOutputStream(s.getOutputStream());
            this.clientOrServer = clientOrServer;
        }
        catch (Exception e) {
            Logger.exception("Error occurred while initializing packet " + getClass().getName(), e);
        }
    }

    public static int getIdByPacket(Packet packet) {
        return getIdByPacketName(packet.getClass().getName());
    }

    public static int getIdByPacketName(String name) {
        int i = 0;
        for (Packet c : packets) {
            if (c.getName().equals(name))
                return i;
            ++i;
        }
        return 0;
    }

    public static Packet getPacketById(int id, Object clientOrServer, Socket connection) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        if (id < 0 || id > packets.length-1)
            return null;

        Packet p = (Packet) packets[id].getClass().getConstructor().newInstance();
        p.setConnection(clientOrServer, connection);
        return p;
    }

    public String getName() {
        return "Packet";
    }

    public Server getServer() {
        return (Server) clientOrServer;
    }

    public Client getClient() {
        return (Client) clientOrServer;
    }

    @Override
    public boolean equals(Object obj) {
        return ((Packet)obj).getName().equals(getName());
    }

}
