package OpenCraft.World;

import OpenCraft.Rendering.LevelRenderer;
import OpenCraft.Interfaces.ITick;
import OpenCraft.OpenCraft;
import OpenCraft.World.Block.Block;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class LevelSaver implements ITick
{

    private int tickCnt = 1;
    private String name;
    private int seed;

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

                }
                dis.close();
            } catch (IOException e) {}
        }
        //else {
        //    new File("saves").mkdir();
        //    new File("saves" + File.separator + name).mkdir();
        //    try {
        //        DataOutputStream dos = new DataOutputStream(new GZIPOutputStream(new FileOutputStream("saves" + File.separator + name + File.separator + "level.data")));
        //        dos.writeInt(78924536);
        //        dos.writeUTF(name);
        //        dos.writeUTF("author");
        //        dos.writeUTF("creation_date");
        //        dos.writeUTF(OpenCraft.getVersion());
        //        dos.writeInt(seed);
        //        dos.close();
        //    } catch (IOException e) { }
        //}

        //if (!load)
        //{
            OpenCraft.getLevel().generateWorld(seed);
        //}

        OpenCraft.registerTickEvent(this);
    }

    public void save()
    {
        //DataOutputStream dos;
        //try {
        //    dos = new DataOutputStream(new GZIPOutputStream(new FileOutputStream("saves" + File.separator + name + File.separator + "blocks.data")));
        //    dos.writeInt(78924536);
        //    dos.writeInt(OpenCraft.getLevel().getByteBlocks().length);
        //    dos.write(OpenCraft.getLevel().getByteBlocks());
        //    dos.close();
        //    OpenCraft.setWorldSavingState(true);
        //} catch (IOException e) { }
    }

    public String getName() {
        return name;
    }

    public int getSeed() {
        return seed;
    }

    public void destroy()
    {
        save();
    }

    @Override
    public void tick()
    {
        if (tickCnt >= 2400)
        {
            save();
            tickCnt = 1;
        }
        tickCnt++;
    }
}
