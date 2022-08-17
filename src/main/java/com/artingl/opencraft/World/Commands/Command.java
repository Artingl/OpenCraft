package com.artingl.opencraft.World.Commands;

import com.artingl.opencraft.World.Entity.EntityPlayer;
import com.artingl.opencraft.World.Level.Level;

public class Command {

    public static CommandGamemode commandGamemode = new CommandGamemode();
    public static CommandDimension commandDimension = new CommandDimension();
    public static CommandTP commandTP = new CommandTP();

    public static Command[] commands = new Command[]{
            commandGamemode, commandDimension, commandTP
    };

    public static void execute(String commandString, EntityPlayer as, Level level) {
        boolean found = false;
        String[] args = new String[commandString.split(" ").length-1];
        for (int i = 1; i < args.length+1; i++)
            args[i-1] = commandString.split(" ")[i];

        for (Command command: commands) {
            if (command.commandName.equals(commandString.substring(1).split(" ")[0])) {
                command.execute(args, as, level);
                found = true;
                break;
            }
        }

        if (!found) {
            as.tellInChat("Command " + commandString.substring(1).split(" ")[0] + " not found!");
        }
    }

    //

    public final String commandName;

    public Command(String commandName) {
        this.commandName = commandName;
    }

    public void execute(String[] args, EntityPlayer as, Level level) {
    }

}
