package com.artingl.opencraft.Multiplayer.World.Entity;

import com.artingl.opencraft.Logger.Logger;
import com.artingl.opencraft.Math.Vector2f;
import com.artingl.opencraft.Math.Vector3f;
import com.artingl.opencraft.Multiplayer.Packet.PacketEntityUpdate;
import com.artingl.opencraft.Multiplayer.Server;
import com.artingl.opencraft.Multiplayer.Side;
import com.artingl.opencraft.Opencraft;
import com.artingl.opencraft.World.Entity.Entity;
import com.artingl.opencraft.World.Level.ClientLevel;
import com.artingl.opencraft.World.Level.LevelTypes;

import java.net.Socket;

public class EntityMP extends Entity {

    private Socket connection;
    private LevelTypes currentLevel;

    private Vector3f velocity;

    public static Class<?>[] ENTITIES = {
            EntityPlayerMP.class,
    };

    public EntityMP(LevelTypes currentLevel) {
        super(null, Server.Side.SERVER);
        this.currentLevel = currentLevel;
    }

    public static EntityMP getDefaultEntityByType(int entityType) {
        try {
            for (Class<?> c : ENTITIES) {
                EntityMP e = (EntityMP) c.getConstructor().newInstance();
                if (e.getEntityType().ordinal() == entityType) {
                    return e;
                }
            }
        } catch (Exception e) {
            Logger.exception("Error getting entity", e);
        }

        return null;
    }

    @Side(Server.Side.SERVER)
    public void sendInfoPacket(Server server) {
        try {
            new PacketEntityUpdate(server, this.getConnection()).sendEntityNbtToEverybody(this, false);
        } catch (Exception e) {
            Logger.exception("Unable to update entity (" + this.getUUID() + ") info", e);
        }
    }

    public void setLevel(LevelTypes currentLevel) {
        this.currentLevel = currentLevel;
    }

    public LevelTypes getLevel() {
        return currentLevel;
    }

    public Socket getConnection() {
        return connection;
    }

    public void setConnection(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void render() {
        super.render();
    }
}
