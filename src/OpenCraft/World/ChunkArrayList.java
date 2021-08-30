package OpenCraft.World;

import OpenCraft.World.Block.Block;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ChunkArrayList
{

    private HashMap<String, Chunk> list;

    public ChunkArrayList(HashMap<String, Chunk> l)
    {
        list = l;
    }

    public ChunkArrayList()
    {
        list = new HashMap<>();
    }

    public Chunk get(int x, int y, int z)
    {
        return list.get(x+","+y+","+z);
    }

    public boolean contains(int x, int y, int z)
    {
        return list.containsKey(x+","+y+","+z);
    }

    public void set(int x, int y, int z, Chunk block)
    {
        list.put(x+","+y+","+z, block);
    }

    public Set<Map.Entry<String, Chunk>> entry()
    {
        return copy().getList().entrySet();
    }

    public ChunkArrayList copy() {
        return new ChunkArrayList(new HashMap<>(list));
    }

    public void remove(int x, int y, int z) {
        list.remove(x+","+y+","+z);
    }

    public HashMap<String, Chunk> getList()
    {
        return list;
    }

}
