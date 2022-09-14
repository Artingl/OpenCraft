package com.artingl.opencraft.Multiplayer.World.Entity;

import com.artingl.opencraft.GUI.Screens.Screen;
import com.artingl.opencraft.Logger.Logger;
import com.artingl.opencraft.Math.Vector2i;
import com.artingl.opencraft.Math.Vector3f;
import com.artingl.opencraft.Multiplayer.Packet.PacketEntityUpdate;
import com.artingl.opencraft.Multiplayer.Server;
import com.artingl.opencraft.Multiplayer.Side;
import com.artingl.opencraft.Multiplayer.World.Gamemode.Creative;
import com.artingl.opencraft.Opencraft;
import com.artingl.opencraft.Control.Game.TextureEngine;
import com.artingl.opencraft.Multiplayer.World.Gamemode.Gamemode;
import com.artingl.opencraft.World.Entity.Models.PlayerModel;
import com.artingl.opencraft.World.Level.LevelTypes;
import com.artingl.opencraft.World.NBT.EntityPlayerNBT;
import com.artingl.opencraft.World.Entity.EntityPlayerController;
import org.lwjgl.opengl.GL11;

public class EntityPlayerMP extends EntityMP {

    private EntityPlayerNBT playerNbt;

    public static PlayerModel model = new PlayerModel();
    public float timeOffs;
    private int texture;

    public EntityPlayerMP() {
        super(LevelTypes.WORLD);
        this.playerNbt = new EntityPlayerNBT();

        this.setHeightOffset(1.82F);
        this.setGamemode(Creative.instance);

        this.entityType = Types.PLAYER;
        this.timeOffs = (float)Math.random() * 1239813.0F;
        this.texture = -1;
    }

    public void destroy() {
        super.destroy();
    }

    @Side(Server.Side.SERVER)
    public void tellInChat(String msg) {
        try {
            new PacketEntityUpdate(null, this.getConnection()).tellInChat(this, msg);
        } catch (Exception e) {
            Logger.exception("Error telling to " + this.getUUID(), e);
        }
    }

    @Side(Server.Side.SERVER)
    public void tellServerLogInChat(String msg) {
        try {
            new PacketEntityUpdate(null, this.getConnection()).tellInChat(this, "[SERVER]: " + msg);
        } catch (Exception e) {
            Logger.exception("Error telling to " + this.getUUID(), e);
        }
    }

    public EntityPlayerNBT getPlayerNbt() {
        return this.playerNbt;
    }

    public void setPlayerNbt(EntityPlayerNBT nbt) {
        this.playerNbt = nbt;
    }

    public void setGamemode(Gamemode gamemode) {
        this.playerNbt.setGamemode(gamemode);
    }

    public Gamemode getGamemode() {
        return this.playerNbt.getGamemode();
    }

    public boolean hasInventory() {
        return true;
    }

    public void closeScreen() {
    }

    public void setScreen(Screen playerScreen) {
    }

    public Screen getScreen() {
        return null;
    }

    @Override
    public void tick() {
        super.tick();

        if (getPositionQueue().isEmpty())
            EntityPlayerController.updateEntityPositionByVelocity(this, new Vector2i(0, 0));

        for (Vector2i vel: getPositionQueue()) {
            EntityPlayerController.updateEntityPositionByVelocity(this, vel);
        }
    }

    @Override
    public void render() {
        super.render();

        if (texture == -1) {
            this.texture = TextureEngine.load("opencraft:entity/steve.png");
        }

        float rot = getRotation().y;
        float a = Opencraft.getTimer().a;

        Vector3f position = getPosition();
        Vector3f prevPosition = getPrevPosition();

        float x = prevPosition.x + (position.x - prevPosition.x) * Opencraft.getTimer().a;
        float y = prevPosition.y + (position.y - prevPosition.y) * Opencraft.getTimer().a - 2;
        float z = prevPosition.z + (position.z - prevPosition.z) * Opencraft.getTimer().a;

        GL11.glEnable(3553);
        GL11.glBindTexture(3553, texture);
        GL11.glPushMatrix();
        float yy = (float)(-Math.abs(Math.sin(0.6662D)) * 5.0D - 23.0D);
        GL11.glTranslatef(x, y, z);
        GL11.glScalef(1.0F, -1.0F, 1.0F);
        GL11.glScalef(0.058333334F, 0.058333334F, 0.058333334F);
        GL11.glTranslatef(0.0F, yy, 0.0F);
        GL11.glRotatef(360 - rot, 0.0F, 1.0F, 0.0F);
        model.render();
        GL11.glPopMatrix();
        GL11.glDisable(3553);
    }
}
