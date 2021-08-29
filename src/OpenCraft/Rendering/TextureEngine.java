package OpenCraft.Rendering;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;

public class TextureEngine
{

    private static HashMap<String /* file name */ , Float[] /* texture position */> blocksTextures; // Blocks textures
    private static HashMap<String /* file name */ , Integer /* texture position */> blocksTexturesID; // Blocks textures id
    private static BufferedImage terrainImage; // Terrain image
    private static int terrainId; // Terrain texture id

    public static final int maxCountOfBlocks = 64; // Max count of blocks
    public static float addTextCoord = 1.0f / maxCountOfBlocks; // Normalized texcoord of 1 texture

    public static void init() throws IOException {
        File folder = new File("resources/textures/blocks/");
        File[] listOfFiles = folder.listFiles();

        blocksTextures = new HashMap<>();
        blocksTexturesID = new HashMap<>();
        terrainImage = new BufferedImage(16 * maxCountOfBlocks, 16 * maxCountOfBlocks, BufferedImage.TYPE_INT_ARGB);

        int x = 0;
        int y = 0;

        for (int i = 0; i < listOfFiles.length; i++) {
            String name = listOfFiles[i].getName();
            if (listOfFiles[i].isFile() && name.endsWith(".png")) {
                BufferedImage img = ImageIO.read(new File("resources/textures/blocks/" + name));
                img = cropImage(img, new Rectangle(16, 16));
                terrainImage.getGraphics().drawImage(img, x, y, null);

                float tx = (x / 16f) * addTextCoord;
                float ty = (y / 16f) * addTextCoord;
                TextureEngine.blocksTextures.put(name.split("." + "png")[0], new Float[]{tx, ty});
                TextureEngine.blocksTexturesID.put(name.split("." + "png")[0], i);

                x += 16;
                if (x > 16 * maxCountOfBlocks)
                {
                    x = 0;
                    y += 16;
                }

            }
        }
        terrainId = TextureEngine.load();
    }

    private static BufferedImage cropImage(BufferedImage src, Rectangle rect) {
        BufferedImage dest = src.getSubimage(0, 0, rect.width, rect.height);
        return dest;
    }

    public static int load()
    {
        return load(terrainImage);
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
