package com.artingl.opencraft.World.Block;

import com.artingl.opencraft.Control.Game.Texture;
import com.artingl.opencraft.Control.Game.TextureEngine;

public class BlockLogOak extends Block
{

    public BlockLogOak()
    {
        super("opencraft:log_oak", false);

        float tx, ty, bx, by, sx, sy;
        tx = 0; ty = 0; bx = 0; by = 0; sx = 0; sy = 0;

        tx = TextureEngine.getBlockTextureX("opencraft:log_oak_top");
        ty = TextureEngine.getBlockTextureY("opencraft:log_oak_top");
        bx = TextureEngine.getBlockTextureX("opencraft:log_oak_top");
        by = TextureEngine.getBlockTextureY("opencraft:log_oak_top");
        sx = TextureEngine.getBlockTextureX("opencraft:log_oak_side");
        sy = TextureEngine.getBlockTextureY("opencraft:log_oak_side");
        int id = TextureEngine.getBlockTextureId("opencraft:log_oak_side");

        this.texture = new Texture(id, tx, ty, bx, by, sx, sy);

    }

}
