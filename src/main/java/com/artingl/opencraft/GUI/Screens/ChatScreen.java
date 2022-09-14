package com.artingl.opencraft.GUI.Screens;

import com.artingl.opencraft.Control.Game.Input;
import com.artingl.opencraft.GUI.Elements.ChatEditArea;
import com.artingl.opencraft.GUI.Elements.Element;
import com.artingl.opencraft.Logger.Logger;
import com.artingl.opencraft.Multiplayer.Packet.PacketEntityUpdate;
import com.artingl.opencraft.Multiplayer.World.Entity.EntityPlayerMP;
import com.artingl.opencraft.Opencraft;
import com.artingl.opencraft.Resources.Lang.Lang;
import com.artingl.opencraft.World.Entity.EntityPlayer;
import com.artingl.opencraft.World.Level.ClientLevel;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.*;

public class ChatScreen extends Screen
{
    private int commandEditArea;
    private EntityPlayer player;
    private ClientLevel level;
    private String message;
    private final String startText;

    public ChatScreen(EntityPlayer player, ClientLevel level, String startText) {
        super(Opencraft.getWidth(), Opencraft.getHeight(), "chat");
        this.showTittle = false;
        this.player = player;
        this.level = level;
        this.startText = startText;
        this.message = "";
    }

    public void init() {
        super.init();

        message = "";
        commandEditArea = this.addElement(new ChatEditArea(this,0, 0, Lang.getTranslatedString("opencraft:gui.text.enter_in_chat"), () ->
                message = ((ChatEditArea)getElements().get(commandEditArea)).getText()));

        ((ChatEditArea)getElements().get(commandEditArea)).setText(startText);
    }

    @Override
    public void updateElement(Element element, int screenWidth, int screenHeight, int scale) {
        super.updateElement(element, screenWidth, screenHeight, scale);
        if (element instanceof ChatEditArea edit)
        {
            if (edit.getId() == commandEditArea) {
                edit.setX(2);
                edit.setHeight(14);
                edit.setY(screenHeight - edit.getHeight() - 2);
                edit.setWidth(screenWidth - 4);
            }
        }
    }

    public void render(int screenWidth, int screenHeight, int scale)
    {
        GL11.glEnable(GL_BLEND);
        GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        GL11.glPushMatrix();
        GL11.glTranslatef(0, 0, 50);
        super.render(screenWidth, screenHeight, scale);
        GL11.glPopMatrix();
        GL11.glDisable(GL_BLEND);
    }

    @Override
    protected void keyPressed(Input.KeyInput keyInput) {
        super.keyPressed(keyInput);

        if (keyInput.keyCode == Input.Keys.KEY_ESCAPE) {
            Opencraft.closePlayerScreen();
        }
        else if (keyInput.keyCode == Input.Keys.KEY_ENTER) {
            if (message.length() > 0) {
                if (Opencraft.getClientConnection().isActive()) {
                    try {
                        new PacketEntityUpdate(null, Opencraft.getClientConnection()
                                .getConnection()).tellInChat((EntityPlayerMP) player.convertToMP(), message);
                    } catch (Exception e) {
                        Logger.exception("Unable to tell in chat", e);
                    }
                }

                ((ChatEditArea) getElements().get(commandEditArea)).setText("");
            }

            Opencraft.closePlayerScreen();
        }
    }
}
