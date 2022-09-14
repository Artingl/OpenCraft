package com.artingl.opencraft.Multiplayer.World.Commands;

import com.artingl.opencraft.Multiplayer.Server;
import com.artingl.opencraft.Multiplayer.World.Entity.EntityPlayerMP;

public class CommandKill extends Command {

    public CommandKill() {
        super("kill");
    }

    @Override
    public void execute(String[] args, EntityPlayerMP as, Server server) {
        if (args.length == 0) {
            as.die();
        }
//        else {
//
//        }

        as.sendInfoPacket(server);
    }
}
