package OpenCraft.World.Level;

import OpenCraft.OpenCraft;
import OpenCraft.World.Block.Block;
import OpenCraft.World.Chunk.Region;
import OpenCraft.math.Vector2i;
import OpenCraft.utils.StackTrace;

import java.io.*;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class LevelSaver {

    class Regions {
        Region region;
        Level level;

        public Regions(Region region, Level level) {
            this.region = region;
            this.level = level;
        }
    }

    public static final int MAGIC = 0xABCFDA;

    private DataOutputStream dos;
    private DataInputStream dis;
    private String levelName;

    private ArrayList<Regions> updatedRegions;

    public LevelSaver(String levelName) {
        this.levelName = levelName;
        this.updatedRegions = new ArrayList<>();
    }

    public void create() {
        try {
            new File("saves" + File.separator + levelName).mkdir();
            this.dos = new DataOutputStream(
                    new GZIPOutputStream(
                            new FileOutputStream("saves" + File.separator + levelName + File.separator + "level.data")));
            new File("saves" + File.separator + levelName + File.separator + "world_region").mkdir();
            new File("saves" + File.separator + levelName + File.separator + "hell_region").mkdir();
            this.dos.writeInt(LevelSaver.MAGIC);
            this.dos.writeUTF(levelName);
            this.dos.writeUTF("Author!");
            this.dos.writeUTF("8/12/2022");
            this.dos.writeUTF(OpenCraft.getVersion());
            this.dos.writeInt(OpenCraft.getLevel().getSeed());
            this.dos.flush();
        } catch (Exception e) {
            System.out.println("Error while loading world: " + StackTrace.getStackTrace(e));
        }
    }

    public void load() {
        try {
            new File("saves" + File.separator + levelName).mkdir();
            this.dis = new DataInputStream(
                    new GZIPInputStream(
                            new FileInputStream("saves" + File.separator + levelName + File.separator + "level.data")));
            new File("saves" + File.separator + levelName + File.separator + "world_region").mkdir();
            new File("saves" + File.separator + levelName + File.separator + "hell_region").mkdir();

            File folder = new File("saves" + File.separator + levelName + File.separator + "world_region");
            File[] listOfFiles = folder.listFiles();

            for (File listOfFile : listOfFiles) {
                String name = listOfFile.getName();
                if (listOfFile.isFile() && name.endsWith(".dat")) {
                    DataInputStream local_dis = new DataInputStream(
                            new GZIPInputStream(
                                    new FileInputStream("saves" + File.separator + levelName + File.separator + "world_region" + File.separator + name)));
                    if (local_dis.readInt() == LevelSaver.MAGIC) {
                        int x = local_dis.readInt();
                        int y = local_dis.readInt();

                        while (true) {
                            int block_x = local_dis.readInt();
                            int block_y = local_dis.readInt();
                            int block_z = local_dis.readInt();
                            int block_id = local_dis.readInt();

                            OpenCraft.getLevel().setBlock(block_x, block_y, block_z, Block.getBlockByIntId(block_id));

                            if (block_id == -1) {
                                break;
                            }
                        }
                    }

                    local_dis.close();
                }
            }

        } catch (Exception e) {
            System.out.println("Error while loading world: " + StackTrace.getStackTrace(e));
        }
    }

    public void update() {
        for (Regions reg: updatedRegions) {
            if (reg.region == null) continue;

            try {
                Vector2i position = reg.region.getChunk().getPosition();
                String dir = "world_region";

                if (reg.level.getLevelType() == Level.LevelType.HELL) {
                    dir = "hell_region";
                }

                DataOutputStream local_dos = new DataOutputStream(
                        new GZIPOutputStream(
                                new FileOutputStream("saves" + File.separator + levelName + File.separator + dir + File.separator + position.x + "x" + position.y + ".dat")));
                local_dos.writeInt(LevelSaver.MAGIC);
                local_dos.writeInt(position.x);
                local_dos.writeInt(position.y);
                reg.region.writeBlocksBytes(local_dos);
                local_dos.writeInt(-1);
                local_dos.writeInt(-1);
                local_dos.writeInt(-1);
                local_dos.writeInt(-1);
                local_dos.close();
            } catch (Exception e) {
                System.out.println("Error while saving world: " + StackTrace.getStackTrace(e));
            }
        }

        this.updatedRegions.clear();
    }

    public void destroy() {
        try {
            this.updatedRegions.clear();
            this.dos.close();
        } catch (Exception e) {
            System.out.println("Error while saving world: " + StackTrace.getStackTrace(e));
        }
    }

    public void appendRegion(Region region, Level level) {
        return;
//        // todo: optimize it
//        boolean found = false;
//        for (Regions reg: updatedRegions) {
//            if (reg.region == null || region == null) continue;
//            if (reg.region.getChunk() == null || region.getChunk() == null) continue;
//            if (reg.region.getChunk().getPosition().equals(region.getChunk().getPosition())) {
//                found = true;
//                break;
//            }
//        }
//
//        if (!found) {
//            updatedRegions.add(new Regions(region, level));
//        }

    }

}
