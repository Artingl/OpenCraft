package OpenCraft.World.Block;

import OpenCraft.Rendering.Texture;
import OpenCraft.Rendering.TextureEngine;

public class BlockLeavesOak extends Block
{

    public BlockLeavesOak(int idi)
    {
        super("opencraft:leaves_oak", idi);

    }

    @Override
    public boolean hasTranslucent() {
        return true;
    }

}
