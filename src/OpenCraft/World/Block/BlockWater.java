package OpenCraft.World.Block;

import OpenCraft.Game.Rendering.Texture;
import OpenCraft.Game.Rendering.TextureManager;
import OpenCraft.OpenCraft;
import OpenCraft.World.Level;

public class BlockWater extends Block
{

    public BlockWater(int idi)
    {
        super("water", idi);

        float tx, ty, bx, by, sx, sy;
        tx = 0; ty = 0; bx = 0; by = 0; sx = 0; sy = 0;

        tx = TextureManager.getBlockTextureX(this.getId());
        ty = TextureManager.getBlockTextureY(this.getId());
        bx = TextureManager.getBlockTextureX(this.getId());
        by = TextureManager.getBlockTextureY(this.getId());
        sx = TextureManager.getBlockTextureX(this.getId());
        sy = TextureManager.getBlockTextureY(this.getId());
        int id = TextureManager.getBlockTextureId(this.getId());

        this.texture = new Texture(id, tx, ty, bx, by, sx, sy);

    }

    @Override
    public boolean isLiquid()
    {
        return true;
    }

    @Override
    public boolean hasTranslucent() {
        return true;
    }

}
