package com.artingl.opencraft.Multiplayer.World.Commands;

import com.artingl.opencraft.Multiplayer.Server;
import com.artingl.opencraft.Multiplayer.World.Entity.EntityPlayerMP;

public class CommandTP extends Command {

    public CommandTP() {
        super("tp");
    }

    @Override
    public void execute(String[] args, EntityPlayerMP as, Server server) {
        if (args.length < 2) {
            as.tellServerLogInChat("/tp requires at least 3 arguments");
            return;
        }

        as.setPosition(
                Integer.parseInt(args[0]),
                Integer.parseInt(args[1]),
                Integer.parseInt(args[2]));

        as.tellServerLogInChat("You were teleported to " + args[0] + " " + args[1] + " " + args[2]);
        as.sendInfoPacket(server);
    }
}
