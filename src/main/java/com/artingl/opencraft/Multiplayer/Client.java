package com.artingl.opencraft.Multiplayer;

import com.artingl.opencraft.GUI.GUI;
import com.artingl.opencraft.Logger.Logger;
import com.artingl.opencraft.Multiplayer.Packet.Packet;
import com.artingl.opencraft.Multiplayer.Packet.PacketHandshake;
import com.artingl.opencraft.Multiplayer.World.Entity.EntityMP;
import com.artingl.opencraft.Opencraft;
import com.artingl.opencraft.Utils.Utils;
import com.artingl.opencraft.World.Entity.Entity;
import com.artingl.opencraft.World.EntityData.UUID;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class Client {

    private String host;
    private int port;

    private Thread handlerThread;
    private Socket connection;
    private boolean isActive;
    private boolean checked;

    private final ArrayList<EntityMP> entities;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
        this.isActive = false;
        this.entities = new ArrayList<>();
    }

    public void create() {
        try {
            Logger.debug("Connecting to the server on " + this.host + ":" + this.port);
            this.connection = new Socket(this.host, this.port);
            this.createHandler();
        } catch (Exception e) {
            Logger.exception("Unable to connect to the server", e);
            Opencraft.setCurrentScreen(GUI.mainMenu);
        }
    }

    private void createHandler() {
        Logger.debug("Running client handler");
        this.checked = false;
        this.handlerThread = new Thread(() -> {
            try {
                DataInputStream dataInputStream = new DataInputStream(connection.getInputStream());
                DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());

                PacketHandshake handshake = new PacketHandshake(this, connection);
                handshake.sendHandshake();

                byte magic_0 = dataInputStream.readByte();
                byte magic_1 = dataInputStream.readByte();
                while (magic_0 != Packet.MAGIC_0 || magic_1 != Packet.MAGIC_1) {
                    Logger.error("Bad magic " + magic_0 + " " + magic_1);
                    magic_0 = dataInputStream.readByte();
                    magic_1 = dataInputStream.readByte();
                }

                if (dataInputStream.readInt() == 1) {
                    int seed = dataInputStream.readInt();

                    Opencraft.getLevel().setSeed(seed);

                    this.isActive = true;

                    while (this.isActive) {
                        try {
                            magic_0 = dataInputStream.readByte();
                            magic_1 = dataInputStream.readByte();
                            while (magic_0 != Packet.MAGIC_0 || magic_1 != Packet.MAGIC_1) {
                                magic_0 = dataInputStream.readByte();
                                magic_1 = dataInputStream.readByte();
                            }

                            int id = dataInputStream.readInt();
                            Packet packetInput = Packet.getPacketById(id, this, connection);

                            if (packetInput != null) {
                                packetInput.executeClientSide();
                            } else {
                                Logger.error("Bad packet with id " + id);
                            }
                        } catch (Exception e) {
                            String kl = e.getLocalizedMessage();

                            if (kl != null) {
                                if (kl.contains("Socket closed")) {
                                    Logger.error("Socket connection closed");
                                    this.isActive = false;
                                } else
                                    Logger.exception("Received bad packet!", e);
                            } else
                                Logger.exception("Received bad packet!", e);
                        }

//                        if (!this.checked) {
//                            this.checked = true;
//                            Utils.createThread(() -> {
//                                try {
//                                    this.isActive = connection.getInetAddress().isReachable(Server.TIMEOUT);
//                                    this.checked = false;
//                                } catch (IOException e) {
//                                    this.isActive = false;
//                                }
//                            });
//                        }
                    }
                }

                connection.close();

            } catch (Exception e) {
                Logger.exception("Unable to connect to the server", e);
            }
        });

        this.handlerThread.setDaemon(true);
        this.handlerThread.start();
    }

    public void tick() {
        for (EntityMP entityMP : this.entities) {
            entityMP.tick();
        }
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Socket getConnection() {
        return connection;
    }

    public boolean isActive() {
        return isActive;
    }

    public void destroy() throws IOException {
        this.handlerThread.interrupt();

        for (EntityMP entityMP : this.entities) {
            entityMP.destroy();
        }

        this.entities.clear();
        this.connection.close();
    }

    public void connect(String host, int port) {
        this.host = host;
        this.port = port;
        this.create();
    }

    public void close() throws IOException {
        this.isActive = false;
        this.connection.close();
        this.handlerThread.interrupt();
    }

    public EntityMP getEntitySave(UUID uuid, Entity.Types entityType) {
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

    public ArrayList<EntityMP> getEntities() {
        return entities;
    }

}
