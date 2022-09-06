package com.artingl.opencraft.World.Commands;

import com.artingl.opencraft.Utils.Random;
import com.artingl.opencraft.World.Block.Block;
import com.artingl.opencraft.World.Block.BlockRegistry;
import com.artingl.opencraft.World.Entity.Entity;
import com.artingl.opencraft.World.Entity.EntityPlayer;
import com.artingl.opencraft.World.Item.ItemBlock;
import com.artingl.opencraft.World.Level.ClientLevel;

import java.util.Map;

public class CommandGive extends Command {

    public CommandGive() {
        super("give");
    }

    public void execute(String[] args, EntityPlayer as, ClientLevel level) {
        if (args.length < 2) {
            as.tellInChat("/give requires at least 2 argument");
            return;
        }

    }
}
