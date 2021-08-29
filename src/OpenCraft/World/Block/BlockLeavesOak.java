package OpenCraft.World.Block;

import OpenCraft.Game.Rendering.Texture;
import OpenCraft.Game.Rendering.TextureEngine;

public class BlockLeavesOak extends Block
{

    public BlockLeavesOak(int idi)
    {
        super("leaves_oak", idi);

        float tx, ty, bx, by, sx, sy;
        tx = 0; ty = 0; bx = 0; by = 0; sx = 0; sy = 0;

        tx = TextureEngine.getBlockTextureX(this.getId());
        ty = TextureEngine.getBlockTextureY(this.getId());
        bx = TextureEngine.getBlockTextureX(this.getId());
        by = TextureEngine.getBlockTextureY(this.getId());
        sx = TextureEngine.getBlockTextureX(this.getId());
        sy = TextureEngine.getBlockTextureY(this.getId());
        int id = TextureEngine.getBlockTextureId(this.getId());

        this.texture = new Texture(id, tx, ty, bx, by, sx, sy);

    }

    @Override
    public boolean hasTranslucent() {
        return true;
    }

}
