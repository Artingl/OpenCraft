package OpenCraft.Rendering;

import OpenCraft.Interfaces.ITick;
import OpenCraft.Interfaces.LevelRendererListener;
import OpenCraft.OpenCraft;
import OpenCraft.World.Chunk.Chunk;
import OpenCraft.World.Chunk.ChunksSorter;
import OpenCraft.World.Entity.EntityPlayer;
import OpenCraft.World.Entity.PlayerController;
import OpenCraft.World.Level.Level;
import OpenCraft.math.Vector2i;
import OpenCraft.math.Vector3i;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class LevelRenderer implements ITick
{
    public static int CHUNK_UPDATES = 0;
    public static int CHUNKS_RENDERED = 0;

    private int chunksUpdate;
    private ArrayList<Chunk> chunks;
    private ArrayList<LevelRendererListener> listeners;

    public LevelRenderer() {
        this.chunks = new ArrayList<>();
        this.listeners = new ArrayList<>();

        OpenCraft.registerTickEvent(this);
    }

    public void prepareChunks()
    {
        Level level = OpenCraft.getLevel();
        int renderDistance = OpenCraft.getRenderDistance();

        if (chunks.isEmpty()) {
            for (int x = -renderDistance; x < renderDistance; x++) {
                for (int z = -renderDistance; z < renderDistance; z++) {
                    Chunk chunk = new Chunk(new Vector2i(x >> 4, z >> 4));
                    for (int i = 0; i < Chunk.CHUNK_LAYERS; i++)
                        chunk.buildLayer(i);
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

        CHUNKS_RENDERED = 0;
        int updates = 0;

        chunks.sort(new ChunksSorter(OpenCraft.getLevel().getPlayerEntity()));

        for(Chunk chunk: chunks) {
            if (frustum.isVisible(chunk.getAABB())) {
                for (int layer = 0; layer < Chunk.CHUNK_LAYERS; layer++) {
                    if (frustum.isVisible(chunk.getSimpleAABB(layer))) {
                        if (!chunk.layerState(layer) && ++updates < 5) {
                            if (!chunk.buildLayer(layer))
                                updates--;
                        }

                        chunk.render(layer);
                        CHUNKS_RENDERED++;
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

    public void sendEvent(LevelRendererListener.Events event, Level level, Chunk chunk, Vector3i position) {
        if (event == LevelRendererListener.Events.CHUNK_UPDATE) {
            if (chunk != null)
                chunk.buildLayer(position.y / (256 / Chunk.CHUNK_LAYERS));
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
            if (chunk == null) {
                continue;
            }

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

    @Override
    public void tick() {
        // check if we're able to render new chunks
        int renderDistance = OpenCraft.getRenderDistance();

        Level level = OpenCraft.getLevel();
        EntityPlayer player = level.getPlayerEntity();

        for (int x = -renderDistance; x < renderDistance; x++) {
            for (int z = -renderDistance; z < renderDistance; z++) {
                Vector2i chunkPosition = new Vector2i(
                        (x * 16) + (int)player.getX(),
                        (z * 16) + (int)player.getZ()
                );

                if (getChunkByBlockPos(chunkPosition.x, chunkPosition.y) == null) {
                    Chunk chunk = new Chunk(new Vector2i(chunkPosition.x >> 4, chunkPosition.y >> 4));
                    chunks.add(chunk);

                    this.publishListenersEvent(LevelRendererListener.Events.CHUNK_UPDATE, level, chunk);
                    chunksUpdate++;
                }
            }
        }

        // remove chunks which out of our render distance
        for (int index = 0; index < chunks.size(); index++) {
            Chunk chunk = chunks.get(index);

            if (chunk == null) {
                continue;
            }

            int dist = (int) (chunk.distanceToEntity(player));

            if (dist > OpenCraft.getRenderDistance() + 2) {
                chunk.destroy();
                chunks.remove(chunk);
                chunksUpdate++;
            }
        }

        if (chunksUpdate > 4096) {
            // clear memory after some time (in case some garbage is still in memory)
            System.out.println("Clearing memory");
            new Thread(System::gc).start();
            chunksUpdate = 0;
        }
    }

    public int getChunksAmount() {
        return chunks.size();
    }
}
