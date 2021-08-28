package OpenCraft.World;

import OpenCraft.OpenCraft;
import OpenCraft.World.Block.Block;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class LevelSaver
{

    private String name;
    private int seed;

    public LevelSaver(String name, int seed)
    {
        this.name = name;
        this.seed = seed;

        boolean haveToGen = true;

        if (new File(name + ".world").exists())
        {
            try {
                DataInputStream dis = new DataInputStream(new GZIPInputStream(new FileInputStream(name + ".world")));
                int header = dis.readInt();
                if (header == 78924536)
                {
                    haveToGen = false;
                    this.seed = dis.readInt();
                    int size = dis.readInt();
                    OpenCraft.getLevel().generateWorld(this.seed);
                    byte[] blocks = new byte[size];
                    dis.readFully(blocks);

                    for (int i = 0; i < size; i++)
                    {
                        OpenCraft.getLevel().setBlockWithoutRendering(i, Block.getBlockByIntId(blocks[i]));
                    }
                    dis.close();
                }

            } catch (IOException e) {}
        }

        if (haveToGen)
        {
            OpenCraft.getLevel().generateWorld(seed);
        }
    }

    public String getName() {
        return name;
    }

    public int getSeed() {
        return seed;
    }

    public void destroy() throws IOException {
        DataOutputStream dos = new DataOutputStream(new GZIPOutputStream(new FileOutputStream(name + ".world")));
        dos.writeInt(78924536);
        dos.writeInt(seed);
        dos.writeInt(OpenCraft.getLevel().getByteBlocks().length);
        dos.write(OpenCraft.getLevel().getByteBlocks());
        dos.close();
    }
}
