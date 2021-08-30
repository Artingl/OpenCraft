package OpenCraft.Rendering;

import OpenCraft.World.*;
import OpenCraft.World.Entity.Player;
import OpenCraft.Interfaces.LevelRendererListener;
import OpenCraft.OpenCraft;
import org.lwjgl.opengl.GL11;

import java.lang.reflect.Array;
import java.util.*;

public class LevelRenderer implements LevelRendererListener
{

    public final static int C_WIDTH = OpenCraft.getRenderDistance();//(Level.WIDTH + 16 - 1) / 16;
    public final static int C_HEIGHT = OpenCraft.getRenderDistance();//(Level.HEIGHT + 16 - 1) / 16;
    public final static int C_DEPTH = (Level.DEPTH + 16 - 1) / 16;

    private ChunkArrayList chunks;
    private ChunkArrayList newChunks;
    private boolean readyToCopy = false;
    private float lX;
    private float lY;
    private float lZ;

    public LevelRenderer() {
        this.lX = -900000.0F;
        this.lY = -900000.0F;
        this.lZ = -900000.0F;

        OpenCraft.getLevel().addListener(this);
        chunks = new ChunkArrayList();
    }

    public void destroy()
    {
        for(Map.Entry<String, Chunk> m: chunks.entry()) {
            Chunk chunk = m.getValue();
            if (chunk == null) continue;
            chunk.reset();
        }
    }

    public List<Chunk> getAllDirtyChunks() {
        ArrayList<Chunk> dirty = null;

        for(Map.Entry<String, Chunk> m: chunks.entry()) {
            Chunk chunk = m.getValue();
            if (chunk == null) continue;

            if (chunk.isDirty() || (chunk.haveToRerender && OpenCraft.getLevel().getBlock(chunk.getX() * 16, chunk.getY() * 16, chunk.getZ() * 16) != null)) {
                chunk.haveToRerender = false;
                if (dirty == null) {
                    dirty = new ArrayList();
                }

                dirty.add(chunk);
            }
        }

        return dirty;
    }

    public void checkCopyState() {
        if (readyToCopy)
        {
            readyToCopy = false;
            chunks = newChunks.copy();
        }
    }

    public void updateChunks()
    {
        int start_x = (int) (((OpenCraft.getPlayer().getX()) / 16) - C_WIDTH / 2f);
        int start_z = (int) (((OpenCraft.getPlayer().getZ()) / 16) - C_HEIGHT / 2f);
        int end_x =  (int)  (((OpenCraft.getPlayer().getX()) / 16) + C_WIDTH / 2f);
        int end_z =  (int)  (((OpenCraft.getPlayer().getZ()) / 16) + C_HEIGHT / 2f);

        newChunks = chunks.copy();
        for(int x = start_x; x < end_x; ++x) {
            for(int y = 0; y < C_DEPTH; ++y) {
                for(int z = start_z; z < end_z; ++z) {
                    if (newChunks.contains(x, y, z)) continue;

                    int x0 = x * 16;
                    int y0 = y * 16;
                    int z0 = z * 16;
                    int x1 = (x + 1) * 16;
                    int y1 = (y + 1) * 16;
                    int z1 = (z + 1) * 16;

                    newChunks.set(x, y, z, new Chunk(OpenCraft.getLevel(), x0, y0, z0, x1, y1, z1));
                }
            }
        }

        readyToCopy = true;
    }

    public void render(Player player, int layer) {
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureEngine.getTerrain());

        Frustum frustum = Frustum.getFrustum();

        for(Map.Entry<String, Chunk> m: chunks.entry()) {
            Chunk chunk = m.getValue();
            if (chunk == null) continue;
            if (chunk.visible) {
                float dd = (float)(OpenCraft.getRenderDistance() * 16);
                if (chunk.distanceToSqr(player) < dd * dd) {
                    chunk.render(layer);
                }
                else {
                    chunks.remove(chunk.getX(), chunk.getY(), chunk.getZ());
                    chunk.reset();
                    chunk = null;
                }
            }

        }

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    }

    public void updateDirtyChunks(Player player) {
        List<Chunk> dirty = this.getAllDirtyChunks();
        if (dirty != null) {
            Collections.sort(dirty, new DirtyChunkSorter(player));

            for(int i = 0; i < 4 && i < dirty.size(); ++i) {
                ((Chunk)dirty.get(i)).prepare();
            }

        }
    }

    public void setDirty(int x0, int y0, int z0, int x1, int y1, int z1) {
        x0 /= 16;
        x1 /= 16;
        y0 /= 16;
        y1 /= 16;
        z0 /= 16;
        z1 /= 16;
        if (x0 < 0) {
            x0 = 0;
        }

        if (y0 < 0) {
            y0 = 0;
        }

        if (z0 < 0) {
            z0 = 0;
        }

        if (x1 >= C_WIDTH) {
            x1 = C_WIDTH - 1;
        }

        if (y1 >= C_DEPTH) {
            y1 = C_DEPTH - 1;
        }

        if (z1 >= C_HEIGHT) {
            z1 = C_HEIGHT - 1;
        }

        for(int x = x0; x <= x1; ++x) {
            for(int y = y0; y <= y1; ++y) {
                for(int z = z0; z <= z1; ++z) {
                    this.chunks.get(x, y, z).setDirty();
                    //this.chunks[(x + y * C_WIDTH) * C_WIDTH + z].setDirty();
                }
            }
        }

    }

    public void tileChanged(int x, int y, int z) {
        this.setDirty(x - 1, y - 1, z - 1, x + 1, y + 1, z + 1);
    }

    public void lightColumnChanged(int x, int z, int y0, int y1) {
        this.setDirty(x - 1, y0 - 1, z - 1, x + 1, y1 + 1, z + 1);
    }

    public void allChanged() {
        this.setDirty(0, 0, 0, Level.WIDTH, Level.DEPTH, Level.HEIGHT);
    }

    public void cull(Frustum frustum) {
        for(Map.Entry<String, Chunk> m: chunks.entry()) {
            Chunk chunk = m.getValue();
            if (chunk == null) continue;
            chunk.visible = frustum.isVisible(chunk.aabb);
        }
    }

}
