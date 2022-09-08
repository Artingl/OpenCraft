package com.artingl.opencraft.GUI.Screens;

import com.artingl.opencraft.GL.Controls;
import com.artingl.opencraft.GUI.Elements.Element;
import com.artingl.opencraft.GUI.Elements.Slider;
import com.artingl.opencraft.GUI.Font.Font;
import com.artingl.opencraft.Opencraft;
import com.artingl.opencraft.Rendering.Game.TextureEngine;
import com.artingl.opencraft.Rendering.Game.VerticesBuffer;
import com.artingl.opencraft.Resources.Lang.Lang;
import com.artingl.opencraft.Resources.Options.OptionsListener;
import com.artingl.opencraft.Resources.Options.OptionsRegistry;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Objects;

import static org.lwjgl.opengl.GL11.*;

public class Screen implements OptionsListener
{
    private static int background_id = TextureEngine.load("opencraft:gui/dirt.png");

    private HashMap<Integer, Element> elements;

    protected float width;
    protected float height;
    protected String title;
    private int optionsListener = -1;
    private int keyboardEvent = -1;
    private int mouseEvent = -1;
    private int framesCounter;
    private long eventsTimeout;
    public final String screenId;
    private Element selectedElement;
    private boolean droppedFirstMouseEvent;
    protected boolean renderGameInBackground;
    protected boolean showTittle;

    public Screen(int width, int height, String screenId)
    {
        this.elements = new HashMap<>();

        this.showTittle = true;
        this.renderGameInBackground = false;
        this.width = width;
        this.height = height;
        this.title = Lang.getTranslatedString("opencraft:gui.screen." + screenId);
        this.screenId = screenId;
    }

    public void resize(int width, int height)
    {
        this.width = width;
        this.height = height;
    }

    public void init()
    {
        this.eventsTimeout = System.currentTimeMillis();
        this.framesCounter = 0;
        this.droppedFirstMouseEvent = !Controls.getMouseKey(0);
        this.elements = new HashMap<>();

        if (this.keyboardEvent == -1 || this.optionsListener == -1) {
            this.registerHandlers();
        }
    }

    protected void fill(int x0, int y0, int x1, int y1, int col) {
        float a = (float)(col >> 24 & 255) / 255.0F;
        float r = (float)(col >> 16 & 255) / 255.0F;
        float g = (float)(col >> 8 & 255) / 255.0F;
        float b = (float)(col & 255) / 255.0F;
        VerticesBuffer t = VerticesBuffer.getGlobalInstance();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glColor4f(r, g, b, a);
        t.begin();
        t.vertex((float)x0, (float)y1, 0.0F);
        t.vertex((float)x1, (float)y1, 0.0F);
        t.vertex((float)x1, (float)y0, 0.0F);
        t.vertex((float)x0, (float)y0, 0.0F);
        t.end();
        GL11.glDisable(3042);
    }

    public void setLoadingScreen(String s) {
        GL11.glTranslatef(0, 0, 50);
        drawBackground(VerticesBuffer.getGlobalInstance(), Opencraft.getScreenScaledWidth(), Opencraft.getScreenScaledHeight(), 0x808080);
        Opencraft.getFont().drawShadow(s, (Opencraft.getScreenScaledWidth() - Opencraft.getFont().getTextWidth(s)) / 2, (int) (Opencraft.getScreenScaledHeight() / 2f) - 5, 0xFFFFFF);
        Opencraft.getDisplay().swapBuffers();
    }

