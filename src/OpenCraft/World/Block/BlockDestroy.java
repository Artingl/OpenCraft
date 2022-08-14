package OpenCraft.World.Block;

import OpenCraft.Rendering.Texture;
import OpenCraft.Rendering.TextureEngine;

public class BlockDestroy extends Block
{

    public BlockDestroy(int stage)
    {
        super("opencraft:destroy_stage_" + stage, -1);
    }

}
