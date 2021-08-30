package OpenCraft.Rendering;

import OpenCraft.World.Entity.Player;
import OpenCraft.World.Particle;
import OpenCraft.Interfaces.ITick;
import OpenCraft.OpenCraft;
import OpenCraft.World.Level;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class ParticleEngine implements ITick {
   protected Level level;
   private List<Particle> particles = new ArrayList();

   public ParticleEngine() {
      this.level = OpenCraft.getLevel();
      OpenCraft.registerTickEvent(this);
   }

   public void add(Particle p) {
      this.particles.add(p);

   }

   @Override
   public void tick() {
      for(int i = 0; i < this.particles.size(); ++i) {
         Particle p = (Particle)this.particles.get(i);
         p.update();
         if (p.removed) {
            this.particles.remove(i--);
         }
      }

   }

   public void render(Player player, float a, int layer) {
      if (this.particles.size() != 0) {
         GL11.glEnable(3553);
         GL11.glBindTexture(3553, TextureEngine.getTerrain());

         float xa = -((float)Math.cos((double)player.getRy() * 3.141592653589793D / 180.0D));
         float za = -((float)Math.sin((double)player.getRy() * 3.141592653589793D / 180.0D));
         float xa2 = -za * (float)Math.sin((double)player.getRx() * 3.141592653589793D / 180.0D);
         float za2 = xa * (float)Math.sin((double)player.getRx() * 3.141592653589793D / 180.0D);
         float ya = (float)Math.cos((double)player.getRx() * 3.141592653589793D / 180.0D);
         VerticesBuffer t = VerticesBuffer.instance;
         GL11.glColor4f(0.8F, 0.8F, 0.8F, 1.0F);
         t.begin();

         for(int i = 0; i < this.particles.size(); ++i) {
            Particle p = (Particle)this.particles.get(i);
            if (p.isLit() ^ layer == 1) {
               p.render(t, a, xa, ya, za, xa2, za2);
            }
         }

         t.end();
         GL11.glDisable(3553);
      }
   }
}
