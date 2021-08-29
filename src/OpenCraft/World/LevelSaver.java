package OpenCraft.World;

import OpenCraft.Game.Rendering.LevelRenderer;
import OpenCraft.OpenCraft;
import OpenCraft.World.Block.Block;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class LevelSaver
{

    private String name;
    private int seed;
    private boolean save = true;
    private Thread th;

    public LevelSaver(String name, int seed, boolean load)
    {
        this.name = name;
        this.seed = seed;

        if (load && new File("saves" + File.separator + name + File.separator + "level.data").exists() && new File("saves" + File.separator + name + File.separator + "blocks.data").exists())
        {
            try {
                DataInputStream dis = new DataInputStream(new GZIPInputStream(new FileInputStream("saves" + File.separator + name + File.separator + "level.data")));
                int header = dis.readInt();
                if (header <= 78924536)
                {
                    String worldName = dis.readUTF();
                    String author = dis.readUTF();
                    String creationDate = dis.readUTF();
                    String version = dis.readUTF();
                    this.seed = dis.readInt();

                    OpenCraft.getLevel().generateWorld(this.seed);

                    DataInputStream blocks_dis = new DataInputStream(new GZIPInputStream(new FileInputStream("saves" + File.separator + name + File.separator + "blocks.data")));
                    int blocks_header = blocks_dis.readInt();
                    if (blocks_header <= 78924536)
                    {
                        byte[] blocks = new byte[blocks_dis.readInt()];
                        blocks_dis.readFully(blocks);
                        for (int i = 0; i < blocks.length; i++)
                        {
                            OpenCraft.getLevel().setBlockWithoutRendering(i, Block.getBlockByIntId(blocks[i]));
                        }
                    }
                    blocks_dis.close();

                    //File folder = new File("saves" + File.separator + name + File.separator + "chunks");
                    //File[] listOfFiles = folder.listFiles();

                    //for (File file : listOfFiles) {
                    //    if (file.isFile()) {
                    //        DataInputStream dis_chunk = new DataInputStream(new GZIPInputStream(new FileInputStream("saves" + File.separator + name + File.separator + "chunks" + File.separator + file.getName())));
                    //        for (int k = 0; k <= 8; k++)
                    //        {
                    //            int chunk_header = dis_chunk.readInt();

                    //            if (chunk_header == 78924536)
                    //            {
                    //                int chunk_x = dis_chunk.readInt();
                    //                int chunk_y = dis_chunk.readInt();
                    //                int chunk_z = dis_chunk.readInt();
                    //                byte[] blocks = new byte[16 * 16 * 16];
                    //                dis_chunk.readFully(blocks);
                    //                int i = 0;
                    //                for (int x = 0; x < 16; x++)
                    //                {
                    //                    for (int y = 0; y < 16; y++)
                    //                    {
                    //                        for (int z = 0; z < 16; z++)
                    //                        {
                    //                            OpenCraft.getLevel().setBlockWithoutRendering(chunk_x + x, chunk_y + y, chunk_z + z, Block.getBlockByIntId(blocks[i]));
                    //                            i++;
                    //                        }
                    //                    }
                    //                }
                    //            }
                    //        }

                    //        OpenCraft.getLevel().calcLightDepths(0, 0, Level.WIDTH, Level.HEIGHT);
                    //        dis_chunk.close();
                    //    }
                    //}

                }
                dis.close();
            } catch (IOException e) {}
        }
        else {
            new File("saves").mkdir();
            new File("saves" + File.separator + name).mkdir();
            try {
                DataOutputStream dos = new DataOutputStream(new GZIPOutputStream(new FileOutputStream("saves" + File.separator + name + File.separator + "level.data")));
                dos.writeInt(78924536);
                dos.writeUTF(name);
                dos.writeUTF("author");
                dos.writeUTF("creation_date");
                dos.writeUTF(OpenCraft.getVersion());
                dos.writeInt(seed);
                dos.close();
            } catch (IOException e) { }

        }
        //startSaveThread();

        if (!load)
        {
            OpenCraft.getLevel().generateWorld(seed);
        }
    }

    public void save()
    {
        DataOutputStream dos;
        try {
            dos = new DataOutputStream(new GZIPOutputStream(new FileOutputStream("saves" + File.separator + name + File.separator + "blocks.data")));
            dos.writeInt(78924536);
            dos.writeInt(OpenCraft.getLevel().getByteBlocks().length);
            dos.write(OpenCraft.getLevel().getByteBlocks());
            dos.close();
        } catch (IOException e) { }
    }

    public String getName() {
        return name;
    }

    public int getSeed() {
        return seed;
    }

    public void destroy()
    {
        //save = false;
        save();
    }

    /*public void startSaveThread()
    {
        new File("saves" + File.separator + name + File.separator + "chunks").mkdir();

        th = new Thread(() -> {
            try {
                while (save)
                {
                    if (OpenCraft.getLevelRenderer() == null) continue;
                    if (OpenCraft.getLevelRenderer().getChunks() == null) continue;
                    Chunk[] chunks = OpenCraft.getLevelRenderer().getChunks();

                    int id = 0;
                    for (int i = 0; i < chunks.length; i+=8)
                    {
                        DataOutputStream dos = new DataOutputStream(new GZIPOutputStream(new FileOutputStream("saves" + File.separator + name + File.separator + "chunks" + File.separator + "c" + id + ".data")));
                        boolean br = false;
                        for (int k = 0; k <= 8; k++)
                        {
                            if (i + k > chunks.length - 1)
                            {
                                br = true;
                                break;
                            }
                            Chunk chunk = chunks[i + k];

                            dos.writeInt(78924536);
                            dos.writeInt(chunk.getX());
                            dos.writeInt(chunk.getY());
                            dos.writeInt(chunk.getZ());
                            dos.write(chunk.getBlocks());
                        }

                        dos.close();

                        if (br) break;
                        id++;
                    }

                }
            } catch (IOException e) {}
            Thread.currentThread().interrupt();
        });
        th.start();
    }*/

}
