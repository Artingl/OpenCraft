package com.artingl.opencraft.Multiplayer.World.Commands;

import com.artingl.opencraft.Multiplayer.Server;
import com.artingl.opencraft.Multiplayer.World.Entity.EntityPlayerMP;

public class Command {
    public final String commandName;

    public Command(String commandName) {
        this.commandName = commandName;
    }

    public void execute(String[] args, EntityPlayerMP as, Server server) {
    }

}
