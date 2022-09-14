package com.artingl.opencraft.ModAPI.Texture;

import com.artingl.opencraft.Control.Game.Texture;
import com.artingl.opencraft.Control.Game.TextureEngine;

public class TexturesApi {

    public static Texture getTexture(String id) {
        float tx, ty, bx, by, sx, sy;

        tx = TextureEngine.getBlockTextureX(id);
        ty = TextureEngine.getBlockTextureY(id);
        bx = TextureEngine.getBlockTextureX(id);
        by = TextureEngine.getBlockTextureY(id);
        sx = TextureEngine.getBlockTextureX(id);
        sy = TextureEngine.getBlockTextureY(id);
        return new Texture(TextureEngine.getBlockTextureId(id), tx, ty, bx, by, sx, sy);
    }

}
