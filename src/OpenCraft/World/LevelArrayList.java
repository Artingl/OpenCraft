package OpenCraft.World;

import OpenCraft.World.Block.Block;

import java.util.ArrayList;
import java.util.HashMap;

public class LevelArrayList
{

    private HashMap<Integer, Block> list;

    public LevelArrayList()
    {
        list = new HashMap<>();
    }

    public Block get(int i)
    {
        return list.get(i);
    }

    public boolean contains(int i)
    {
        return list.containsKey(i);
    }

    public void set(int i, Block block)
    {
        list.put(i, block);
    }

}
