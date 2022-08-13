package OpenCraft.World.Entity;

import OpenCraft.World.Entity.Models.Model;
import OpenCraft.World.Item.Item;
import OpenCraft.phys.AABB;
import OpenCraft.Interfaces.ITick;
import OpenCraft.OpenCraft;
import OpenCraft.World.Level.Level;

import java.util.List;

public class Entity implements ITick
{
    public static int ENTITIES_ID = 0;


    /* Collision and physics */
    public float xd;
    public float yd;
    public float zd;
    public AABB aabb;
    public boolean onGround = true;
    public boolean horizontalCollision = false;
    public boolean removed = false;

    private Level level;

    /* Entity position */
    protected float width = 0.6F;
    protected float height = 1.8F;
    protected float heightOffset = 0.0F;
    protected float x; // X coordinate
    protected float y; // Y coordinate
    protected float z; // Z coordinate
    protected float xo; // X coordinate
    protected float yo; // Y coordinate
    protected float zo; // Z coordinate

    /* Model */
    protected Model model;

    protected float spawnPointX = 0;
    protected float spawnPointY = 0;
    protected float spawnPointZ = 0;

    /* Entity rotation */
    protected float rx; // X rotation
    protected float ry; // Y rotation
    private int hearts;
    private int maxHearts;
    private float fallValue;
    private boolean dead;
    private int entityId;
    private int tickEvent;

    public Entity(Level level)
    {
        this.entityId = ENTITIES_ID++;
        this.level = level;
        reset(0, 0, 0);
    }

    public Entity()
    {
        this.entityId = ENTITIES_ID++;
        this.level = OpenCraft.getLevel();
        reset(0, 0, 0);
    }

    public Entity(float x, float y, float z)
    {
        this.entityId = ENTITIES_ID++;
        this.level = OpenCraft.getLevel();
        reset(x, y, z);
    }

    public void reset(float x, float y, float z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.rx = 0;
        this.ry = 0;
        float w = 0.3F;
        float h = 0.9F;
        this.fallValue = 0;
        this.aabb = new AABB(x - w, y - h, z - w, x + w, y + h, z + w);
        this.hearts = 20;
        this.maxHearts = 20;
        this.dead = false;
        this.tickEvent = OpenCraft.registerTickEvent(this);
    }

    public void setModel(Model model)
    {
        this.model = model;
    }

    public Model getModel(Model model)
    {
        return model;
    }

    protected void remove()
    {
        removed = true;
    }

    protected void setSize(float w, float h) {
        this.width = w;
        this.height = h;
    }

    public void render() { }

    public void move(float xa, float ya, float za) {
        float xaOrg = xa;
        float yaOrg = ya;
        float zaOrg = za;
        List<AABB> aABBs = OpenCraft.getLevel().getCubes(this.aabb.expand(xa, ya, za));

        int i;
        for(i = 0; i < aABBs.size(); ++i) {
            ya = ((AABB)aABBs.get(i)).clipYCollide(this.aabb, ya);
        }

        this.aabb.move(0.0F, ya, 0.0F);

        for(i = 0; i < aABBs.size(); ++i) {
            xa = ((AABB)aABBs.get(i)).clipXCollide(this.aabb, xa);
        }

        this.aabb.move(xa, 0.0F, 0.0F);

        for(i = 0; i < aABBs.size(); ++i) {
            za = ((AABB)aABBs.get(i)).clipZCollide(this.aabb, za);
        }

        if (!this.onGround && (yaOrg != ya && yaOrg < 0.0F)) {
            this.fallValue = Math.round(this.fallValue);
            this.hitHandler(this.fallValue);
            this.fallValue = 0;
        }
        else {
            this.fallValue -= ya;
        }

        this.aabb.move(0.0F, 0.0F, za);
        this.horizontalCollision = xaOrg != xa || zaOrg != za;
        this.onGround = yaOrg != ya && yaOrg < 0.0F;

        if (xaOrg != xa) {
            this.xd = 0.0F;
        }

        if (yaOrg != ya) {
            this.yd = 0.0F;
        }

        if (zaOrg != za) {
            this.zd = 0.0F;
        }

        this.x = (this.aabb.x0 + this.aabb.x1) / 2.0F;
        this.y = this.aabb.y0 + this.heightOffset;
        this.z = (this.aabb.z0 + this.aabb.z1) / 2.0F;
    }

