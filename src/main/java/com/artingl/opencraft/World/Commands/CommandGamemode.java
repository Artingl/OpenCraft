package com.artingl.opencraft.World.Commands;

import com.artingl.opencraft.World.Entity.EntityPlayer;
import com.artingl.opencraft.World.Entity.Gamemode.Creative;
import com.artingl.opencraft.World.Entity.Gamemode.Survival;
import com.artingl.opencraft.World.Level.ClientLevel;

public class CommandGamemode extends Command {

    public CommandGamemode() {
        super("gamemode");
    }

    public void execute(String[] args, EntityPlayer as, ClientLevel level) {
        if (args.length < 1) {
            as.tellInChat("/gamemode requires at least 1 argument");
            return;
        }

        if (args[0].equals("creative") || args[0].equals("1")) {
            as.setGamemode(Creative.instance);
            as.tellInChat("Your gamemode is changed to " + args[0]);
        }
        else if (args[0].equals("survival") || args[0].equals("0")) {
            as.setGamemode(Survival.instance);
            as.tellInChat("Your gamemode is changed to " + args[0]);
        }
        else {
            as.tellInChat("Invalid gamemode " + args[0]);
        }
    }
}
