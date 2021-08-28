package OpenCraft.Game.Gui.screens;

import OpenCraft.Game.Gui.Screen;
import OpenCraft.Game.Rendering.BlockRenderer;
import OpenCraft.Game.Rendering.TextureManager;
import OpenCraft.OpenCraft;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glVertex3f;

public class MainMenu extends Screen
{

    private final int panoramaId0;
    private final int panoramaId1;
    private final int panoramaId2;
    private final int panoramaId3;
    private final int panoramaId4;
    private final int panoramaId5;
    private final int logo;

    private float rotationX = 0;
    private float rotationY = 0;
    private boolean b_rotation;

    public MainMenu() throws IOException {
        super(OpenCraft.getWidth(), OpenCraft.getHeight());

        panoramaId0 = TextureManager.load(ImageIO.read(new File("resources/title/bg/panorama0.png")));
        panoramaId1 = TextureManager.load(ImageIO.read(new File("resources/title/bg/panorama1.png")));
        panoramaId2 = TextureManager.load(ImageIO.read(new File("resources/title/bg/panorama2.png")));
        panoramaId3 = TextureManager.load(ImageIO.read(new File("resources/title/bg/panorama3.png")));
        panoramaId4 = TextureManager.load(ImageIO.read(new File("resources/title/bg/panorama4.png")));
        panoramaId5 = TextureManager.load(ImageIO.read(new File("resources/title/bg/panorama5.png")));
        logo = TextureManager.load(ImageIO.read(new File("resources/title/logo.png")));
    }

    @Override
    public void render(int screenWidth, int screenHeight)
    {
        GL11.glMatrixMode(5889);
        GL11.glLoadIdentity();
        GLU.gluPerspective(90, (float)width / (float)height, 0.05F, 1000.0F);
        GL11.glMatrixMode(5888);
        GL11.glLoadIdentity();
        GL11.glEnable(3553);

        GL11.glPushMatrix();
        GL11.glRotatef(rotationY, 1, 0, 0);
        GL11.glRotatef(rotationX, 0, 1, 0);
        GL11.glTranslatef(0, 0, 0);
        GL11.glScalef(3, 3, 3);

        GL11.glBindTexture(3553, panoramaId4);
        GL11.glBegin(GL11.GL_QUADS);
        BlockRenderer.renderLegacyTop(0, 0, 0);
        GL11.glEnd();

        GL11.glBindTexture(3553, panoramaId5);
        GL11.glBegin(GL11.GL_QUADS);
        BlockRenderer.renderLegacyBottom(0, 0, 0);
        GL11.glEnd();

        GL11.glBindTexture(3553, panoramaId0);
        GL11.glBegin(GL11.GL_QUADS);
        BlockRenderer.renderLegacyFront(0, 0, 0);
        GL11.glEnd();

        GL11.glBindTexture(3553, panoramaId2);
        GL11.glBegin(GL11.GL_QUADS);
        BlockRenderer.renderLegacyBack(0, 0, 0);
        GL11.glEnd();

        GL11.glBindTexture(3553, panoramaId1);
        GL11.glBegin(GL11.GL_QUADS);
        BlockRenderer.renderLegacyLeft(0, 0, 0);
        GL11.glEnd();

        GL11.glBindTexture(3553, panoramaId3);
        GL11.glBegin(GL11.GL_QUADS);
        BlockRenderer.renderLegacyRight(0, 0, 0);
        GL11.glEnd();

        GL11.glPopMatrix();
        GL11.glDisable(3553);

        if (rotationY < 25)
        {
            b_rotation = false;
        }
        if (rotationY > 75)
        {
            b_rotation = true;
        }

        if (b_rotation)
        {
            rotationY -= OpenCraft.getTimer().a / 1000f;
        }
        else {
            rotationY += OpenCraft.getTimer().a / 1000f;
        }
        rotationX += OpenCraft.getTimer().a / 1000f;

        GL11.glMatrixMode(5889);
        GL11.glLoadIdentity();
        GL11.glOrtho(0.0D, (double)screenWidth, (double)screenHeight, 0.0D, 100.0D, 300.0D);
        GL11.glMatrixMode(5888);
        GL11.glLoadIdentity();
        GL11.glTranslatef(0.0F, 0.0F, -200.0F);

        fillTexture(0, 0, screenWidth, screenHeight, logo);

        //fill(0, 0, screenWidth, screenHeight, 0xFFFFFFFF);
        //drawCenteredString("Test", 10, 10, 0xFFFFFF);
    }

}
