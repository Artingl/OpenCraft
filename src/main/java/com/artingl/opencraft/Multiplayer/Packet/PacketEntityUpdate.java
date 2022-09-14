package com.artingl.opencraft.Multiplayer.Packet;

import com.artingl.opencraft.Logger.Logger;
import com.artingl.opencraft.Math.MathUtils;
import com.artingl.opencraft.Math.Vector2f;
import com.artingl.opencraft.Math.Vector2i;
import com.artingl.opencraft.Math.Vector3f;
import com.artingl.opencraft.Multiplayer.Client;
import com.artingl.opencraft.Multiplayer.Server;
import com.artingl.opencraft.Multiplayer.Side;
import com.artingl.opencraft.Multiplayer.SocketVectorArraylist;
import com.artingl.opencraft.Multiplayer.World.Commands.CommandsRegistry;
import com.artingl.opencraft.Multiplayer.World.Entity.EntityMP;
import com.artingl.opencraft.Multiplayer.World.Entity.EntityPlayerMP;
import com.artingl.opencraft.Multiplayer.World.Gamemode.Gamemode;
import com.artingl.opencraft.Multiplayer.World.Gamemode.Survival;
import com.artingl.opencraft.Opencraft;
import com.artingl.opencraft.World.Entity.Entity;
import com.artingl.opencraft.World.Entity.EntityPlayer;
import com.artingl.opencraft.World.EntityData.UUID;
import com.artingl.opencraft.World.NBT.EntityNBT;
import com.artingl.opencraft.World.NBT.EntityPlayerNBT;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class PacketEntityUpdate extends Packet {

    public enum Types {
        ENTITY_NBT,
        ENTITY_PLAYER_NBT,
        ENTITY_PLAYER_CHAT_MESSAGE,
        ENTITY_POSITION,
        ENTITY_INVENTORY,
        SUMMON_ENTITY,
        SUMMON_ENTITY_PLAYER,
        UPDATE_GAMEMODE,
        ENTITY_NBT_MINOR
    }

    public PacketEntityUpdate(){}
    public PacketEntityUpdate(Object clientOrServer, Socket connection) {
        super(clientOrServer, connection);
    }

    @Override
    public void executeClientSide() throws Exception {
        int requestType = inputStream.readInt();
        int entityType = inputStream.readInt();
        UUID uuid = new UUID(inputStream.readUTF());

        Client client = Opencraft.getClientConnection();

        if (requestType == Types.ENTITY_NBT.ordinal()
                || requestType == Types.ENTITY_PLAYER_NBT.ordinal()) {

            if (Opencraft.getPlayerEntity().getUUID().equals(uuid)) {
                EntityPlayer player = Opencraft.getPlayerEntity();
                player.getNbt().readFromStream(inputStream);
                player.setPosition(player.getPosition());

                if (requestType == Types.ENTITY_PLAYER_NBT.ordinal()) {
                    player.getPlayerNbt().readFromStream(inputStream);
                }
            } else {
                EntityMP playerMP = client.getEntitySave(uuid, entityType);

                if (playerMP == null) {
                    new EntityNBT(UUID.empty).readFromStream(inputStream);

                    if (requestType == Types.ENTITY_PLAYER_NBT.ordinal()) {
                        new EntityPlayerNBT().readFromStream(inputStream);
                    }
                } else {
                    playerMP.getNbt().readFromStream(inputStream);
                    playerMP.setPosition(playerMP.getPosition());

                    if (requestType == Types.ENTITY_PLAYER_NBT.ordinal()) {
                        ((EntityPlayerMP) (playerMP)).getPlayerNbt().readFromStream(inputStream);
                    }
                }
            }
        } else if (requestType == Types.SUMMON_ENTITY.ordinal() ||
                requestType == Types.SUMMON_ENTITY_PLAYER.ordinal()) {
            EntityNBT entityNBT = new EntityNBT(UUID.empty);
            entityNBT.readFromStream(inputStream);

            EntityPlayerNBT entityPlayerNBT = new EntityPlayerNBT();
            entityPlayerNBT.readFromStream(inputStream);

            if (Opencraft.getPlayerEntity().getUUID().equals(uuid)) {
                EntityPlayer player = Opencraft.getPlayerEntity();
                player.setNbt(entityNBT);

                if (requestType == Types.SUMMON_ENTITY_PLAYER.ordinal()) {
                    player.setPlayerNbt(entityPlayerNBT);
                }
            }
            else {
                EntityMP entity = client.getEntitySave(uuid, entityType);
                entity.setConnection(connection);
                entity.setNbt(entityNBT);

                if (requestType == Types.SUMMON_ENTITY_PLAYER.ordinal()) {
                    ((EntityPlayerMP) (entity)).setPlayerNbt(entityPlayerNBT);
                }

                client.getEntities().add(entity);
            }
        } else if (requestType == Types.ENTITY_POSITION.ordinal()) {
            Vector3f entityPos = Vector3f.readFromStream(inputStream);
            Vector3f entityPrevPos = Vector3f.readFromStream(inputStream);
            Vector2f entityRot = Vector2f.readFromStream(inputStream);
            Vector2f entityPrevRot = Vector2f.readFromStream(inputStream);
            Vector3f velocity = Vector3f.readFromStream(inputStream);

            EntityMP entityMP = client.getEntity(uuid, entityType);

            if (entityMP != null) {
                entityMP.setPosition(entityPos);
                entityMP.setPrevPosition(entityPrevPos);
                entityMP.setRotation(entityRot);
                entityMP.setPrevRotation(entityPrevRot);
                entityMP.setVelocity(velocity);
            }
        }
        else if (requestType == Types.ENTITY_NBT_MINOR.ordinal()) {
            boolean isFlying = inputStream.readBoolean();
            float acceleration = inputStream.readFloat();
            float height = inputStream.readFloat();

            if (Opencraft.getPlayerEntity().getUUID().equals(uuid)) {
                EntityPlayer player = Opencraft.getPlayerEntity();
                player.getNbt().setFlyingState(isFlying);
                player.getNbt().setAcceleration(acceleration);
                player.getNbt().setHeightOffset(height);
            } else {
                EntityMP entityMP = client.getEntity(uuid, entityType);

                if (entityMP != null) {
                    entityMP.getNbt().setFlyingState(isFlying);
                    entityMP.getNbt().setAcceleration(acceleration);
                    entityMP.getNbt().setHeightOffset(height);
                }
            }
        }
        else if (requestType == Types.ENTITY_INVENTORY.ordinal()) {
            if (Opencraft.getPlayerEntity().getUUID().equals(uuid)) {
                EntityPlayer player = Opencraft.getPlayerEntity();
                player.getNbt().readInventoryFromStream(inputStream);
            } else {
                EntityMP entityMP = client.getEntity(uuid, entityType);

                if (entityMP != null) {
                    entityMP.getNbt().readInventoryFromStream(inputStream);
                } else {
                    new EntityNBT(UUID.empty).readInventoryFromStream(inputStream);
                }
            }
        } else if (requestType == Types.ENTITY_PLAYER_CHAT_MESSAGE.ordinal()) {
            Opencraft.getPlayerEntity().tellInChat(inputStream.readUTF());
        } else if (requestType == Types.UPDATE_GAMEMODE.ordinal()) {
            Gamemode gamemode = Gamemode.readFromStream(inputStream);

            if (Opencraft.getPlayerEntity().getUUID().equals(uuid)) {
                Opencraft.getPlayerEntity().setGamemode(gamemode);
            } else {
                EntityMP entityMP = client.getEntitySave(uuid, entityType);

                if (entityMP != null) {
                    if (entityMP instanceof EntityPlayerMP) {
                        ((EntityPlayerMP) entityMP).setGamemode(gamemode);
                    }
                }
            }
        }
    }

    @Override
    public void executeServerSide() throws Exception {
        int requestType = inputStream.readInt();

        int entityType = inputStream.readInt();
        UUID uuid = new UUID(inputStream.readUTF());
        EntityMP entity = getServer().getEntity(uuid, entityType);

        if (requestType == Types.ENTITY_POSITION.ordinal()) {
            Vector3f entityPos = Vector3f.readFromStream(inputStream);
            Vector3f entityPrevPos = Vector3f.readFromStream(inputStream);
            Vector2f entityRot = Vector2f.readFromStream(inputStream);
            Vector2f entityPrevRot = Vector2f.readFromStream(inputStream);
            SocketVectorArraylist<Vector2i> vel = new SocketVectorArraylist<>();
            vel.readFromStream(inputStream);

            if (entity == null)
                return;

            Vector3f pos = entity.getPosition();

            float length = MathUtils.calculateLength(pos.x, pos.y, pos.z, entityPos.x, entityPos.y, entityPos.z);

            if (length > (entity.isDead() ? 2 : 12) && !entity.isFlying()) {
//                Logger.info("Entity " + entity.getUUID() + " (" + entity.getNameTag() + ") moved too quickly (" + length  + " blocks). This could be a teleport");
//                sendEntityNbt(entity, outputStream);
            }
            else {
                entity.setPosition(entityPos);
                entity.setPrevPosition(entityPrevPos);
                entity.setRotation(entityRot);
                entity.setPrevRotation(entityPrevRot);
                entity.setPositionQueue(vel);
            }

            sendEntityPositionToEverybody(entity, true);
        }
        else if (requestType == Types.ENTITY_NBT_MINOR.ordinal()) {
            boolean isFlying = inputStream.readBoolean();
            float acceleration = inputStream.readFloat();
            float height = inputStream.readFloat();

            if (entity == null)
                return;

            entity.setFlyingState(isFlying);
            entity.setAcceleration(acceleration != 1f && acceleration != 0.1f ? 1f : acceleration);
            entity.setHeightOffset(height != 1.72F && height != 1.82F ? 1.82F : height);

            if (entity instanceof EntityPlayerMP) {
                if (((EntityPlayerMP) entity).getGamemode().getId() == Survival.id) {
                    entity.setFlyingState(false);
                    sendMinorEntityNbt(entity, outputStream);
                }
            }

            for (EntityMP entityMP : getServer().getEntities()) {
                if (entityMP instanceof EntityPlayerMP && entityMP.getLevel() == entity.getLevel() && (entityMP.insideRenderDistance(entity))) {
                    DataOutputStream outputStream = new DataOutputStream(entityMP.getConnection().getOutputStream());
                    sendMinorEntityNbt(entity, outputStream);
                }
            }
        }
        else if (requestType == Types.ENTITY_PLAYER_CHAT_MESSAGE.ordinal()) {
            String msg = inputStream.readUTF();

            if (entity == null)
                return;

            if (entity instanceof EntityPlayerMP) {
                if (msg.charAt(0) == '/') {
                    CommandsRegistry.execute(msg, (EntityPlayerMP) entity, getServer());
                } else {
                    getServer().broadcastToEverybody(entity.getNameTag() + ": " + msg);
                }
            }
        }
    }

    @Side(Server.Side.SERVER)
    public void sendEntityPositionToEverybody(EntityMP entity, boolean checkDistance) throws IOException {
        for (EntityMP entityMP : getServer().getEntities()) {
            if (entityMP instanceof EntityPlayerMP && entityMP.getLevel() == entity.getLevel() && (entityMP.insideRenderDistance(entity) || !checkDistance)) {
                DataOutputStream outputStream = new DataOutputStream(entityMP.getConnection().getOutputStream());

                outputStream.writeByte(Packet.MAGIC_0);
                outputStream.writeByte(Packet.MAGIC_1);
                outputStream.writeInt(getIdByPacketName(getName()));
                outputStream.writeInt(Types.ENTITY_POSITION.ordinal());
                outputStream.writeInt(entity.getEntityType().ordinal());
                outputStream.writeUTF(entity.getUUID().getStringUUID());
                entity.getPosition().writeToStream(outputStream);
                entity.getPrevPosition().writeToStream(outputStream);
                entity.getRotation().writeToStream(outputStream);
                entity.getPrevRotation().writeToStream(outputStream);
                entity.getVelocity().writeToStream(outputStream);
                outputStream.flush();
            }
        }
    }

    @Side(Server.Side.SERVER)
    public void sendEntityNbtToEverybody(EntityMP entity, boolean checkDistance) throws IOException {
        for (EntityMP entityMP : getServer().getEntities()) {
            if (entityMP instanceof EntityPlayerMP && entityMP.getLevel() == entity.getLevel() && (entityMP.insideRenderDistance(entity) || !checkDistance)) {
                DataOutputStream outputStream = new DataOutputStream(entityMP.getConnection().getOutputStream());
                sendEntityNbt(entity, outputStream);
            }
        }
    }

    @Side(Server.Side.SERVER)
    public void sendEntityNbt(EntityMP entity, DataOutputStream outputStream) throws IOException {
        outputStream.writeByte(Packet.MAGIC_0);
        outputStream.writeByte(Packet.MAGIC_1);
        outputStream.writeInt(getIdByPacketName(getName()));
        outputStream.writeInt(Types.ENTITY_NBT.ordinal());
        outputStream.writeInt(entity.getEntityType().ordinal());
        outputStream.writeUTF(entity.getUUID().getStringUUID());
        entity.getNbt().writeToStream(outputStream);
        outputStream.flush();
    }

    @Side(Server.Side.SERVER)
    public void sendEntityPlayerNbt(EntityPlayerMP entity, DataOutputStream outputStream) throws IOException {
        outputStream.writeByte(Packet.MAGIC_0);
        outputStream.writeByte(Packet.MAGIC_1);
        outputStream.writeInt(getIdByPacketName(getName()));
        outputStream.writeInt(Types.ENTITY_PLAYER_NBT.ordinal());
        outputStream.writeInt(entity.getEntityType().ordinal());
        outputStream.writeUTF(entity.getUUID().getStringUUID());
        entity.getNbt().writeToStream(outputStream);
        entity.getPlayerNbt().writeToStream(outputStream);
        outputStream.flush();
    }

    @Side(Server.Side.SERVER)
    public void sendEntityInventory(EntityMP entity) throws IOException {
        outputStream.writeByte(Packet.MAGIC_0);
        outputStream.writeByte(Packet.MAGIC_1);
        outputStream.writeInt(getIdByPacketName(getName()));
        outputStream.writeInt(Types.ENTITY_INVENTORY.ordinal());
        outputStream.writeInt(entity.getEntityType().ordinal());
        outputStream.writeUTF(entity.getUUID().getStringUUID());
        entity.getNbt().writeInventoryToStream(outputStream);
        outputStream.flush();
    }

    @Side(Server.Side.SERVER)
    public void summonEntity(EntityMP entity) throws IOException {
        outputStream.writeByte(Packet.MAGIC_0);
        outputStream.writeByte(Packet.MAGIC_1);
        outputStream.writeInt(getIdByPacketName(getName()));
        outputStream.writeInt(Types.SUMMON_ENTITY.ordinal());
        outputStream.writeInt(entity.getEntityType().ordinal());
        outputStream.writeUTF(entity.getNbt().getUUID().getStringUUID());
        entity.getNbt().writeToStream(outputStream);
        outputStream.flush();
    }

    @Side(Server.Side.SERVER)
    public void summonEntityPlayer(EntityPlayerMP entity) throws IOException {
        outputStream.writeByte(Packet.MAGIC_0);
        outputStream.writeByte(Packet.MAGIC_1);
        outputStream.writeInt(getIdByPacketName(getName()));
        outputStream.writeInt(Types.SUMMON_ENTITY_PLAYER.ordinal());
        outputStream.writeInt(entity.getEntityType().ordinal());
        outputStream.writeUTF(entity.getNbt().getUUID().getStringUUID());
        entity.getNbt().writeToStream(outputStream);
        entity.getPlayerNbt().writeToStream(outputStream);
        outputStream.flush();
    }

    @Side(Server.Side.CLIENT)
    public void updateEntityPosition(Entity entity) throws IOException {
        outputStream.writeByte(Packet.MAGIC_0);
        outputStream.writeByte(Packet.MAGIC_1);
        outputStream.writeInt(getIdByPacketName(getName()));
        outputStream.writeInt(Types.ENTITY_POSITION.ordinal());
        outputStream.writeInt(entity.getEntityType().ordinal());
        outputStream.writeUTF(entity.getNbt().getUUID().getStringUUID());
        entity.getPosition().writeToStream(outputStream);
        entity.getPrevPosition().writeToStream(outputStream);
        entity.getRotation().writeToStream(outputStream);
        entity.getPrevRotation().writeToStream(outputStream);
        entity.getPrevRotation().writeToStream(outputStream);
        entity.getPositionQueue().writeToStream(outputStream);
        outputStream.flush();

        entity.clearPositionQueue();
    }

    @Side(Server.Side.SERVER)
    public void updateGamemode(EntityPlayerMP entity) throws IOException {
        outputStream.writeByte(Packet.MAGIC_0);
        outputStream.writeByte(Packet.MAGIC_1);
        outputStream.writeInt(getIdByPacketName(getName()));
        outputStream.writeInt(Types.UPDATE_GAMEMODE.ordinal());
        outputStream.writeInt(entity.getEntityType().ordinal());
        outputStream.writeUTF(entity.getNbt().getUUID().getStringUUID());
        entity.getGamemode().writeToStream(outputStream);
        outputStream.flush();
    }

    @Side(Server.Side.BOTH)
    public void tellInChat(EntityPlayerMP entityPlayerMP, String msg) throws IOException {
        outputStream.writeByte(Packet.MAGIC_0);
        outputStream.writeByte(Packet.MAGIC_1);
        outputStream.writeInt(getIdByPacketName(getName()));
        outputStream.writeInt(Types.ENTITY_PLAYER_CHAT_MESSAGE.ordinal());
        outputStream.writeInt(Entity.Types.PLAYER.ordinal());
        outputStream.writeUTF(entityPlayerMP.getUUID().getStringUUID());
        outputStream.writeUTF(msg);
        outputStream.flush();
    }

    @Side(Server.Side.CLIENT)
    public void sendMinorEntityNbt(Entity entity) throws IOException {
        sendMinorEntityNbt(entity, outputStream);
    }

    @Side(Server.Side.CLIENT)
    public void sendMinorEntityNbt(Entity entity, DataOutputStream outputStream) throws IOException {
        outputStream.writeByte(Packet.MAGIC_0);
        outputStream.writeByte(Packet.MAGIC_1);
        outputStream.writeInt(getIdByPacketName(getName()));
        outputStream.writeInt(Types.ENTITY_NBT_MINOR.ordinal());
        outputStream.writeInt(entity.getEntityType().ordinal());
        outputStream.writeUTF(entity.getUUID().getStringUUID());

        outputStream.writeBoolean(entity.isFlying());
        outputStream.writeFloat(entity.getAcceleration());
        outputStream.writeFloat(entity.getHeightOffset());

        outputStream.flush();
    }

    @Override
    public String getName() {
        return "PacketEntityUpdate";
    }
}
