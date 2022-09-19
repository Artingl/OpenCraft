package com.artingl.opencraft.Multiplayer;

import com.artingl.opencraft.GUI.ScreenRegistry;
import com.artingl.opencraft.Logger.Logger;
import com.artingl.opencraft.Math.Vector3f;
import com.artingl.opencraft.Multiplayer.Packet.Packet;
import com.artingl.opencraft.Multiplayer.Packet.PacketHandshake;
import com.artingl.opencraft.Multiplayer.Packet.PacketKick;
import com.artingl.opencraft.Multiplayer.Packet.PacketEntityUpdate;
import com.artingl.opencraft.Multiplayer.World.Entity.EntityMP;
import com.artingl.opencraft.Multiplayer.World.Entity.EntityPlayerMP;
import com.artingl.opencraft.Opencraft;
import com.artingl.opencraft.Utils.Utils;
import com.artingl.opencraft.World.Block.BlockRegistry;
import com.artingl.opencraft.World.Entity.Entity;
import com.artingl.opencraft.World.Entity.EntityPlayer;
import com.artingl.opencraft.World.EntityData.Nametag;
import com.artingl.opencraft.World.EntityData.UUID;
import com.artingl.opencraft.World.Item.ItemSlot;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    public enum Side {
        SERVER, CLIENT, BOTH
    }

    public static class _ServerSettings {
        public int seed;
    }

    public static final int TIMEOUT = 1500;

    private final ArrayList<EntityMP> entities;
    private String host;
    private int port;
    private ServerSocket server;
    private Thread handlerThread;
    private ArrayList<Connection> connections;
    public final _ServerSettings ServerSettings;

    public Server(String host, int port) {
        this.host = host;
        this.port = port;

        this.ServerSettings = new _ServerSettings();
        this.connections = new ArrayList<>();
        this.entities = new ArrayList<>();
    }

    public void create() {
        try {
            Logger.debug("Binding server on " + this.host + ":" + this.port);
            this.server = new ServerSocket(this.port);
            this.createHandler();
        } catch (Exception e) {
            Logger.exception("Unable to bind server", e);
            Opencraft.setCurrentScreen(ScreenRegistry.mainMenu);
        }
    }

    public ServerSocket getServer() {
        return server;
    }

    public void destroy() throws IOException {
        this.handlerThread.interrupt();
        this.connections.clear();

        this.entities.clear();
        this.server.close();
    }

    public void tick() {
        for (EntityMP entityMP : this.entities) {
            entityMP.tick();
        }
    }

    public void broadcastToEverybody(String text) {
        for (EntityMP e: entities) {
            if (e instanceof EntityPlayerMP) {
                ((EntityPlayerMP) e).tellInChat(text);
            }
        }
    }

    private void createHandler() {
        Logger.debug("Running server handler");

        this.handlerThread = new Thread(() -> {
            while (!server.isClosed()) {
                try {
                    Socket connection = server.accept();

                    // connection thread
                    Utils.createThread(() -> {
                        try {
                            DataInputStream dataInputStream = new DataInputStream(connection.getInputStream());
                            DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());

                            PacketHandshake handshake = new PacketHandshake(this, connection);
                            PacketHandshake.Type handshakeResult = handshake.checkHandshake();

                            if (handshakeResult == PacketHandshake.Type.SUCCESS) {
                                Connection playerConnection = new Connection(connection);
                                connections.add(playerConnection);

                                Logger.info("New player joined! " +
                                        "EntityPlayerMP{uuid=" + handshake.getPlayerUUID() + ", name=" + handshake.getPlayerNametag() + "}");

                                summonPlayerEntity(handshake.getPlayerUUID(), handshake.getPlayerNametag(), connection);

                                while (!connection.isClosed()) {
                                    byte magic_0 = dataInputStream.readByte();
                                    byte magic_1 = dataInputStream.readByte();
                                    while (magic_0 != Packet.MAGIC_0 || magic_1 != Packet.MAGIC_1) {
                                        magic_0 = dataInputStream.readByte();
                                        magic_1 = dataInputStream.readByte();
                                    }

                                    int id = dataInputStream.readInt();
                                    Packet packetInput = Packet.getPacketById(id, this, connection);

                                    if (packetInput != null) {
                                        packetInput.executeServerSide();
                                    }
                                    else {
                                        Logger.error("Bad packet with id" + id);
                                    }
                                }

                                connections.remove(playerConnection);
                            }
                            else {
                                Logger.error("Unable to accept player connection! " + handshakeResult);
                                new PacketKick(null, connection).kick(
                                        handshakeResult == PacketHandshake.Type.BAD_VERSION ?
                                                PacketKick.KickReason.BAD_VERSION : PacketKick.KickReason.SERVER_ERROR);
                            }

                            while (connection.getInetAddress().isReachable(Server.TIMEOUT)){}

                        } catch (Exception e) {
                            Logger.exception("Error in connection thread", e);

                            try {
                                connection.close();
                            } catch (IOException ex) {
                                Logger.exception("Unable to close connection", e);
                            }
                        }
                    });
                } catch (IOException e) {
                    Logger.exception("Unable to receive connection", e);
                }
            }
        });

        this.handlerThread.setDaemon(true);
        this.handlerThread.start();
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public void hostgame() {
        EntityPlayer entityPlayer = Opencraft.getPlayerEntity();
        entityPlayer.tellInChat("Server is running on " + this.host + ":" + this.port);
    }

    public EntityMP getEntity(UUID uuid, Entity.Types entityType) {
        return getEntitySave(uuid, entityType.ordinal());
    }

    public EntityMP getEntitySave(UUID uuid, int entityType) {
        for (EntityMP e: entities) {
            if (e.getNbt().getUUID().equals(uuid) && e.getEntityType().ordinal() == entityType) {
                return e;
            }
        }

        return EntityMP.getDefaultEntityByType(entityType);
    }

    public EntityMP getEntity(UUID uuid, int entityType) {
        for (EntityMP e: entities) {
            if (e.getNbt().getUUID().equals(uuid) && e.getEntityType().ordinal() == entityType) {
                return e;
            }
        }

        return null;
    }

    private void summonPlayerEntity(UUID playerUUID, Nametag nametag, Socket connection) throws IOException {
        EntityPlayerMP playerMP = (EntityPlayerMP) getEntity(playerUUID, Entity.Types.PLAYER);
        playerMP.setConnection(connection);
        playerMP.getNbt().setNameTag(nametag);
        playerMP.getNbt().setUUID(playerUUID);
        playerMP.setPosition(new Vector3f(290, 72, 1053));
        playerMP.setFlyingState(true);

        playerMP.appendInventory(new ItemSlot(BlockRegistry.Blocks.grass_block, 64));
        playerMP.appendInventory(new ItemSlot(BlockRegistry.Blocks.glass, 64));
        playerMP.appendInventory(new ItemSlot(BlockRegistry.Blocks.stone, 64));

        for (Connection conn: connections) {
            PacketEntityUpdate packet = new PacketEntityUpdate(this, conn.getConnection());
            packet.summonEntityPlayer(playerMP);
        }

        for (EntityMP entityMP: entities) {
            PacketEntityUpdate packet = new PacketEntityUpdate(this, connection);

            if (entityMP instanceof EntityPlayerMP) {
                packet.summonEntityPlayer((EntityPlayerMP) entityMP);
            }
            else {
                packet.summonEntity(entityMP);
            }
        }

        entities.add(playerMP);

        broadcastToEverybody("Player " + nametag.getNametag() + " joined the game!");
    }

    public ArrayList<Connection> getConnections() {
        return connections;
    }

    public ArrayList<EntityMP> getEntities() {
        return entities;
    }
}
