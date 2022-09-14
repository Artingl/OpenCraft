package com.artingl.opencraft.GUI.Elements;

import com.artingl.opencraft.Control.Game.Input;
import com.artingl.opencraft.GUI.Screens.Screen;
import com.artingl.opencraft.Logger.Logger;
import com.artingl.opencraft.Math.Vector2i;
import com.artingl.opencraft.Control.Game.TextureEngine;
import com.artingl.opencraft.Resources.Resources;
import com.artingl.opencraft.Opencraft;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import static org.lwjgl.opengl.GL11.*;

public class Button extends Element
{

    public static int[] BUTTON_TEXTURES = new int[]{
            TextureEngine.load("opencraft:gui/button.png"),
            TextureEngine.load("opencraft:gui/button_hover.png"),
            TextureEngine.load("opencraft:gui/button_disabled.png")
    };

    private boolean shouldUpdateTextures;
    public boolean enabled = true;
    private String text;
    private final Runnable onClick;
    private float actualWidth;

    private int[][] croppedImages;

    public Button(Screen screen, float x, float y, String text, Runnable onClick)
    {
        super(screen, x, y, 200, 20);
        this.text = text;
        this.onClick = onClick;

        this.shouldUpdateTextures = true;
        this.croppedImages = new int[3][3];
        this.updateTextures();
    }

    @Override
    public void tick(int screenWidth, int screenHeight, int scale)
    {
        this.actualWidth = (200f * Math.min(scale / 100, 1));
        this.height = 20f * Math.min(scale / 100, 1);
    }

    @Override
    public void render(int screenWidth, int screenHeight, int scale)
    {
        if (this.shouldUpdateTextures) {
            this.shouldUpdateTextures = false;
            this.updateTextures();
        }

        super.render(screenWidth, screenHeight, scale);
        int id = !isMouseHover() && enabled ? 0 : !enabled ? 2 : 1;

        GL11.glEnable(GL_BLEND);
        GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        GL11.glPushMatrix();
        GL11.glTranslatef(this.x, this.y, 50);
        fillTexture(0, 0, 5, height, croppedImages[id][0]);
        fillTexture(5, 0, width-5, height, croppedImages[id][1]);
        fillTexture(width-5, 0, width, height, croppedImages[id][2]);
        Opencraft.getFont().drawShadow(text, (int)((this.width / 2f) - Opencraft.getFont().getTextWidth(text) / 2f), (int)(this.height / 2f) - 5, super.getTitleColor());
        GL11.glPopMatrix();
        GL11.glLoadIdentity();
        GL11.glTranslatef(0, 0,-200);

        GL11.glDisable(GL_BLEND);

    }

    public int getCroppedTexture(int textureId, int x, int y, int w, int h) {
        String [] BUTTON_TEXTURES = new String[]{
                "opencraft:gui/button.png",
                "opencraft:gui/button_hover.png",
                "opencraft:gui/button_disabled.png"
        };

        try {
            BufferedImage bufi = ImageIO.read(Resources.load(Opencraft.class, BUTTON_TEXTURES[textureId]));
            return TextureEngine.load(TextureEngine.cropImage(bufi, new Vector2i(x, y), new Vector2i(w, h)));
        } catch (Exception e) {
            Logger.exception("Error while cropping button texture", e);
        }

        return Button.BUTTON_TEXTURES[textureId];
    }

    @Override
    public void mouseHandler(Input.MouseInput mouseInput) {
        super.mouseHandler(mouseInput);

        if (mouseInput.state == Input.MouseState.UP) {
            if (mouseInput.button == Input.Buttons.BUTTON_LEFT && enabled) {
                if (isMouseHover())
                    clickHandler();
            }
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        this.croppedImages = null;
    }

    public void clickHandler() {
        Opencraft.getSoundEngine().loadAndPlay("opencraft", "gui/click1");
        if (onClick != null && enabled)
            onClick.run();
        selected = false;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void updateTextures() {
        for (int i = 0; i < 3; i++) {
            float w = (2 * (this.width / this.actualWidth * 100));
            this.croppedImages[i] = new int[]{
                    getCroppedTexture(i, 0, 0, 5, 20),
                    getCroppedTexture(i, 5, 0, (int) (w > 190 ? 190 : w - 10), 20),
                    getCroppedTexture(i, 195, 0, 5, 20)
            };
        }
    }

    @Override
    public void setHeight(int h) {
        super.setHeight(h);
        this.shouldUpdateTextures = true;
    }

    @Override
    public void setWidth(int w) {
        super.setWidth(w);
        this.shouldUpdateTextures = true;
    }
}
