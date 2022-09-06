package com.artingl.opencraft.World.Block;

import com.artingl.opencraft.Rendering.Texture;
import com.artingl.opencraft.Rendering.TextureEngine;

public class BlockGrassBlock extends Block
{

    public BlockGrassBlock()
    {
        super("opencraft:grass_block", false);

        float tx, ty, bx, by, sx, sy;
        tx = 0; ty = 0; bx = 0; by = 0; sx = 0; sy = 0;

        tx = TextureEngine.getBlockTextureX("opencraft:grass_top");
        ty = TextureEngine.getBlockTextureY("opencraft:grass_top");
        bx = TextureEngine.getBlockTextureX("opencraft:grass_bottom");
        by = TextureEngine.getBlockTextureY("opencraft:grass_bottom");
        sx = TextureEngine.getBlockTextureX("opencraft:grass_side");
        sy = TextureEngine.getBlockTextureY("opencraft:grass_side");
        int id = TextureEngine.getBlockTextureId("opencraft:grass_side");

        this.texture = new Texture(id, tx, ty, bx, by, sx, sy);

    }

}
