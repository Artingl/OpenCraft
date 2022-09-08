package com.artingl.opencraft.ModAPI.World.Block;

import com.artingl.opencraft.ModAPI.Texture.TexturesApi;
import com.artingl.opencraft.Rendering.Game.Texture;
import com.artingl.opencraft.World.Item.Tool;

public class BlockProperties {

    protected Texture texture;
    protected String id;
    protected int strength;
    protected Tool tool;

    protected BlockProperties(String id) {
        this.id = id;
        this.strength = 1;
        this.tool = Tool.IMMEDIATELY;
        this.texture = TexturesApi.getTexture("opencraft:none");
    }

    public static BlockProperties create(String id) {
        return new BlockProperties(id);
    }

    public BlockProperties setTexture(Texture texture) {
        this.texture = texture;
        return this;
    }

    public BlockProperties setStrength(int strength) {
        this.strength = strength;
        return this;
    }

    public BlockProperties setTool(Tool tool) {
        this.tool = tool;
        return this;
    }

}
