package OpenCraft.World.Level;

import OpenCraft.World.Block.Block;

import java.util.ArrayList;
import java.util.HashMap;

public class LevelArrayList
{

    private HashMap<Long, Block> list;

    public LevelArrayList()
    {
        list = new HashMap<>();
    }

    public Block get(long i)
    {
        return list.get(i);
    }

    public boolean contains(long i)
    {
        return list.containsKey(i);
    }

    public void set(long i, Block block)
    {
        list.put(i, block);
    }

    public void clear() {
        list.clear();
    }
}
