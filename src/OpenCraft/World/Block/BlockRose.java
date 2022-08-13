package OpenCraft.World.Block;

import OpenCraft.Rendering.Texture;
import OpenCraft.Rendering.TextureEngine;
import OpenCraft.World.Direction;
import OpenCraft.World.Item.Tool;
import OpenCraft.World.Level.Level;
import OpenCraft.math.Vector3i;

public class BlockRose extends Block
{

    public BlockRose(int idi)
    {
        super("rose", idi);

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
        this.setStrength(1);
        this.setDefaultTool(Tool.IMMEDIATELY);

        super.makeRandomization();
    }

    public boolean isTile()
    {
        return true;
    }

    public void neighborChanged(Level level, Vector3i blockPos, Direction.Values direction, Block newBlock) {
        super.neighborChanged(level, blockPos, direction, newBlock);

        if (newBlock.equals(Block.air) && direction.equals(Direction.Values.UP)) {
            this.destroy(blockPos);
            this.createDrop(blockPos);
            level.removeBlock(blockPos);
        }
    }

}
