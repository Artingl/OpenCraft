package com.artingl.opencraft.Multiplayer.Packet;

import com.artingl.opencraft.Math.Vector2i;
import com.artingl.opencraft.Math.Vector3i;
import com.artingl.opencraft.Multiplayer.Server;
import com.artingl.opencraft.Multiplayer.Side;
import com.artingl.opencraft.Multiplayer.World.Entity.EntityMP;
import com.artingl.opencraft.Multiplayer.World.Entity.EntityPlayerMP;
import com.artingl.opencraft.Multiplayer.World.Gamemode.Spectator;
import com.artingl.opencraft.Opencraft;
import com.artingl.opencraft.World.Block.Block;
import com.artingl.opencraft.World.Block.BlockRegistry;
import com.artingl.opencraft.World.Chunk.Region;
import com.artingl.opencraft.World.Entity.Entity;
import com.artingl.opencraft.World.EntityData.UUID;
import com.artingl.opencraft.World.Item.ItemSlot;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class PacketWorldUpdate extends Packet {

    public enum Types {
        SET_BLOCK, BREAK_BLOCK
    }

    public PacketWorldUpdate(){}
    public PacketWorldUpdate(Object clientOrServer, Socket connection) {
        super(clientOrServer, connection);
    }


    @Side(Server.Side.CLIENT)
    @Override
    public void executeClientSide() throws Exception {
        int requestType = inputStream.readInt();

        if (requestType == Types.SET_BLOCK.ordinal()) {
            Block block = Block.unpackFromShort(inputStream.readShort());
            Vector3i blockPosition = Vector3i.readFromStream(inputStream);

            Opencraft.getLevel().setBlock(block, blockPosition);
        }
    }

    @Override
    public void executeServerSide() throws Exception {
        int requestType = inputStream.readInt();

        int entityType = inputStream.readInt();
        UUID uuid = new UUID(inputStream.readUTF());
        EntityMP entity = getServer().getEntity(uuid, entityType);

        if (requestType == Types.SET_BLOCK.ordinal()) {
            int selectedSlot = inputStream.readInt();
            Block block = Block.unpackFromShort(inputStream.readShort());
            Vector3i blockPosition = Vector3i.readFromStream(inputStream);

            if (entity == null)
                return;

            boolean canBreak = true;

            if (entity instanceof EntityPlayerMP) {
                canBreak = !(((EntityPlayerMP) entity).getGamemode().equals(Spectator.instance));
            }

            ItemSlot itemSlot = entity.getInventoryItem(selectedSlot);

            if (itemSlot == null || block.equals(BlockRegistry.Blocks.air)) {
                for (EntityMP entityMP : getServer().getEntities()) {
                    if (entityMP instanceof EntityPlayerMP && entityMP.getLevelMP() == entity.getLevelMP() && entityMP.insideRenderDistance(entity)) {
                        sendBlock(blockPosition, BlockRegistry.Blocks.air, new DataOutputStream(entityMP.getConnection().getOutputStream()));
                    }
                }
            } else {
                for (EntityMP entityMP : getServer().getEntities()) {
                    if (entityMP instanceof EntityPlayerMP && entityMP.getLevelMP() == entity.getLevelMP() && entityMP.insideRenderDistance(entity)) {
                        sendBlock(blockPosition, block, new DataOutputStream(entityMP.getConnection().getOutputStream()));
                    }
                }

                entity.decreaseSlot(selectedSlot);
            }

            new PacketEntityUpdate(null, connection).sendEntityInventory(entity);
        }
        else if (requestType == Types.BREAK_BLOCK.ordinal()) {
            int selectedSlot = inputStream.readInt();
            Vector3i blockPosition = Vector3i.readFromStream(inputStream);

            if (entity == null)
                return;

            for (EntityMP entityMP : getServer().getEntities()) {
                if (entityMP instanceof EntityPlayerMP && entityMP.getLevelMP() == entity.getLevelMP() && entityMP.insideRenderDistance(entity)) {
                    sendBlock(blockPosition, BlockRegistry.Blocks.air, new DataOutputStream(entityMP.getConnection().getOutputStream()));
                }
            }

//            ItemSlot itemSlot = entity.getInventoryItem(selectedSlot);
//            Region tempRegion = new Region(new Vector2i(blockPosition.x >> 4, blockPosition.z >> 4));
//
//            Opencraft.getLevel().generation.getLevelGeneration().generateRegion(tempRegion);
//
//            Block block = tempRegion.getBlock(
//                blockPosition.x - ((blockPosition.x >> 4) * 16),
//                blockPosition.y,
//                blockPosition.z - ((blockPosition.z >> 4) * 16)
//            );
//
//            System.out.println(block);
//
//            tempRegion.destroy();
//            System.gc();

        }
    }

    @Side(Server.Side.SERVER)
    public void sendBlock(Vector3i blockPosition, Block block, DataOutputStream outputStream) throws IOException {
        outputStream.writeByte(Packet.MAGIC_0);
        outputStream.writeByte(Packet.MAGIC_1);
        outputStream.writeInt(getIdByPacketName(getName()));
        outputStream.writeInt(Types.SET_BLOCK.ordinal());
        outputStream.writeShort(block.packToShort());
        blockPosition.writeToStream(outputStream);
        outputStream.flush();
    }

    @Side(Server.Side.CLIENT)
    public void setBlock(Block block, Vector3i position, Entity entity) throws IOException {
        outputStream.writeByte(Packet.MAGIC_0);
        outputStream.writeByte(Packet.MAGIC_1);
        outputStream.writeInt(getIdByPacketName(getName()));
        outputStream.writeInt(Types.SET_BLOCK.ordinal());
        outputStream.writeInt(entity.getEntityType().ordinal());
        outputStream.writeUTF(entity.getNbt().getUUID().getStringUUID());
        outputStream.writeInt(entity.getInventorySelectedSlot());
        outputStream.writeShort(block.packToShort());
        position.writeToStream(outputStream);
        outputStream.flush();
    }

    @Side(Server.Side.CLIENT)
    public void breakBlock(Vector3i position, Entity entity) throws IOException {
        outputStream.writeByte(Packet.MAGIC_0);
        outputStream.writeByte(Packet.MAGIC_1);
        outputStream.writeInt(getIdByPacketName(getName()));
        outputStream.writeInt(Types.BREAK_BLOCK.ordinal());
        outputStream.writeInt(entity.getEntityType().ordinal());
        outputStream.writeUTF(entity.getNbt().getUUID().getStringUUID());
        outputStream.writeInt(entity.getInventorySelectedSlot());
        position.writeToStream(outputStream);
        outputStream.flush();
    }

    @Override
    public String getName() {
        return "PacketWorldUpdate";
    }

}
