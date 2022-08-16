package com.artingl.opencraft.ModAPI.World.Block;

import com.artingl.opencraft.ModAPI.ModEntry;
import com.artingl.opencraft.World.Block.Block;

public class BlocksApi {

    private final ModEntry mod;

    protected BlocksApi(ModEntry mod) {
        this.mod = mod;
    }

    public static BlocksApi create(ModEntry entry) {
        return new BlocksApi(entry);
    }

    public Block createBlock(BlockProperties properties) {
        return new ModBlock(properties);
    }

}
