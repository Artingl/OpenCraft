package OpenCraft.World.Entity;

import OpenCraft.Game.phys.AABB;
import OpenCraft.Interfaces.ITick;
import OpenCraft.OpenCraft;
import OpenCraft.World.Level;

import java.util.List;

public class Entity implements ITick
{

    /* Collision and physics */
    public float xd;
    public float yd;
    public float zd;
    public AABB aabb;
    public boolean onGround = false;
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

    protected float spawnPointX = 0;
    protected float spawnPointY = 0;
    protected float spawnPointZ = 0;

    /* Entity rotation */
    protected float rx; // X rotation
    protected float ry; // Y rotation

    public Entity(Level level)
    {
        this.level = level;
        reset(0, 0, 0);
    }

    public Entity()
    {
        this.level = OpenCraft.getLevel();
        reset(0, 0, 0);
    }

    public Entity(float x, float y, float z)
    {
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
        this.aabb = new AABB(x - w, y - h, z - w, x + w, y + h, z + w);
        OpenCraft.registerTickEvent(this);
    }

    protected void remove()
    {
        removed = true;
    }

    protected void setSize(float w, float h) {
        this.width = w;
        this.height = h;
    }

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

    protected static long symmetricRound( double d ) {
        return d < 0 ? - Math.round( -d ) : Math.round( d );
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

}
