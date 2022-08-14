package OpenCraft.gui.screens;

import OpenCraft.Rendering.BlockRenderer;
import OpenCraft.Rendering.VerticesBuffer;
import OpenCraft.gui.Button;
import OpenCraft.gui.Screen;
import OpenCraft.Rendering.TextureEngine;
import OpenCraft.OpenCraft;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class MainMenuScreen extends Screen
{

    private final int[] panorama = {
            TextureEngine.load(ImageIO.read(new File("resources/title/bg/panorama0.png"))),
            TextureEngine.load(ImageIO.read(new File("resources/title/bg/panorama1.png"))),
            TextureEngine.load(ImageIO.read(new File("resources/title/bg/panorama2.png"))),
            TextureEngine.load(ImageIO.read(new File("resources/title/bg/panorama3.png"))),
            TextureEngine.load(ImageIO.read(new File("resources/title/bg/panorama4.png"))),
            TextureEngine.load(ImageIO.read(new File("resources/title/bg/panorama5.png")))
    };
    private final int logo;

    private float panoramaTimer = 0;

    public MainMenuScreen() throws IOException {
        super(OpenCraft.getWidth(), OpenCraft.getHeight(), "");

        logo = TextureEngine.load(ImageIO.read(new File("resources/title/logo.png")), GL11.GL_LINEAR);

        this.addElement(new Button(0, 0, 0, "Singleplayer", () -> OpenCraft.setCurrentScreen(OpenCraft.getWorldListScreen())));
        this.addElement(new Button(1, 0, 0, "Quit the game", OpenCraft::close));

    }

    public void init()
    {

    }

    public void render(int screenWidth, int screenHeight, int scale)
    {
        getElements().forEach((id, element) -> {
            if (element instanceof Button)
            {
                Button btn = (Button)element;
                btn.setX(screenWidth / 2f - btn.getWidth() / 2f);

                if (btn.getId() == 0)
                {
                    btn.setY(screenHeight / 2f - 15);
                }
                else if (btn.getId() == 1)
                {
                    btn.setY(screenHeight / 2f + 15);
                }
            }
        });


        this.drawPanorama(screenWidth, screenHeight);

        GL11.glLoadIdentity();
        GL11.glTranslatef(0.0F, 0.0F, -200.0F);

        float l_width = ((858f / 2) * Math.min(scale / 100, 1));
        float l_height = ((106f / 2) * Math.min(scale / 100, 1));

        GL11.glTranslatef(screenWidth / 2f - (l_width / 2), 20, -50);
        fillTexture(0, 0, l_width, l_height, logo);
        GL11.glTranslatef(-(screenWidth / 2f - (l_width / 2)), -20, 50);
//        drawBackground(VerticesBuffer.instance, screenWidth, screenHeight, 0x808080);

        super.render(screenWidth, screenHeight, scale);
    }
    public void drawPanorama(int screenWidth, int screenHeight) {
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GLU.gluPerspective(90, (float)width / (float)height, 0.05F, 1000.0F);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
        GL11.glEnable(GL11.GL_TEXTURE_2D);

        this.panoramaTimer += OpenCraft.getTimer().a / 2f;

        GL11.glPushMatrix();
        GL11.glRotated((Math.sin(this.panoramaTimer / 400.0F)) * 25.0F + 20.0F, 1.0F, 0.0F, 0.0F);
        GL11.glRotated(-(this.panoramaTimer) * 0.1F, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(0, 0, 0);
        GL11.glScalef(3, 3, 3);

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.panorama[4]);
        GL11.glBegin(GL11.GL_QUADS);
        BlockRenderer.renderLegacyTop(0, 0, 0, 1, 1, 1);
        GL11.glEnd();

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.panorama[5]);
        GL11.glBegin(GL11.GL_QUADS);
        BlockRenderer.renderLegacyBottom(0, 0, 0, 1, 1, 1);
        GL11.glEnd();

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.panorama[0]);
        GL11.glBegin(GL11.GL_QUADS);
        BlockRenderer.renderLegacyFront(0, 0, 0, 1, 1, 1);
        GL11.glEnd();

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.panorama[2]);
        GL11.glBegin(GL11.GL_QUADS);
        BlockRenderer.renderLegacyBack(0, 0, 0, 1, 1, 1);
        GL11.glEnd();

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.panorama[1]);
        GL11.glBegin(GL11.GL_QUADS);
        BlockRenderer.renderLegacyLeft(0, 0, 0, 1, 1, 1);
        GL11.glEnd();

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.panorama[3]);
        GL11.glBegin(GL11.GL_QUADS);
        BlockRenderer.renderLegacyRight(0, 0, 0, 1, 1, 1);
        GL11.glEnd();

        GL11.glPopMatrix();
        GL11.glDisable(GL11.GL_TEXTURE_2D);

        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0.0D, screenWidth, screenHeight, 0.0D, 100.0D, 300.0D);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
    }

}