    public void moveRelative(float xa, float za, float speed) {
        float dist = xa * xa + za * za;
        if (!(dist < 0.01F)) {
            dist = speed / (float)Math.sqrt((double)dist);
            xa *= dist;
            za *= dist;
            float sin = (float)Math.sin(getRy() / 180 * 3.14159265359);
            float cos = (float)Math.cos(getRy() / 180 * 3.14159265359);
            this.xd += xa * cos - za * sin;
            this.zd += za * cos + xa * sin;
        }
    }

    @Override
    public void tick()
    {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
    }

    public int getHearts() {
        return hearts;
    }

    public void setHearts(int h) {
        this.hearts = h;
    }

    public int getMaxHearts() {
        return maxHearts;
    }

    public void setMaxHearts(int h) {
        this.maxHearts = h;

        if (this.hearts > this.maxHearts) {
            this.hearts = this.maxHearts;
        }
    }

    public boolean hitHandler(float fallHeight) {
        if (fallHeight >= 4) {
            this.hearts -= 5;

            if (fallHeight >= 47) {
                this.hearts = 0;
            }
            else if (fallHeight >= 30) {
                this.hearts -= 17;
            }
            else if (fallHeight >= 23) {
                this.hearts -= 13;
            }
            else if (fallHeight >= 16) {
                this.hearts -= 9;
            }
            else if (fallHeight >= 10) {
                this.hearts -= 7;
            }

            if (this.hearts < 0) {
                this.die();
            }

            return true;
        }

        return false;
    }

    public void die() {
        this.hearts = 0;
        this.dead = true;
    }

    public boolean isDead() {
        return this.dead;
    }

    public void respawn() {
        this.dead = false;
        this.teleportToSpawnPoint();
        this.hearts = this.maxHearts;
        this.xd = 0;
        this.yd = 0;
        this.zd = 0;
        this.rx = 0;
        this.ry = 0;
        this.fallValue = 0;
    }

    public boolean inWater() {
        return OpenCraft.getLevel().containsLiquid(this.aabb.grow(0.0F, -0.4F, 0.0F));
    }

    public boolean isFree(float xa, float ya, float za) {
        AABB box = this.aabb.cloneMove(xa, ya, za);
        List<AABB> aABBs = OpenCraft.getLevel().getCubes(box);
        if (aABBs.size() > 0) {
            return false;
        } else {
            return !OpenCraft.getLevel().containsLiquid(box);
        }
    }

    public void teleportToSpawnPoint()
    {
        setPosition(spawnPointX, spawnPointY, spawnPointZ);
    }

    public float getSpawnPointX()
    {
        return spawnPointX;
    }

    public float getSpawnPointY()
    {
        return spawnPointY;
    }

    public float getSpawnPointZ()
    {
        return spawnPointZ;
    }

    public void setSpawnPointX(float i)
    {
        spawnPointX = i;
    }

    public void setSpawnPointY(float i)
    {
        spawnPointY = i;
    }

    public void setSpawnPointZ(float i)
    {
        spawnPointZ = i;
    }

    public void setSpawnPoint(float x, float y, float z)
    {
        spawnPointX = x;
        spawnPointY = y;
        spawnPointZ = z;
    }

    public float getXo() {
        return xo;
    }

    public float getYo() {
        return yo;
    }

    public float getZo() {
        return zo;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public float getRx() {
        return rx;
    }

    public void setRx(float rx) {
        this.rx = rx;
    }

    public float getRy() {
        return ry;
    }

    public void setRy(float ry) {
        this.ry = ry;
    }

    public void changeLevel(Level level) {
        this.level = level;
    }

    protected void setPosition(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        float w = this.width / 2.0F;
        float h = this.height / 2.0F;
        this.aabb = new AABB(x - w, y - h, z - w, x + w, y + h, z + w);
    }

    public boolean isLit() {
        int xTile = (int)this.x;
        int yTile = (int)this.y;
        int zTile = (int)this.z;
        return this.level.isLit(xTile, yTile, zTile);
    }

    public int getEntityId() {
        return this.entityId;
    }

    @Override
    public boolean equals(Object obj) {
        return ((Entity)obj).getEntityId() == this.entityId;
    }

    public void destroy() {
        OpenCraft.unregisterTickEvent(this.tickEvent);
    }

    public float getHeightOffset() {
        return this.heightOffset;
    }

    public void setHeightOffset(float heightOffset) {
        this.heightOffset = heightOffset;
    }

    public boolean hasInventory() {
        return false;
    }

    public void pick(Item item) {}

}
