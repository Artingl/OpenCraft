package com.artingl.opencraft.Multiplayer.World.Commands;

import com.artingl.opencraft.Multiplayer.Server;
import com.artingl.opencraft.Multiplayer.World.Entity.EntityPlayerMP;

public class CommandsRegistry {

    public static class Commands {
        public static CommandGamemode commandGamemode = new CommandGamemode();
        public static CommandTP commandTP = new CommandTP();
        public static CommandKill commandKill = new CommandKill();
    }

    public static Command[] commands = new Command[]{
            Commands.commandGamemode,
            Commands.commandTP,
            Commands.commandKill
    };

    public static void execute(String commandString, EntityPlayerMP as, Server server) {
        boolean found = false;
        String[] args = new String[commandString.split(" ").length-1];
        for (int i = 1; i < args.length+1; i++)
            args[i-1] = commandString.split(" ")[i];

        for (Command command: commands) {
            if (command.commandName.equals(commandString.substring(1).split(" ")[0])) {
                command.execute(args, as, server);
                found = true;
                break;
            }
        }

        if (!found) {
            as.tellServerLogInChat("Command " + commandString.substring(1).split(" ")[0] + " not found!");
        }
    }

}
