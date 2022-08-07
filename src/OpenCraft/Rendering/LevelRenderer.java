package OpenCraft.Rendering;

import OpenCraft.Interfaces.LevelRendererListener;
import OpenCraft.OpenCraft;
import OpenCraft.World.Chunk;
import OpenCraft.World.Level;
import OpenCraft.math.Vector2i;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

public class LevelRenderer
{
    public static int CHUNK_UPDATES = 0;
    public static int CHUNK_RENDERED = 0;

    private ArrayList<Chunk> chunks;
    private ArrayList<LevelRendererListener> listeners;

    public LevelRenderer() {
        this.chunks = new ArrayList<>();
        this.listeners = new ArrayList<>();

        this.prepareChunks();
    }

    public void prepareChunks()
    {
        Level level = OpenCraft.getLevel();
        int renderDistance = OpenCraft.getRenderDistance();

        if (chunks.isEmpty()) {
            for (int x = -renderDistance; x < renderDistance; x++) {
                for (int z = -renderDistance; z < renderDistance; z++) {
                    Chunk chunk = new Chunk(new Vector2i(x, z));
                    chunks.add(chunk);

                    this.publishListenersEvent(LevelRendererListener.Events.CHUNK_UPDATE, level, chunk);
                }
            }

            System.gc();
        }
        else {
            System.out.println("TODO: implement LevelRenderer chunks cleaner");
        }
    }

    public void render(Frustum frustum) {
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureEngine.getTerrain());

        CHUNK_RENDERED = 0;
        for(Chunk chunk: chunks) {
            if (!chunk.isDirty() && frustum.isVisible(chunk.getAABB())) {
                for (int layer = 0; layer < Chunk.CHUNK_LAYERS; layer++) {
                    if (frustum.isVisible(chunk.getSimpleAABB(layer))) {
                        chunk.render(layer);
                        CHUNK_RENDERED++;
                    }
                }
            }
        }

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    }

    public void registerListener(LevelRendererListener listener) {
        listeners.add(listener);
    }

    public void destroy() {
        this.publishListenersEvent(LevelRendererListener.Events.LEVEL_DESTROY, OpenCraft.getLevel(), null);

        for(Chunk chunk: chunks) {
            chunk.destroy();
        }

        chunks.clear();
        System.gc();
    }

    public void sendEvent(LevelRendererListener.Events event, Level level, Chunk chunk) {
        if (event == LevelRendererListener.Events.CHUNK_UPDATE) {
            if (chunk != null)
                chunk.prepare();
        }

        this.publishListenersEvent(event, level, chunk);
    }

    private void publishListenersEvent(LevelRendererListener.Events event, Level level, Chunk chunk) {
        for (LevelRendererListener listener : this.listeners) {
            if (event == LevelRendererListener.Events.CHUNK_UPDATE) {
                listener.chunkUpdate(level, chunk);
            }
            else if (event == LevelRendererListener.Events.LEVEL_DESTROY) {
                listener.levelDestroy(level);
            }
            else if (event == LevelRendererListener.Events.LEVEL_SWITCH) {
                listener.levelSwitch(level);
            }
        }
    }

    public Chunk getChunkByBlockPos(int x, int z) {
        for (Chunk chunk: this.chunks) {
            if (chunk.getPosition().x == x >> 4 && chunk.getPosition().y == z >> 4) {
                return chunk;
            }
        }

        return null;
    }

    public void switchWorld() {
        // todo: fix memory leak. it doubles the amount of used memory
        //       when the very first switch is called. after that no memory leak
        //       was found.

        this.publishListenersEvent(LevelRendererListener.Events.LEVEL_SWITCH, OpenCraft.getLevel(), null);

        for(Chunk chunk: chunks) {
            chunk.destroy();
        }

        chunks.clear();
        System.gc();

        // this place might be in a thread,
        // so we'd like to run it inside GL context
        OpenCraft.runInGLContext(this::prepareChunks);
    }
}
