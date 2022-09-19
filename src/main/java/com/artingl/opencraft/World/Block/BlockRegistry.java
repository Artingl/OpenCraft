package com.artingl.opencraft.World.Block;

import com.artingl.opencraft.Logger.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class BlockRegistry {
    public static class Blocks {
        public static BlockAir air = new BlockAir();
        public static BlockStone stone = new BlockStone();
        public static BlockDirt dirt = new BlockDirt();
        public static BlockGrassBlock grass_block = new BlockGrassBlock();
        public static BlockBedrock bedrock = new BlockBedrock();
        public static BlockWater water = new BlockWater();
        public static BlockLava lava = new BlockLava();
        public static BlockSand sand = new BlockSand();
        public static BlockGravel gravel = new BlockGravel();
        public static BlockLogOak log_oak = new BlockLogOak();
        public static BlockLeavesOak leaves_oak = new BlockLeavesOak();
        public static BlockGlass glass = new BlockGlass();
        public static BlockSandStone sandStone = new BlockSandStone();
        public static BlockHellrock hellrock = new BlockHellrock();
        public static BlockGrass grass = new BlockGrass();
        public static BlockRose rose = new BlockRose();
        public static BlockSnow snow = new BlockSnow();
    }

    public static ArrayList<Block> blocksRegistered = new ArrayList<>();
    public static ArrayList<Block> illegalBlocks = new ArrayList<>();

    public static ArrayList<Class<?>> blocks = new ArrayList<>();
    public static HashMap<Short, Block> blocksHashes = new HashMap<>();

    public static void init() {
        blocksRegistered.add(Blocks.air);
        blocksRegistered.add(Blocks.stone);
        blocksRegistered.add(Blocks.dirt);
        blocksRegistered.add(Blocks.grass_block);
        blocksRegistered.add(Blocks.bedrock);
        blocksRegistered.add(Blocks.water);
        blocksRegistered.add(Blocks.lava);
        blocksRegistered.add(Blocks.sand);
        blocksRegistered.add(Blocks.gravel);
        blocksRegistered.add(Blocks.log_oak);
        blocksRegistered.add(Blocks.leaves_oak);
        blocksRegistered.add(Blocks.glass);
        blocksRegistered.add(Blocks.sandStone);
        blocksRegistered.add(Blocks.hellrock);
        blocksRegistered.add(Blocks.grass);
        blocksRegistered.add(Blocks.rose);
        blocksRegistered.add(Blocks.snow);

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
                BlockRose.class,
                BlockSnow.class,
        };

        blocks.addAll(Arrays.asList(vanilla_blocks));

        try {
            for (Class<?> blockClass : BlockRegistry.blocks) {
                Block block = (Block) blockClass.getConstructor().newInstance();
                blocksHashes.put((short) block.getId().hashCode(), block);
            }
        } catch (Exception e) {
            Logger.exception("Error while getting block id", e);
        }

        addIllegalBlock(Blocks.air);
    }

    public static void updateTextures() {
        Logger.debug("Updating blocks texture");

        for (Block block: blocksRegistered) {
            block.createTexture();
        }
    }

    public static Block registerBlock(Block block) {
        blocks.add(block.getClass());
        blocksRegistered.add(block);
        blocksHashes.put((short) block.getId().hashCode(), block);
        return block;
    }

    public static Block addIllegalBlock(Block block) {
        illegalBlocks.add(block);
        return block;
    }

    public static boolean isBlockIllegal(Block block) {
        return illegalBlocks.contains(block);
    }

}
