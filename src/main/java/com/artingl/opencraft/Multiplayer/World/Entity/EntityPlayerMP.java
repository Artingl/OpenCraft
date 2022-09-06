package com.artingl.opencraft.Multiplayer.World.Entity;

import com.artingl.opencraft.GUI.Screens.Screen;
import com.artingl.opencraft.Logger.Logger;
import com.artingl.opencraft.Math.Vector3f;
import com.artingl.opencraft.Multiplayer.Packet.PacketUpdateEntity;
import com.artingl.opencraft.Opencraft;
import com.artingl.opencraft.Rendering.TextureEngine;
import com.artingl.opencraft.World.Entity.Gamemode.Gamemode;
import com.artingl.opencraft.World.Entity.Gamemode.Survival;
import com.artingl.opencraft.World.Entity.Models.PlayerModel;
import com.artingl.opencraft.World.Item.Item;
import org.lwjgl.opengl.GL11;

import java.net.Socket;

public class EntityPlayerMP extends EntityMP {

    private Gamemode gamemode;
    private Socket connection;


    public static PlayerModel model = new PlayerModel();
    public float timeOffs;
    private int texture;

    public EntityPlayerMP() {
        super(null);
        this.setHeightOffset(1.82F);
        this.gamemode = Survival.instance;
        this.entityType = Types.PLAYER;
        this.timeOffs = (float)Math.random() * 1239813.0F;
        this.texture = -1;
    }

    public void destroy() {
        super.destroy();
    }

    public void tellInChat(String msg) {
        try {
            new PacketUpdateEntity(null, this.connection).tellInChat(this, msg);
        } catch (Exception e) {
            Logger.exception("Error telling to " + this.getUUID(), e);
        }
    }

    public Gamemode getGamemode() {
        return this.gamemode;
    }

    public void setGamemode(Gamemode gm) {
        this.gamemode = gm;
    }

    public boolean hasInventory() {
        return true;
    }

    public boolean pick(Item item) {
        // todo
        return false;
    }

    public void closeScreen() {
    }

    public void setScreen(Screen playerScreen) {
    }

    public Screen getScreen() {
        return null;
    }

    public void setConnection(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void render() {
        super.render();

        if (texture == -1) {
            this.texture = TextureEngine.load("opencraft:entity/steve.png");
        }

        float rot = 0;

        Vector3f position = getPosition();
        Vector3f prevPosition = getPrevPosition();

        float a = Opencraft.getTimer().a;
        GL11.glEnable(3553);
        GL11.glBindTexture(3553, texture);
        GL11.glPushMatrix();
        float yy = (float)(-Math.abs(Math.sin(0.6662D)) * 5.0D - 23.0D);
        GL11.glTranslatef(prevPosition.x + (position.x - prevPosition.x) * a, prevPosition.y + (position.y - prevPosition.y) * a, prevPosition.z + (position.z - prevPosition.z) * a);
        GL11.glScalef(1.0F, -1.0F, 1.0F);
        GL11.glScalef(0.058333334F, 0.058333334F, 0.058333334F);
        GL11.glTranslatef(0.0F, yy, 0.0F);
        float c = 57.29578F;
        GL11.glRotatef(rot * c + 180.0F, 0.0F, 1.0F, 0.0F);
        model.render();
        GL11.glPopMatrix();
        GL11.glDisable(3553);
    }
}
