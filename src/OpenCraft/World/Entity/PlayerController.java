package OpenCraft.World.Entity;

import OpenCraft.Controls;
import OpenCraft.Rendering.TextureEngine;
import OpenCraft.World.Entity.Models.PlayerModel;
import OpenCraft.World.Level.Level;
import OpenCraft.World.RayCast;
import OpenCraft.gui.windows.PlayerInventory;
import OpenCraft.OpenCraft;
import OpenCraft.World.Block.Block;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class PlayerController extends Entity
{

    public static int TEXTURE;
    public static PlayerModel model = new PlayerModel();

    static {
        try {
            TEXTURE = TextureEngine.load(ImageIO.read(new File("resources/entity/steve.png")));
        } catch (IOException e) { }
    }

    private Block[] inventory;
    private PlayerInventory playerInventory;

    private HashMap<Integer, Boolean> clickedMouse;
    private HashMap<String, Boolean> clickedKeyboard;

    private boolean isFlying;

    public PlayerController(float x, float y, float z) {
        super(x, y, z);
        setModel(model);

        this.heightOffset = 1.62F;
        this.playerInventory = new PlayerInventory();
        this.clickedMouse = new HashMap<>();
        this.clickedKeyboard = new HashMap<>();
        this.inventory = new Block[36];

        inventory[0] = Block.dirt;
        inventory[1] = Block.log_oak;
        inventory[2] = Block.stone;
        inventory[3] = Block.grass_block;
        inventory[4] = Block.leaves_oak;
        inventory[5] = Block.glass;

        for (int i = 0; i < 20; i++) clickedMouse.put(i, false);
        for(int i = 32; i <= 126; i++) clickedKeyboard.put(String.valueOf((char)i).toLowerCase(), false);
        for(int i = 32; i <= 126; i++) clickedKeyboard.put(String.valueOf((char)i).toUpperCase(), false);
    }

    public void rotate()
    {
        super.setRy((float)((double)super.getRy() + (double)((float) Mouse.getDX()) * 0.15D));
        super.setRx((float)((double)super.getRx() - (double)((float)Mouse.getDY()) * 0.15D));

        if (super.getRx() > 90) super.setRx(90);
        if (super.getRx() < -90) super.setRx(-90);
    }

    @Override
    public void tick()
    {
        super.tick();

        float w = 0.3F;
        float h = 0.9F;

        float xa = 0.0F;
        float ya = 0.0F;
        boolean inWater = inWater();

        if (Keyboard.isKeyDown(Keyboard.KEY_W)) --ya;
        if (Keyboard.isKeyDown(Keyboard.KEY_S)) ++ya;
        if (Keyboard.isKeyDown(Keyboard.KEY_A)) --xa;
        if (Keyboard.isKeyDown(Keyboard.KEY_D)) ++xa;

        if (isFlying)
        {
            if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
                this.yd = 0.42F;
            }
            else if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                this.yd = -0.42F;
            }
            else this.yd = 0;
        }
        else {
            if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
                if (inWater) {
                    this.yd += 0.04F;
                } else if (this.onGround) {
                    this.yd = 0.42F;
                }
            }
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_F) && !clickedKeyboard.get("f")) {
            setFlying(!isFlying);
            if (isFlying)
                this.yd = 0.42f;
            clickedKeyboard.put("f", true);
        }
        else if(!Keyboard.isKeyDown(Keyboard.KEY_F))
        {
            clickedKeyboard.put("f", false);
        }

        float yo;
        float acceleration = 0.0f;
        if (inWater) {
            yo = this.y;
            this.moveRelative(xa, ya, 0.02F);
            this.move(this.xd, this.yd, this.zd);
            this.xd *= 0.8F + acceleration;
            this.yd *= 0.8F;
            this.zd *= 0.8F + acceleration;
            this.yd = (float) ((double) this.yd - 0.02D);
            if (this.horizontalCollision && this.isFree(this.xd, this.yd + 0.6F - this.y + yo, this.zd)) {
                this.yd = 0.3F;
            }
        } else if (isFlying)  {
            this.moveRelative(xa, ya, 0.1F);
            this.move(this.xd, this.yd, this.zd);
            this.xd *= 0.91F + acceleration;
            this.yd *= 0.98F + acceleration;
            this.zd *= 0.91F + acceleration;
            this.yd = (float)((double)this.yd - 0.08D);

            if (this.onGround) {
                setFlying(false);
            }
        } else {
            this.moveRelative(xa, ya, this.onGround ? 0.1F : 0.02F);
            this.move(this.xd, this.yd, this.zd);
            this.xd *= 0.91F + acceleration;
            this.yd *= 0.98F;
            this.zd *= 0.91F + acceleration;
            this.yd = (float)((double)this.yd - 0.08D);
            if (this.onGround) {
                this.xd *= 0.6F;
                this.zd *= 0.6F;
            }
        }

        float[][] ray = RayCast.rayCastToBlock(6, rx, ry, x, y, z);
        if (ray[0][0] == 1)
        {
            if (Controls.getMouseKey(0) && !clickedMouse.get(0))
            {
                OpenCraft.getLevel().getBlock((int)ray[1][0], (int)ray[1][1], (int)ray[1][2]).destroy((int)ray[1][0], (int)ray[1][1], (int)ray[1][2]);
                OpenCraft.getLevel().removeBlock((int)ray[1][0], (int)ray[1][1], (int)ray[1][2]);
                clickedMouse.put(0, true);
            }
            else if(!Controls.getMouseKey(0))
            {
                clickedMouse.put(0, false);
            }

            if (Controls.getMouseKey(1) && !clickedMouse.get(1) && !aabb.intersects(Block.getAABB((int)ray[2][0], (int)ray[2][1], (int)ray[2][2])) && getInventoryBlock(playerInventory.selected) != null)
            {
                OpenCraft.getLevel().setBlock((int)ray[2][0], (int)ray[2][1], (int)ray[2][2], getInventoryBlock(playerInventory.selected));
                clickedMouse.put(1, true);
            }
            else if(!Controls.getMouseKey(1))
            {
                clickedMouse.put(1, false);
            }
        }

        if (checkClickedKey("1")) playerInventory.selected = 0;
        if (checkClickedKey("2")) playerInventory.selected = 1;
        if (checkClickedKey("3")) playerInventory.selected = 2;
        if (checkClickedKey("4")) playerInventory.selected = 3;
        if (checkClickedKey("5")) playerInventory.selected = 4;
        if (checkClickedKey("6")) playerInventory.selected = 5;
        if (checkClickedKey("7")) playerInventory.selected = 6;
        if (checkClickedKey("8")) playerInventory.selected = 7;
        if (checkClickedKey("9")) playerInventory.selected = 8;


        // todo: remove it in the future (is used to be able to change current dimension)
        if (Keyboard.isKeyDown(Keyboard.KEY_C) && !clickedKeyboard.get("c")) {
            OpenCraft.switchWorld(OpenCraft.getLevelType() == Level.LevelType.OVERWORLD ? Level.LevelType.NETHER : Level.LevelType.OVERWORLD);
            clickedKeyboard.put("c", true);
        }
        else if(!Keyboard.isKeyDown(Keyboard.KEY_F))
        {
            clickedKeyboard.put("c", false);
        }
    }

    public void setFlying(boolean i)
    {
        isFlying = i;
    }

    public boolean isFlying() {
        return isFlying;
    }

    private boolean checkClickedKey(String key)
    {
        if (!clickedKeyboard.containsKey(key) || (Keyboard.isKeyDown(Keyboard.getKeyIndex(key)) && !clickedKeyboard.get(key)))
        {
            clickedKeyboard.put(key, true);
            return true;
        }
        else if (!Keyboard.isKeyDown(Keyboard.getKeyIndex(key)))
            clickedKeyboard.put(key, false);
        return false;
    }

    public Block getInventoryBlock(int i)
    {
        if (i < 0 || i >= 38) return null;
        return inventory[i];
    }

    public Block getCurrentBlock()
    {
        return inventory[playerInventory.selected];
    }

    @Override
    public void render() {
        float a = OpenCraft.getTimer().a;
        GL11.glEnable(3553);
        GL11.glBindTexture(3553, TEXTURE);
        GL11.glPushMatrix();
        float size = 0.058333334F;
        float yy = (float)(-Math.abs(Math.sin(0.6662D)) * 5.0D - 23.0D);
        GL11.glTranslatef(this.x, this.y - 2, this.z);
        GL11.glScalef(1.0F, -1.0F, 1.0F);
        GL11.glScalef(size, size, size);
        GL11.glTranslatef(0.0F, yy, 0.0F);
        GL11.glRotatef(360 - this.ry, 0.0F, 1.0F, 0.0F);
        super.model.render((float)0);
        GL11.glPopMatrix();
        GL11.glDisable(3553);
    }

    public void destroy() {
        super.destroy();

        this.clickedMouse.clear();
        this.clickedKeyboard.clear();

        this.playerInventory = null;
        this.inventory = null;
        this.clickedKeyboard = null;
        this.clickedMouse = null;
    }
}
