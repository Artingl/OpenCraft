package com.artingl.opencraft.World.Commands;

import com.artingl.opencraft.World.Entity.EntityPlayer;
import com.artingl.opencraft.World.Level.ClientLevel;

public class CommandDimension extends Command {

    public CommandDimension() {
        super("dimension");
    }

    public void execute(String[] args, EntityPlayer as, ClientLevel level) {
        if (args.length < 1) {
            as.tellInChat("/dimension requires at least 1 argument");
            return;
        }

        if (args[0].equals("world")) {
//            OpenCraft.switchWorld(ClientLevel.LevelType.WORLD);
            as.tellInChat("Current dimension is changed to " + args[0]);
        }
        else if (args[0].equals("hell")) {
//            OpenCraft.switchWorld(ClientLevel.LevelType.HELL);
            as.tellInChat("Current dimension is changed to " + args[0]);
        }
        else {
            as.tellInChat("Invalid dimension " + args[0]);
        }
    }
}