    protected void drawBackground(VerticesBuffer t, int screenWidth, int screenHeight, int clr)
    {
        GL11.glEnable(3553);
        GL11.glBindTexture(3553, background_id);
        t.begin();
        t.color(clr);
        float s = 32.0F;
        t.vertexUV(0.0F, (float)screenHeight, 0.0F, 0.0F, (float)screenHeight / s);
        t.vertexUV((float)screenWidth, (float)screenHeight, 0.0F, (float)screenWidth / s, (float)screenHeight / s);
        t.vertexUV((float)screenWidth, 0.0F, 0.0F, (float)screenWidth / s, 0.0F);
        t.vertexUV(0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        t.end();
        GL11.glEnable(3553);
    }

    protected void fillTexture(float x0, float y0, float x1, float y1, int texture) {
        GL11.glEnable(3553);
        GL11.glBindTexture(3553, texture);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glBegin(7);
        GL11.glTexCoord2f(1, 0);
        GL11.glVertex2f((float)x1, (float)y0);
        GL11.glTexCoord2f(0, 0);
        GL11.glVertex2f((float)x0, (float)y0);
        GL11.glTexCoord2f(0, 1);
        GL11.glVertex2f((float)x0, (float)y1);
        GL11.glTexCoord2f(1, 1);
        GL11.glVertex2f((float)x1, (float)y1);
        GL11.glEnd();
        GL11.glDisable(3042);
        GL11.glDisable(3553);
    }

    protected void drawCenteredString(String str, int x, int y, int color) {
        Font font = Opencraft.getFont();
        font.drawShadow(str, x - font.getTextWidth(str) / 2, y, color);
    }

    protected void keyPressed(Controls.KeyInput keyInput) {
        if (this.eventsTimeout + 100 > System.currentTimeMillis())
            return;

        elements.forEach((id, element) -> {
            if (element != null) element.keyHandler(keyInput);
        });

        if (keyInput.keyCode == Controls.Keys.KEY_TAB) {
            var values = new Object() {
                boolean nextHighlight = false;
                boolean skipEverything = false;
            };

            elements.forEach((id, element) -> {
                if (values.skipEverything)
                    return;

                if (element != null) {
                    if (values.nextHighlight) {
                        element.isHighlighting = true;
                        element.selected = true;
                        values.skipEverything = true;
                        return;
                    }

                    element.selected = false;
                    if (element.isHighlighting) {
                        values.nextHighlight = true;
                        element.isHighlighting = false;
                    }
                }
            });

            if (!values.skipEverything && elements.size() > 0) {
                elements.get(0).isHighlighting = true;
                elements.get(0).selected = true;
            }
        }
        else if (keyInput.keyCode == Controls.Keys.KEY_ENTER) {
            elements.forEach((id, element) -> {
                if (element.isHighlighting)
                {
                    element.clickHandler();
                }
            });
        }
    }

    protected void mouseHandler(Controls.MouseInput mouseInput) {
        if (this.eventsTimeout + 100 > System.currentTimeMillis())
            return;

        Element dropEvent = null;

        if (!droppedFirstMouseEvent)
            if (mouseInput.state != Controls.MouseState.UP)
                return;
            else {
                this.droppedFirstMouseEvent = true;
                return;
            }

        elements.forEach((id, element) -> {
            if (element != null) {
                if (selectedElement == null) {
                    element.isHighlighting = false;
                    element.selected = false;
                }
                else if (!selectedElement.equals(element)) {
                    element.isHighlighting = false;
                    element.selected = false;
                }
            }
        });

        if (mouseInput.state == Controls.MouseState.UP) {

            if (selectedElement != null) {
                if (selectedElement instanceof Slider) {
                    selectedElement.mouseHandler(mouseInput);
                    selectedElement.selected = false;
                    dropEvent = selectedElement;
                    selectedElement = null;
                }
                else if (!selectedElement.isMouseHover()) {
                    selectedElement.mouseHandler(mouseInput);
                    selectedElement.selected = false;
                    dropEvent = selectedElement;
                    selectedElement = null;
                }
            }
        }

        if (mouseInput.state == Controls.MouseState.DOWN) {
            if (selectedElement == null || !(selectedElement instanceof Slider)) {
                elements.forEach((id, element) -> {
                    if (selectedElement != null && selectedElement instanceof Slider)
                        return;

                    if (element.isMouseHover()) {
                        element.selected = true;
                        selectedElement = element;
                    }
                });
            }
        }

        Element finalDropEvent = dropEvent;
        elements.forEach((id, element) -> {
            if (element != null) {
                if (finalDropEvent != null) {
                    if (finalDropEvent.equals(element)) {
                        return;
                    }
                }

                element.mouseHandler(mouseInput);
            }
        });
    }

    public void registerHandlers() {
        this.keyboardEvent = Controls.registerKeyboardHandler(this, this::keyPressed);
        this.mouseEvent = Controls.registerMouseHandler(this, this::mouseHandler);
        this.optionsListener = Opencraft.registerOptionsListener(this);
    }

    protected int addElement(Element e)
    {
        int id = elements.size();
        elements.put(id, e);
        return id;
    }

    protected HashMap<Integer, Element> getElements()
    {
        return elements;
    }

    public void render(int screenWidth, int screenHeight, int scale)
    {
        if (this.showTittle) {
            Opencraft.getFont().drawShadow(title, (screenWidth - Opencraft.getFont().getTextWidth(title)) / 2, 20, 0xFFFFFF);
        }

        GL11.glEnable(GL_BLEND);
        GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        boolean doUpdateElements = framesCounter++ % 60 == 0;

        this.elements.forEach((id, element) -> {
            if (element != null) {
                if (doUpdateElements) {
                    this.updateElement(element, screenWidth, screenHeight, scale);
                    element.tick(screenWidth, screenHeight, scale);
                }

                element.render(screenWidth, screenHeight, scale);
            }
        });

        GL11.glDisable(GL_BLEND);
    }

    public void destroy() {
        Controls.unregisterKeyboardHandler(this.keyboardEvent);
        Controls.unregisterMouseHandler(this.mouseEvent);
        Opencraft.unregisterOptionsListener(this.optionsListener);
        this.keyboardEvent = -1;
        this.mouseEvent = -1;
        this.optionsListener = -1;
        elements.forEach((id, element) -> {
            if (element != null) {
                element.isHighlighting = false;
                element.selected = false;
                element.destroy();
            }
        });
    }

    public void updateElement(Element element, int screenWidth, int screenHeight, int scale) {
    }

    @Override
    public boolean equals(Object obj) {
        return Objects.equals(((Screen) obj).screenId, this.screenId);
    }

    public boolean doGameRenderingInBackground() {
        return renderGameInBackground;
    }

    public void updateDisplay() {
        for(int i = 0; i < 2; i++) {
            if (Opencraft.getDisplay().isResized())
            {
                GL11.glViewport(0, 0, Opencraft.getDisplay().getWidth(), Opencraft.getDisplay().getHeight());
                resize(Opencraft.getDisplay().getWidth(), Opencraft.getDisplay().getHeight());
            }

            Opencraft.getDisplay().swapBuffers();
            GL11.glFlush();

            Opencraft.getDisplay().isClosed();
            Opencraft.getDisplay().createFrame();
            GL11.glClearColor(1, 1, 1, 1);
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

            int width = Opencraft.getWidth();
            int height = Opencraft.getHeight();
            int screenWidth = width * Opencraft.getGuiScaleValue() / height;
            int screenHeight = height * Opencraft.getGuiScaleValue() / height;

            GL11.glClear(256);
            GL11.glMatrixMode(5889);
            GL11.glLoadIdentity();
            GL11.glOrtho(0.0D, (double)screenWidth, (double)screenHeight, 0.0D, 100.0D, 300.0D);
            GL11.glMatrixMode(5888);
            GL11.glLoadIdentity();
            GL11.glTranslatef(0.0F, 0.0F, -200.0F);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            glEnable(GL_BLEND);

            this.render(screenWidth, screenHeight, Opencraft.getGuiScaleValue());

            GL11.glDisable(3042);
            GL11.glDisable(2896);
            GL11.glDisable(3553);
            GL11.glDisable(2912);
        }
    }

    @Override
    public void optionUpdated(OptionsRegistry.Option newValue) {
    }

    public void forceElementsToUpdate() {
        int width = Opencraft.getWidth();
        int height = Opencraft.getHeight();
        int screenWidth = width * Opencraft.getGuiScaleValue() / height;
        int screenHeight = height * Opencraft.getGuiScaleValue() / height;

        elements.forEach((id, element) -> {
            if (element != null) {
                this.updateElement(element, screenWidth, screenHeight, Opencraft.getGuiScaleValue());
                element.tick(screenWidth, screenHeight, Opencraft.getGuiScaleValue());
            }
        });
    }
}
