package com.artingl.opencraft.GUI.Elements;

import com.artingl.opencraft.GL.Controls;
import com.artingl.opencraft.GUI.Screens.Screen;
import com.artingl.opencraft.Logger.Logger;
import com.artingl.opencraft.Math.Vector2i;
import com.artingl.opencraft.Opencraft;
import com.artingl.opencraft.Rendering.TextureEngine;
import com.artingl.opencraft.Resources.Resources;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_BLEND;

public class Slider extends Element
{

    public static int[] SLIDER_TEXTURES = new int[]{
            TextureEngine.load("opencraft:gui/button.png"),
            TextureEngine.load("opencraft:gui/button_hover.png"),
            TextureEngine.load("opencraft:gui/button_disabled.png")
    };

    private String text;
    private int progress;
    private Consumer<Integer> onSlide;
    private int[] sliderTexture = {0, 0, 0, 0};

    private int min;
    private int max;
    private float actualWidth;

    public Slider(Screen screen, int id, float x, float y, int min, int max, String text, Consumer<Integer> onSlide)
    {
        super(id, screen, x, y, 200, 20);
        this.text = text;
        this.onSlide = onSlide;
        this.min = min;
        this.max = max;

        this.sliderTexture[0] = getCroppedTexture(0, 0, 0, 5, 20);
        this.sliderTexture[1] = getCroppedTexture(0, 195, 0, 5, 20);
        this.sliderTexture[2] = getCroppedTexture(1, 0, 0, 5, 20);
        this.sliderTexture[3] = getCroppedTexture(1, 195, 0, 5, 20);
    }

    @Override
    public void tick(int screenWidth, int screenHeight, int scale)
    {
        this.actualWidth = (200f * Math.min(scale / 100, 1));
        this.height = (20f * Math.min(scale / 100, 1));
    }

    @Override
    public void render(int screenWidth, int screenHeight, int scale)
    {
        super.render(screenWidth, screenHeight, scale);

        boolean mouseHover = selected || isMouseHover();

        GL11.glEnable(GL_BLEND);
        GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        GL11.glPushMatrix();
        GL11.glTranslatef(this.x, this.y, 50);
        fillTexture(0, 0, width, height, SLIDER_TEXTURES[2]);
        renderSlider(this.progress, mouseHover);
        Opencraft.getFont().drawShadow(text, (int)((this.width / 2f) - Opencraft.getFont().getTextWidth(text) / 2f), (int)(this.height / 2f) - 5, super.getTitleColor());
        GL11.glPopMatrix();
        GL11.glLoadIdentity();
        GL11.glTranslatef(0, 0,-200);

        GL11.glDisable(GL_BLEND);
    }

    public void renderSlider(int progress, boolean hover) {
        int x = (int) ((this.width / 100 * progress) - (10f / 100f * progress));

        GL11.glTranslatef(x, 0, 0);
        fillTexture(0, 0, 5, 20, sliderTexture[(!hover ? 0 : 2)]);
        fillTexture(5, 0, 10, 20, sliderTexture[(!hover ? 1 : 3)]);
        GL11.glTranslatef(-x, 0, 0);
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

        return Slider.SLIDER_TEXTURES[textureId];
    }

    @Override
    public void mouseHandler(Controls.MouseInput mouseInput) {
        super.mouseHandler(mouseInput);

        if (selected) {
            if (mouseInput.button == Controls.Buttons.BUTTON_LEFT) {
                this.progress = (int) (((int)(normalizeX(mouseInput.mousePosition.x) - this.x)) / this.width * 100);
                if (this.progress < 0) this.progress = 0;
                if (this.progress > 100) this.progress = 100;

                this.sendProgress();
            }

            if (mouseInput.state == Controls.MouseState.UP) {
                Opencraft.getSoundEngine().loadAndPlay("opencraft", "gui/click1");
            }
        }
    }

    @Override
    public void keyHandler(Controls.KeyInput keyInput) {
        super.keyHandler(keyInput);

        if (selected) {
            if (keyInput.keyCode == Controls.Keys.KEY_LEFT) {
                this.progress -= 5;
            }
            else
            if (keyInput.keyCode == Controls.Keys.KEY_RIGHT) {
                this.progress += 5;
            }

            if (this.progress < 0) this.progress = 0;
            if (this.progress > 100) this.progress = 100;

            this.sendProgress();
        }
    }

    public void sendProgress() {
        onSlide.accept((int) ((max - min) / 100f * this.progress) + min);
    }

    public void setProgress(int progress) {
        this.progress = (int) ((float)progress / (float)max * 100f);
        onSlide.accept(progress);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
