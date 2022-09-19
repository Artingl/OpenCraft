package com.artingl.opencraft.GUI.Screens;

import com.artingl.opencraft.GUI.Elements.Button;
import com.artingl.opencraft.GUI.Elements.Element;
import com.artingl.opencraft.GUI.ScreenRegistry;
import com.artingl.opencraft.Opencraft;
import com.artingl.opencraft.Control.Render.BlockRenderer;
import com.artingl.opencraft.Control.Render.Camera;
import com.artingl.opencraft.Control.Game.TextureEngine;
import com.artingl.opencraft.Resources.Lang.Lang;
import org.lwjgl.opengl.GL11;

public class MainMenuScreen extends Screen
{

    private final int[] panorama = {
            TextureEngine.load("opencraft:title/bg/panorama0.png"),
            TextureEngine.load("opencraft:title/bg/panorama1.png"),
            TextureEngine.load("opencraft:title/bg/panorama2.png"),
            TextureEngine.load("opencraft:title/bg/panorama3.png"),
            TextureEngine.load("opencraft:title/bg/panorama4.png"),
            TextureEngine.load("opencraft:title/bg/panorama5.png")
    };
    private final int logo;
    private float panoramaTimer = 0;

    private int singleplayerButton;
    private int multiplayerButton;
    private int modsListButton;
    private int settingsButton;
    private int quitButton;


    public MainMenuScreen() {
        super(Opencraft.getWidth(), Opencraft.getHeight(), "main_menu");

        this.showTittle = false;
        this.logo = TextureEngine.load("opencraft:title/logo.png", GL11.GL_LINEAR);
    }

    public void init() {
        super.init();

        singleplayerButton = this.addElement(new Button(this, 0, 0, Lang.getTranslatedString("opencraft:gui.text.singleplayer"), () -> Opencraft.setCurrentScreen(ScreenRegistry.worldList)));
        multiplayerButton = this.addElement(new Button(this, 0, 0, Lang.getTranslatedString("opencraft:gui.text.multiplayer"), () -> Opencraft.connectTo("95.165.132.6", 65000)));
        modsListButton = this.addElement(new Button(this, 0, 0, Lang.getTranslatedString("opencraft:gui.text.mods_list"), () -> Opencraft.setCurrentScreen(ScreenRegistry.modsList)));
        settingsButton = this.addElement(new Button(this, 0, 0, Lang.getTranslatedString("opencraft:gui.text.settings"), () -> Opencraft.setCurrentScreen(ScreenRegistry.settingsScreen)));
        quitButton = this.addElement(new Button(this, 0, 0, Lang.getTranslatedString("opencraft:gui.text.quit_game"), Opencraft::close));

        getElements().get(settingsButton).setWidth(90);
        getElements().get(quitButton).setWidth(90);
    }

    @Override
    public void updateElement(Element element, int screenWidth, int screenHeight, int scale) {
        super.updateElement(element, screenWidth, screenHeight, scale);
        if (element instanceof Button btn)
        {
            if (btn.getId() == singleplayerButton)
            {
                btn.setY(screenHeight / 2f - 55);
                btn.setX(screenWidth / 2f - btn.getWidth() / 2f);
            }
            else if (btn.getId() == multiplayerButton)
            {
                btn.setY(screenHeight / 2f - 50 + btn.getHeight());
                btn.setX(screenWidth / 2f - btn.getWidth() / 2f);
            }
            else if (btn.getId() == modsListButton)
            {
                btn.setY(screenHeight / 2f - 50 + btn.getHeight() + btn.getHeight() + 5);
                btn.setX(screenWidth / 2f - btn.getWidth() / 2f);
            }
            else if (btn.getId() == settingsButton)
            {
                btn.setY(screenHeight / 2f + 50);
                btn.setX(screenWidth / 2f - btn.getWidth() - 10);
            }
            else if (btn.getId() == quitButton)
            {
                btn.setY(screenHeight / 2f + 50);
                btn.setX(screenWidth / 2f + 10);
            }
        }
    }

    public void render(int screenWidth, int screenHeight, int scale)
    {
        this.drawPanorama(screenWidth, screenHeight);

        GL11.glLoadIdentity();
        GL11.glTranslatef(0.0F, 0.0F, -200.0F);

        float l_width = ((858f / 4) * Math.min(scale / 100, 1));
        float l_height = ((106f / 4) * Math.min(scale / 100, 1));

        super.render(screenWidth, screenHeight, scale);

        GL11.glTranslatef(screenWidth / 2f - (l_width / 2), 20, -50);
        fillTexture(0, 0, l_width, l_height, logo);
        GL11.glTranslatef(-(screenWidth / 2f - (l_width / 2)), -20, 50);
    }

    public void drawPanorama(int screenWidth, int screenHeight) {
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        Camera.perspective(90, (float)width / (float)height, 0.05F, 10.0F);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
        GL11.glEnable(GL11.GL_TEXTURE_2D);

        // todo: fix the animation
        this.panoramaTimer += (float)Math.abs(Math.sin(((float)(30 / 5f) % Opencraft.getFPS()) / Opencraft.getFPS() * Math.PI * 2.0f) * 0.1f) * 10;

        if (Double.isNaN(this.panoramaTimer))
        {
            this.panoramaTimer = 0;
        }

        GL11.glPushMatrix();
        GL11.glRotated((Math.sin(this.panoramaTimer / 400.0F)) * 25.0F + 20.0F, 1.0F, 0.0F, 0.0F);
        GL11.glRotated(-(this.panoramaTimer) * 0.1F, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(0, 0, 0);
        GL11.glScalef(3, 3, 3);

        Opencraft.getShaderProgram().bindTexture(this.panorama[4]);
        GL11.glBegin(GL11.GL_QUADS);
        BlockRenderer.renderLegacyTop(0, 0, 0, 1, 1, 1);
        GL11.glEnd();

        Opencraft.getShaderProgram().bindTexture(this.panorama[5]);
        GL11.glBegin(GL11.GL_QUADS);
        BlockRenderer.renderLegacyBottom(0, 0, 0, 1, 1, 1);
        GL11.glEnd();

        Opencraft.getShaderProgram().bindTexture(this.panorama[0]);
        GL11.glBegin(GL11.GL_QUADS);
        BlockRenderer.renderLegacyFront(0, 0, 0, 1, 1, 1);
        GL11.glEnd();

        Opencraft.getShaderProgram().bindTexture(this.panorama[2]);
        GL11.glBegin(GL11.GL_QUADS);
        BlockRenderer.renderLegacyBack(0, 0, 0, 1, 1, 1);
        GL11.glEnd();

        Opencraft.getShaderProgram().bindTexture(this.panorama[1]);
        GL11.glBegin(GL11.GL_QUADS);
        BlockRenderer.renderLegacyLeft(0, 0, 0, 1, 1, 1);
        GL11.glEnd();

        Opencraft.getShaderProgram().bindTexture(this.panorama[3]);
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
