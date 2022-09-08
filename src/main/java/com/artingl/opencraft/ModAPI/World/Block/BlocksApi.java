package com.artingl.opencraft.ModAPI.World.Block;

import com.artingl.opencraft.ModAPI.API;
import com.artingl.opencraft.ModAPI.ModEntry;
import com.artingl.opencraft.World.Block.Block;
import com.artingl.opencraft.World.Block.BlockRegistry;

public class BlocksApi extends API {

    public BlocksApi(ModEntry mod) {
        super(mod);
    }

    public Block createBlock(BlockProperties properties) {
        return BlockRegistry.registerBlock(new ModBlock(properties));
    }

}
