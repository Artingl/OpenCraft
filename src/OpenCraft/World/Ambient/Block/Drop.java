package OpenCraft.World.Ambient.Block;

import OpenCraft.Interfaces.IRenderHandler;
import OpenCraft.OpenCraft;
import OpenCraft.Rendering.BlockRenderer;
import OpenCraft.Rendering.TextureEngine;
import OpenCraft.Rendering.VerticesBuffer;
import OpenCraft.World.Block.Block;
import OpenCraft.World.Entity.Entity;
import OpenCraft.World.Item.ItemBlock;
import OpenCraft.math.Vector3i;
import OpenCraft.sound.Sound;
import OpenCraft.utils.Random;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

public class Drop extends Entity implements IRenderHandler {

    private final ItemBlock itemBlock;
    private int timer = 30;
    private boolean picked;

    public Drop(ItemBlock itemBlock, Vector3i position) {
        this.itemBlock = itemBlock;
        this.picked = false;

        float x = (position.x + (((float) Random.inRange(-4, 4)) + 0.5F) / 4f);
        float y = position.y;
        float z = (position.z + (((float) Random.inRange(-4, 4)) + 0.5F) / 4f);

        this.setSize(0.4F, 0.4F);
        this.heightOffset = this.height / 2f;
        this.setPosition(x, y + 0.4f, z);
        this.xd = (x - 0.5F) + (float)(Math.random() * 2.0D - 1.0D) * 0.4F;
        this.yd = (y - 0.5F) + (float)(Math.random() * 2.0D - 1.0D) * 0.4F;
        this.zd = (z - 0.5F) + (float)(Math.random() * 2.0D - 1.0D) * 0.4F;
        float speed = (float)(Math.random() + Math.random() + 1.0D) * 0.15F;
        float dd = (float)Math.sqrt(this.xd * this.xd + this.yd * this.yd + this.zd * this.zd);
        this.xd = this.xd / dd * speed * 0.4F;
        this.yd = this.yd / dd * speed * 0.4F + 0.1F;
        this.zd = this.zd / dd * speed * 0.4F;
    }

    @Override
    public void render() {
        super.render();
        if (this.picked) return;

        VerticesBuffer t = VerticesBuffer.instance;

        float animation = 0;

        if (this.onGround) {
            animation = 1.8f - (float)Math.abs(Math.sin(((float)(this.timer / 5f) % OpenCraft.getFPS()) / OpenCraft.getFPS() * Math.PI * 2.0f) * 0.1f);
            animation = animation * 100.0f / ((24f * 12f) + 32f);
            animation = (animation - 0.5333f) * 10;
        }

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureEngine.getTerrain());
//        GL11.glRotatef(rotation, 0, 1, 0);

        t.begin();
        Block block = itemBlock.getBlock();
        if (block.isTile()) {
            BlockRenderer.renderTile0(t, x, y + animation, z, 0.3f, block);
            BlockRenderer.renderTile1(t, x, y + animation, z, 0.3f, block);
        }
        else {
            BlockRenderer.renderBackSide(t, x, y + animation, z, this.height / 2f, block);
            BlockRenderer.renderTopSide(t, x, y + animation, z, this.height / 2f, block);
            BlockRenderer.renderLeftSide(t, x, y + animation, z, this.height / 2f, block);
            BlockRenderer.renderRightSide(t, x, y + animation, z, this.height / 2f, block);
            BlockRenderer.renderBottomSide(t, x, y + animation, z, this.height / 2f, block);
            BlockRenderer.renderFrontSide(t, x, y + animation, z, this.height / 2f, block);
        }
        t.end();

//        GL11.glRotatef(-rotation, 0, 1, 0);
        GL11.glEndList();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);


        if (this.onGround) {
            this.timer++;
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.picked) return;

        this.yd = (float)((double)this.yd - 0.04D);
        this.move(this.xd, this.yd, this.zd);
        this.xd *= 0.98F;
        this.yd *= 0.98F;
        this.zd *= 0.98F;
        if (this.onGround) {
            this.xd *= 0.7F;
            this.zd *= 0.7F;
        }

        ArrayList<Entity> entities = OpenCraft.getLevel().getEntities();

        for (Entity entity: entities) {
            if (entity.hasInventory() && entity.aabb.intersects(this.aabb)) {
                Sound.loadAndPlay("resources/sounds/player/pick.wav");
                if (entity.pick(itemBlock))
                    this.picked();
                break;
            }
        }
    }

    public void picked() {
        // todo: do it able to completely remove from the world
        this.destroy();
        this.picked = true;
    }

}
