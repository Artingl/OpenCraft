package com.artingl.opencraft.Control.Game;

import com.artingl.opencraft.Logger.Logger;
import com.artingl.opencraft.Math.Vector2i;
import com.artingl.opencraft.Opencraft;
import com.artingl.opencraft.Resources.Resources;
import com.artingl.opencraft.Utils.Utils;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.HashMap;

public class TextureEngine
{

    private static HashMap<String, Float[]> blocksTextures; // Blocks textures
    private static HashMap<String, Integer> blocksTexturesID; // Blocks textures id
    private static BufferedImage atlasImage; // Terrain image
    private static int terrainId; // Terrain texture id
    public static final int maxCountOfBlocks = 128; // Max count of blocks

    protected static class texValues {
        public static int x = 0;
        public static int y = 0;
        public static int i = 0;
    };

    public static void init() {
        blocksTextures = new HashMap<>();
        blocksTexturesID = new HashMap<>();
        atlasImage =  new BufferedImage(16 * maxCountOfBlocks, 16 * maxCountOfBlocks, BufferedImage.TYPE_INT_ARGB);
    }

    public static void loadTextures(Class<?> callingClass, String id) throws URISyntaxException {
        Logger.debug("Loading " + id + " textures");
        String resourcesPath = Resources.convertToPath(id + ":textures/blocks/");

        Utils.foreachFiles(callingClass, resourcesPath, (path, name) -> {
            if (path.contains(resourcesPath) && path.endsWith(".png")) {
                try {
                    Logger.debug("Loading " + id + ":textures/blocks/" + name + " texture");

                    Opencraft.drawLoadingScreen(Utils.capitalizeString(id) + " Textures: Loading " + Utils.removeFileExtension(name));

                    BufferedImage img = ImageIO.read(Resources.load(callingClass, id + ":textures/blocks/" + name));
                    img = cropImage(img, new Vector2i(0, 0), new Vector2i(16, 16));
                    atlasImage.getGraphics().drawImage(img, texValues.x, texValues.y, null);

                    float tx = (texValues.x / 16f) * getTextureAtlasSize();
                    float ty = (texValues.y / 16f) * getTextureAtlasSize();
                    TextureEngine.blocksTextures.put(id + ":" + name.split("." + "png")[0], new Float[]{tx, ty});
                    TextureEngine.blocksTexturesID.put(id + ":" + name.split("." + "png")[0], texValues.i++);

                    texValues.x += 16;
                    if (texValues.x > 16 * maxCountOfBlocks) {
                        texValues.x = 0;
                        texValues.y += 16;
                    }
                } catch (Exception e) {
                    Logger.exception("Error occurred while loading " + id + ":textures/blocks/" + name + " file", e);
                }
            }
        });

        terrainId = TextureEngine.load();
    }

    public static float getTextureAtlasSize() {
        return 1.0f / maxCountOfBlocks;
    }

    public static BufferedImage cropImage(BufferedImage src, Vector2i pos, Vector2i rect) {
        BufferedImage dest = src.getSubimage(pos.x, pos.y, rect.x, rect.y);
        return dest;
    }

    public static int load()
    {
        return load(atlasImage);
    }

    public static int load(String path, int mode) {
        try {
            return load(ImageIO.read(Resources.load(Opencraft.class, path)), mode);
        } catch (Exception e) {}

        return terrainId;
    }

    public static int load(String path) {
        try {
            return load(ImageIO.read(Resources.load(Opencraft.class, path)));
        } catch (Exception e) {}

        return terrainId;
    }

    public static int load(BufferedImage image)
    {
        return load(image, GL11.GL_NEAREST);
    }

    public static int load(BufferedImage image, int mode)
    {
        int BYTES_PER_PIXEL = 4;

        int[] pixels = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
        ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * BYTES_PER_PIXEL); //4 for RGBA, 3 for RGB
        for(int y = 0; y < image.getHeight(); y++){
            for(int x = 0; x < image.getWidth(); x++){
                int pixel = pixels[y * image.getWidth() + x];
                buffer.put((byte) ((pixel >> 16) & 0xFF));    // Red component
                buffer.put((byte) ((pixel >> 8) & 0xFF));     // Green component
                buffer.put((byte) (pixel & 0xFF));            // Blue component
                buffer.put((byte) ((pixel >> 24) & 0xFF));    // Alpha component. Only for RGBA
            }
        }
        buffer.flip();
        int textureID = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glTexParameteri(3553, 10241, mode);
        GL11.glTexParameteri(3553, 10240, mode);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
        return textureID;
    }

    public static float getBlockTextureX(String name)
    {
        if (!TextureEngine.blocksTextures.containsKey(name)) return 0;
        return blocksTextures.get(name)[0];
    }

    public static float getBlockTextureY(String name)
    {
        if (!TextureEngine.blocksTextures.containsKey(name)) return 0;
        return blocksTextures.get(name)[1];
    }

    public static int getTerrain() {
        return terrainId;
    }

    public static int getBlockTextureId(String name) {
        if (!TextureEngine.blocksTexturesID.containsKey(name)) return 0;
        return blocksTexturesID.get(name);
    }
}
