package com.artingl.opencraft.ModAPI.World.Block;

import com.artingl.opencraft.ModAPI.ModEntry;
import com.artingl.opencraft.World.Block.Block;
import com.artingl.opencraft.World.Block.BlockRegistry;

public class BlocksApi {

    private final ModEntry mod;

    protected BlocksApi(ModEntry mod) {
        this.mod = mod;
    }

    public static BlocksApi create(ModEntry entry) {
        return new BlocksApi(entry);
    }

    public Block createBlock(BlockProperties properties) {
        return BlockRegistry.registerBlock(new ModBlock(properties));
    }

}
