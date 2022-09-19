package com.artingl.opencraft.Multiplayer.World.Commands;

import com.artingl.opencraft.Logger.Logger;
import com.artingl.opencraft.Multiplayer.Packet.PacketEntityUpdate;
import com.artingl.opencraft.Multiplayer.Server;
import com.artingl.opencraft.Multiplayer.World.Entity.EntityPlayerMP;
import com.artingl.opencraft.Multiplayer.World.Gamemode.Creative;
import com.artingl.opencraft.Multiplayer.World.Gamemode.Spectator;
import com.artingl.opencraft.Multiplayer.World.Gamemode.Survival;

public class CommandGamemode extends Command {

    public CommandGamemode() {
        super("gamemode");
    }

    @Override
    public void execute(String[] args, EntityPlayerMP as, Server server) {
        if (args.length < 1) {
            as.tellServerLogInChat("/gamemode requires at least 1 argument");
            return;
        }

        if (args[0].equals("creative") || args[0].equals("1")) {
            as.setGamemode(Creative.instance);
            as.tellServerLogInChat("Your gamemode is changed to " + args[0]);
        }
        else if (args[0].equals("survival") || args[0].equals("0")) {
            as.setGamemode(Survival.instance);
            as.tellServerLogInChat("Your gamemode is changed to " + args[0]);
        }
        else if (args[0].equals("spectator") || args[0].equals("3")) {
            as.setGamemode(Spectator.instance);
            as.tellServerLogInChat("Your gamemode is changed to " + args[0]);
        }
        else {
            as.tellServerLogInChat("Invalid gamemode " + args[0]);
        }

        try {
            new PacketEntityUpdate(null, as.getConnection()).updateGamemode(as);
        } catch (Exception e) {
            Logger.exception("Error updating gamemode for " + as.getUUID(), e);
        }
    }
}
