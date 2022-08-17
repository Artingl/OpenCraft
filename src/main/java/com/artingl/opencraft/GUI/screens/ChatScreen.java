package com.artingl.opencraft.GUI.screens;

import com.artingl.opencraft.GL.Controls;
import com.artingl.opencraft.GUI.ChatEditArea;
import com.artingl.opencraft.GUI.Screen;
import com.artingl.opencraft.OpenCraft;
import com.artingl.opencraft.Resources.Lang;
import com.artingl.opencraft.World.Commands.Command;
import com.artingl.opencraft.World.Entity.EntityPlayer;
import com.artingl.opencraft.World.Level.Level;

public class ChatScreen extends Screen
{
    private int commandEditArea;
    private EntityPlayer player;
    private Level level;
    private String command;
    private final String startText;

    public ChatScreen(EntityPlayer player, Level level, String startText) {
        super(OpenCraft.getWidth(), OpenCraft.getHeight(), "chat");
        this.player = player;
        this.level = level;
        this.startText = startText;
        this.command = "";
    }

    public void init() {
        super.init();

        command = "";
        commandEditArea = this.addElement(new ChatEditArea(this, 0, 0, 0, Lang.getLanguageString("opencraft:gui.text.enter_in_chat"), () ->
                command = ((ChatEditArea)getElements().get(commandEditArea)).getText()));

        ((ChatEditArea)getElements().get(commandEditArea)).setText(startText);
    }

    public void render(int screenWidth, int screenHeight, int scale)
    {
        getElements().forEach((id, element) -> {
            if (element instanceof ChatEditArea)
            {
                ChatEditArea edit = (ChatEditArea)element;
                if (edit.getId() == commandEditArea) {
                    edit.setX(0);
                    edit.setY(screenHeight - edit.getHeight());
                    edit.setWidth(screenWidth);
                }
            }
        });

        super.render(screenWidth, screenHeight, scale);
    }

    @Override
    protected void keyPressed(Controls.KeyInput keyInput) {
        super.keyPressed(keyInput);

        if (keyInput.keyCode == Controls.Keys.KEY_ESCAPE) {
            OpenCraft.closePlayerScreen();
        }
        else if (keyInput.keyCode == Controls.Keys.KEY_ENTER) {
            if (command.length() > 0) {
                if (command.charAt(0) == '/')
                    Command.execute(command, player, level);
                else player.tellInChat(player.getNameTag() + ": " + command);
                ((ChatEditArea) getElements().get(commandEditArea)).setText("");
            }

            OpenCraft.closePlayerScreen();
        }
    }
}
