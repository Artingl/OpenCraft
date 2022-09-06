package com.artingl.opencraft.Multiplayer.Packet;

import com.artingl.opencraft.Math.Vector3f;
import com.artingl.opencraft.Multiplayer.Client;
import com.artingl.opencraft.Multiplayer.Connection;
import com.artingl.opencraft.Multiplayer.Server;
import com.artingl.opencraft.Multiplayer.Side;
import com.artingl.opencraft.Multiplayer.World.Entity.EntityMP;
import com.artingl.opencraft.Multiplayer.World.Entity.EntityPlayerMP;
import com.artingl.opencraft.Opencraft;
import com.artingl.opencraft.World.Entity.Entity;
import com.artingl.opencraft.World.Entity.EntityPlayer;
import com.artingl.opencraft.World.EntityData.EntityNBT;
import com.artingl.opencraft.World.EntityData.Nametag;
import com.artingl.opencraft.World.EntityData.UUID;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class PacketUpdateEntity extends Packet {

    public enum Types {
        ENTITY_NBT, BROADCAST, SUMMON_ENTITY, ENTITY_POSITION
    }

    public PacketUpdateEntity(){}
    public PacketUpdateEntity(Object clientOrServer, Socket connection) {
        super(clientOrServer, connection);
    }

    @Side(Server.Side.CLIENT)
    @Override
    public void executeClientSide() throws Exception {
        int requestType = inputStream.readInt();
        int entityType = inputStream.readInt();
        UUID uuid = new UUID(inputStream.readUTF());

        Client client = Opencraft.getClientConnection();

        if (requestType == Types.ENTITY_NBT.hashCode()) {
            if (Opencraft.getPlayerEntity().getUUID().equals(uuid)) {
                EntityPlayer player = Opencraft.getPlayerEntity();
                player.getNbt().loadFromStream(inputStream);
                player.setPosition(player.getPosition());
            }
        }
        else if (requestType == Types.SUMMON_ENTITY.hashCode()) {
            EntityNBT entityNBT = EntityNBT.load(inputStream);

            if (Opencraft.getPlayerEntity().getUUID().equals(uuid)) {
                EntityPlayer player = Opencraft.getPlayerEntity();
                player.setNbt(entityNBT);
            }
            else {
                EntityPlayerMP playerMP = (EntityPlayerMP) client.getEntity(uuid, entityType);
                playerMP.setConnection(connection);
                playerMP.setNbt(entityNBT);

                client.getEntities().add(playerMP);
            }

            System.out.println("summon");
        }
        else if (requestType == Types.BROADCAST.hashCode()) {
            String message = inputStream.readUTF();
            if (Opencraft.getPlayerEntity().getUUID().equals(uuid)) {
                Opencraft.getPlayerEntity().tellInChat(message);
            }
        }
        else if (requestType == Types.ENTITY_POSITION.hashCode()) {
            float x = inputStream.readFloat();
            float y = inputStream.readFloat();
            float z = inputStream.readFloat();

//            System.out.println(uuid);

            client.getEntity(uuid, entityType).setPosition(x, y, z);
        }
    }

    @Side(Server.Side.SERVER)
    @Override
    public void executeServerSide() throws Exception {
        int requestType = inputStream.readInt();

        int entityType = inputStream.readInt();
        UUID uuid = new UUID(inputStream.readUTF());
        Entity entity = getServer().getEntity(uuid, entityType);

        if (requestType == Types.ENTITY_NBT.hashCode()) {
            for (Connection conn: getServer().getConnections()) {
                DataOutputStream outputStream = new DataOutputStream(conn.getConnection().getOutputStream());

                outputStream.writeInt(Packet.MAGIC);
                outputStream.writeInt(getIdByPacketName(getName()));
                outputStream.writeInt(Types.ENTITY_NBT.hashCode());
                outputStream.writeInt(entityType);
                outputStream.writeUTF(uuid.getStringUUID());
                entity.getNbt().writeToStream(outputStream);
                outputStream.flush();
            }
        }
        else if (requestType == Types.ENTITY_POSITION.hashCode()) {
            float x = inputStream.readFloat();
            float y = inputStream.readFloat();
            float z = inputStream.readFloat();

            entity.setPosition(x, y, z);

            for (Connection conn: getServer().getConnections()) {
                DataOutputStream outputStream = new DataOutputStream(conn.getConnection().getOutputStream());

                outputStream.writeInt(Packet.MAGIC);
                outputStream.writeInt(getIdByPacketName(getName()));
                outputStream.writeInt(Types.ENTITY_POSITION.hashCode());
                outputStream.writeInt(entityType);
                outputStream.writeUTF(uuid.getStringUUID());
                outputStream.writeFloat(x);
                outputStream.writeFloat(y);
                outputStream.writeFloat(z);
                outputStream.flush();
            }
        }
    }

    @Side(Server.Side.CLIENT)
    public void requestEntityPosition(Entity entity) throws IOException {
        outputStream.writeInt(Packet.MAGIC);
        outputStream.writeInt(getIdByPacketName(getName()));
        outputStream.writeInt(Types.ENTITY_NBT.hashCode());
        outputStream.writeInt(entity.getEntityType().hashCode());
        outputStream.writeUTF(entity.getNbt().getUUID().getStringUUID());
        outputStream.flush();
    }

    @Side(Server.Side.CLIENT)
    public void updateEntityPosition(Entity entity) throws IOException {
        outputStream.writeInt(Packet.MAGIC);
        outputStream.writeInt(getIdByPacketName(getName()));
        outputStream.writeInt(Types.ENTITY_POSITION.hashCode());
        outputStream.writeInt(entity.getEntityType().hashCode());
        outputStream.writeUTF(entity.getNbt().getUUID().getStringUUID());
        outputStream.writeFloat(entity.getPosition().x);
        outputStream.writeFloat(entity.getPosition().y);
        outputStream.writeFloat(entity.getPosition().z);
        outputStream.flush();
    }

    @Side(Server.Side.SERVER)
    public void tellInChat(Entity entity, String msg) throws IOException {
        outputStream.writeInt(Packet.MAGIC);
        outputStream.writeInt(getIdByPacketName(getName()));
        outputStream.writeInt(Types.BROADCAST.hashCode());
        outputStream.writeInt(Entity.Types.PLAYER.hashCode());
        outputStream.writeUTF(entity.getUUID().getStringUUID());
        outputStream.writeUTF(msg);
        outputStream.flush();
    }

    @Side(Server.Side.SERVER)
    public void summonEntity(Entity entity) throws IOException {
        outputStream.writeInt(Packet.MAGIC);
        outputStream.writeInt(getIdByPacketName(getName()));
        outputStream.writeInt(Types.SUMMON_ENTITY.hashCode());
        outputStream.writeInt(entity.getEntityType().hashCode());
        outputStream.writeUTF(entity.getUUID().getStringUUID());
        entity.getNbt().writeToStream(outputStream);
        outputStream.flush();
    }

    @Override
    public String getName() {
        return "PacketUpdateEntity";
    }
}
