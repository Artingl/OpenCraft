package com.artingl.opencraft.World.Commands;

import com.artingl.opencraft.World.Entity.EntityPlayer;
import com.artingl.opencraft.World.Entity.Gamemode.Creative;
import com.artingl.opencraft.World.Entity.Gamemode.Survival;
import com.artingl.opencraft.World.Level.Level;

public class CommandGamemode extends Command {

    public CommandGamemode() {
        super("gamemode");
    }

    public void execute(String[] args, EntityPlayer as, Level level) {
        if (args.length < 1) {
            as.tellInChat("/gamemode requires at least 1 argument");
            return;
        }

        if (args[0].equals("creative")) {
            as.setGamemode(Creative.instance);
            as.tellInChat("Your gamemode is changed to " + args[0]);
        }
        else if (args[0].equals("survival")) {
            as.setGamemode(Survival.instance);
            as.tellInChat("Your gamemode is changed to " + args[0]);
        }
        else {
            as.tellInChat("Invalid gamemode " + args[0]);
        }
    }
}
