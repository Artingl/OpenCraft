package OpenCraft.Game.Rendering;

import OpenCraft.World.Entity.Player;
import OpenCraft.Interfaces.LevelRendererListener;
import OpenCraft.OpenCraft;
import OpenCraft.World.Chunk;
import OpenCraft.World.DirtyChunkSorter;
import OpenCraft.World.DistanceSorter;
import OpenCraft.World.Level;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LevelRenderer implements LevelRendererListener
{

    public final static int C_WIDTH =( Level.WIDTH + 16 - 1) / 16;
    public final static int C_HEIGHT = (Level.HEIGHT + 16 - 1) / 16;
    public final static int C_DEPTH = (Level.DEPTH + 16 - 1) / 16;

    private Chunk[] chunks;
    private Chunk[] sortedChunks;
    private float lX;
    private float lY;
    private float lZ;

    public LevelRenderer() {
        this.lX = -900000.0F;
        this.lY = -900000.0F;
        this.lZ = -900000.0F;

        OpenCraft.getLevel().addListener(this);
        chunks = new Chunk[C_WIDTH * C_DEPTH * C_HEIGHT];
        sortedChunks = new Chunk[C_WIDTH * C_DEPTH * C_HEIGHT];

        for(int x = 0; x < C_WIDTH; ++x) {
            for(int y = 0; y < C_DEPTH; ++y) {
                for(int z = 0; z < C_HEIGHT; ++z) {
                    int x0 = x * 16;
                    int y0 = y * 16;
                    int z0 = z * 16;
                    int x1 = (x + 1) * 16;
                    int y1 = (y + 1) * 16;
                    int z1 = (z + 1) * 16;
                    if (x1 > Level.WIDTH) {
                        x1 = Level.WIDTH;
                    }

                    if (y1 > Level.DEPTH) {
                        y1 = Level.DEPTH;
                    }

                    if (z1 > Level.HEIGHT) {
                        z1 = Level.HEIGHT;
                    }

                    this.chunks[(x + y * C_WIDTH) * C_HEIGHT + z] = new Chunk(OpenCraft.getLevel(), x0, y0, z0, x1, y1, z1);
                    this.sortedChunks[(x + y * C_WIDTH) * C_HEIGHT + z] = this.chunks[(x + y * C_WIDTH) * C_HEIGHT + z];
                }
            }
        }

    }

    public List<Chunk> getAllDirtyChunks() {
        ArrayList<Chunk> dirty = null;

        for(int i = 0; i < this.chunks.length; ++i) {
            Chunk chunk = this.chunks[i];
            if (chunk.isDirty()) {
                if (dirty == null) {
                    dirty = new ArrayList();
                }

                dirty.add(chunk);
            }
        }

        return dirty;
    }

    public void render(Player player, int layer) {
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.getTerrain());

        float xd = player.getX() - this.lX;
        float yd = player.getY() - this.lY;
        float zd = player.getZ() - this.lZ;
        if (xd * xd + yd * yd + zd * zd > 64.0F) {
            this.lX = player.getX();
            this.lY = player.getY();
            this.lZ = player.getZ();
            Arrays.sort(this.sortedChunks, new DistanceSorter(player));
        }

        for(int i = 0; i < this.sortedChunks.length; ++i) {
            if (this.sortedChunks[i].visible) {
                float dd = (float)(OpenCraft.getRenderDistance() * 16);
                if (this.sortedChunks[i].distanceToSqr(player) < dd * dd) {
                    this.sortedChunks[i].render(layer);
                }
            }

        }

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
    }

    public void updateDirtyChunks(Player player) {
        List<Chunk> dirty = this.getAllDirtyChunks();
        if (dirty != null) {
            Collections.sort(dirty, new DirtyChunkSorter(player));

            for(int i = 0; i < 4 && i < dirty.size(); ++i) {
                if (Frustum.getFrustum().isVisible(((Chunk)dirty.get(i)).aabb))
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
                    this.chunks[(x + y * C_WIDTH) * C_WIDTH + z].setDirty();
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
        for(int i = 0; i < this.chunks.length; ++i) {
            this.chunks[i].visible = frustum.isVisible(this.chunks[i].aabb);
        }

    }

    public Chunk[] getChunks() {
        return sortedChunks;
    }
}
