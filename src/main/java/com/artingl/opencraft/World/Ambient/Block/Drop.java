package com.artingl.opencraft.World.Ambient.Block;

import com.artingl.opencraft.Math.Vector2f;
import com.artingl.opencraft.Math.Vector3f;
import com.artingl.opencraft.Rendering.RenderHandler;
import com.artingl.opencraft.Opencraft;
import com.artingl.opencraft.Rendering.BlockRenderer;
import com.artingl.opencraft.Rendering.TextureEngine;
import com.artingl.opencraft.Rendering.VerticesBuffer;
import com.artingl.opencraft.World.Block.Block;
import com.artingl.opencraft.World.Entity.Entity;
import com.artingl.opencraft.World.Item.ItemBlock;
import com.artingl.opencraft.Math.Vector3i;
import com.artingl.opencraft.Utils.Random;
import org.lwjgl.opengl.GL11;

public class Drop extends Entity implements RenderHandler {

    private final ItemBlock itemBlock;
    private int timer = 30;
    private boolean picked;

    public Drop(ItemBlock itemBlock, Vector3i position) {
        super(Opencraft.getLevel());
        this.itemBlock = itemBlock;
        this.picked = false;

        float x = (position.x + (((float) Random.inRange(-4, 4)) + 0.5F) / 4f);
        float y = position.y;
        float z = (position.z + (((float) Random.inRange(-4, 4)) + 0.5F) / 4f);

        this.setSize(0.4F, 0.4F);
        this.setHeightOffset(this.getSize().y / 2.0F);
        this.setPosition(x, y + 0.4f, z);
        Vector3f vel = new Vector3f(
                (x - 0.5F) + (float)(Math.random() * 2.0D - 1.0D) * 0.4F,
                (y - 0.5F) + (float)(Math.random() * 2.0D - 1.0D) * 0.4F,
                (z - 0.5F) + (float)(Math.random() * 2.0D - 1.0D) * 0.4F
        );
        this.setVelocity(vel);
        float speed = (float)(Math.random() + Math.random() + 1.0D) * 0.15F;
        float dd = (float)Math.sqrt(vel.x * vel.x + vel.y * vel.y + vel.z * vel.z);
        this.setVelocity(new Vector3f(
                vel.x / dd * speed * 0.4F,
                vel.y / dd * speed * 0.4F + 0.1F,
                vel.z / dd * speed * 0.4F
        ));
    }

    @Override
    public void render() {
        super.render();
        if (this.picked) return;

        VerticesBuffer t = VerticesBuffer.getGlobalInstance();

        float animation = 0;

        if (this.isOnGround()) {
            animation = 1.8f - (float)Math.abs(Math.sin(((float)(this.timer / 5f) % Opencraft.getFPS()) / Opencraft.getFPS() * Math.PI * 2.0f) * 0.1f);
            animation = animation * 100.0f / ((24f * 12f) + 32f);
            animation = (animation - 0.5333f) * 10;
        }

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureEngine.getTerrain());
//        GL11.glRotatef(rotation, 0, 1, 0);

        Block block = itemBlock.getBlock();
        if (block == null)
            return;

        Vector3f pos = this.getPosition();
        Vector2f size = this.getSize();

        t.begin();
        if (block.isTile()) {
            BlockRenderer.renderTile0(t, pos.x, pos.y + animation, pos.z, 0.3f, block);
            BlockRenderer.renderTile1(t, pos.x, pos.y + animation, pos.z, 0.3f, block);
        }
        else {
            BlockRenderer.renderBackSide(t, pos.x, pos.y + animation, pos.z, size.y / 2f, block);
            BlockRenderer.renderTopSide(t, pos.x, pos.y + animation, pos.z, size.y / 2f, block);
            BlockRenderer.renderLeftSide(t, pos.x, pos.y + animation, pos.z, size.y / 2f, block);
            BlockRenderer.renderRightSide(t, pos.x, pos.y + animation, pos.z, size.y / 2f, block);
            BlockRenderer.renderBottomSide(t, pos.x, pos.y + animation, pos.z, size.y / 2f, block);
            BlockRenderer.renderFrontSide(t, pos.x, pos.y + animation, pos.z, size.y / 2f, block);
        }
        t.end();

//        GL11.glRotatef(-rotation, 0, 1, 0);
        GL11.glEndList();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

        if (this.isOnGround()) {
            this.timer++;
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.picked) return;

        Vector3f vel = this.getVelocity();

        vel.y = (float)((double)vel.y - 0.04D);
        this.move(vel.x, vel.y, vel.z);
        vel.x *= 0.98F;
        vel.y *= 0.98F;
        vel.z *= 0.98F;
        if (this.isOnGround()) {
            vel.x *= 0.7F;
            vel.z *= 0.7F;
        }

        this.setVelocity(vel);
    }

    public void picked() {
        // todo: do it able to completely remove from the world
        this.destroy();
        this.picked = true;
    }

}
