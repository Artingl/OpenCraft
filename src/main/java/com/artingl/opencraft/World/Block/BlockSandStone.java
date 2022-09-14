package com.artingl.opencraft.World.Block;

import com.artingl.opencraft.Control.Game.Texture;
import com.artingl.opencraft.Control.Game.TextureEngine;

public class BlockSandStone extends Block
{

    public BlockSandStone()
    {
        super("opencraft:sandstone", false);

        float tx, ty, bx, by, sx, sy;
        tx = 0; ty = 0; bx = 0; by = 0; sx = 0; sy = 0;

        tx = TextureEngine.getBlockTextureX("opencraft:sandstone_top");
        ty = TextureEngine.getBlockTextureY("opencraft:sandstone_top");
        bx = TextureEngine.getBlockTextureX("opencraft:sandstone_bottom");
        by = TextureEngine.getBlockTextureY("opencraft:sandstone_bottom");
        sx = TextureEngine.getBlockTextureX("opencraft:sandstone_side");
        sy = TextureEngine.getBlockTextureY("opencraft:sandstone_side");
        int id = TextureEngine.getBlockTextureId("opencraft:sandstone_side");

        this.texture = new Texture(id, tx, ty, bx, by, sx, sy);

    }

}
