package com.artingl.opencraft.World.Block;

import com.artingl.opencraft.Logger.Logger;

import java.util.ArrayList;
import java.util.Arrays;

public class BlockRegistry {
    public static class Blocks {
        public static Block air = new BlockAir();
        public static Block stone = new BlockStone();
        public static Block dirt = new BlockDirt();
        public static Block grass_block = new BlockGrassBlock();
        public static Block bedrock = new BlockBedrock();
        public static Block water = new BlockWater();
        public static Block lava = new BlockLava();
        public static Block sand = new BlockSand();
        public static Block gravel = new BlockGravel();
        public static Block log_oak = new BlockLogOak();
        public static Block leaves_oak = new BlockLeavesOak();
        public static Block glass = new BlockGlass();
        public static Block sandStone = new BlockSandStone();
        public static Block hellrock = new BlockHellrock();
        public static Block grass = new BlockGrass();
        public static Block rose = new BlockRose();
    }

    public static ArrayList<Class<?>> blocks = new ArrayList<>();

    public static void registerAllBlocks() {
        Class<?>[] vanilla_blocks = {
                BlockAir.class,
                BlockStone.class,
                BlockDirt.class,
                BlockGrassBlock.class,
                BlockBedrock.class,
                BlockWater.class,
                BlockLava.class,
                BlockSand.class,
                BlockGravel.class,
                BlockLogOak.class,
                BlockLeavesOak.class,
                BlockGlass.class,
                BlockSandStone.class,
                BlockHellrock.class,
                BlockGrass.class,
                BlockRose.class
        };

        blocks.addAll(Arrays.asList(vanilla_blocks));
    }

    public static Block registerBlock(Block block) {
        blocks.add(block.getClass());
        return block;
    }

    public static Block getBlockById(int id) {
        try {
            return (Block) BlockRegistry.blocks.get(id).getConstructor().newInstance();
        } catch (Exception e) {
            Logger.exception("Error while getting block by its id", e);
        }

        return Blocks.air;
    }

    public static Block getBlockById(String id) {
        try {
            for (Class<?> blockClass : BlockRegistry.blocks) {
                Block block = (Block) blockClass.getConstructor().newInstance();

                if (block.getId().equals(id))
                    return block;
            }
        } catch (Exception e) {
            Logger.exception("Error while getting block id", e);
        }

        return BlockRegistry.Blocks.air;
    }

}
