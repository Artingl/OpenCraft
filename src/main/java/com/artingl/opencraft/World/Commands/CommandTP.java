package com.artingl.opencraft.World.Commands;

import com.artingl.opencraft.World.Entity.EntityPlayer;
import com.artingl.opencraft.World.Entity.Gamemode.Creative;
import com.artingl.opencraft.World.Entity.Gamemode.Survival;
import com.artingl.opencraft.World.Level.Level;

import java.lang.reflect.Array;
import java.util.Arrays;

public class CommandTP extends Command {

    public CommandTP() {
        super("tp");
    }

    public void execute(String[] args, EntityPlayer as, Level level) {
        if (args.length < 2) {
            as.tellInChat("/tp requires at least 3 arguments");
            return;
        }

        as.setPosition(
                Integer.parseInt(args[0]),
                Integer.parseInt(args[1]),
                Integer.parseInt(args[2]));

        as.tellInChat("You were teleported to " + args[0] + " " + args[1] + " " + args[2]);
    }
}
