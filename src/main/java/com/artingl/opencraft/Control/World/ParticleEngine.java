package com.artingl.opencraft.Control.World;

import com.artingl.opencraft.Logger.Logger;
import com.artingl.opencraft.Math.Vector2f;
import com.artingl.opencraft.Control.Game.TextureEngine;
import com.artingl.opencraft.Control.Render.BufferRenderer;
import com.artingl.opencraft.World.Entity.EntityPlayer;
import com.artingl.opencraft.World.Level.ClientLevel;
import com.artingl.opencraft.World.Ambient.Block.Particle;
import com.artingl.opencraft.World.Tick;
import com.artingl.opencraft.Opencraft;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class ParticleEngine implements Tick {
   private List<Particle> particles = new ArrayList();

   public ParticleEngine() {
      Opencraft.registerTickEvent(this);
   }

   public void add(Particle p) {
      this.particles.add(p);

   }

   public void destroy() {
      this.particles.clear();
      this.particles = null;
   }

   @Override
   public void tick() {
      for(int i = 0; i < this.particles.size(); ++i) {
         Particle p = (Particle)this.particles.get(i);
         p.update();
         if (p.isRemoved()) {
            this.particles.remove(i--);
         }
      }

   }

   public void render(float a) {
      if (this.particles.size() != 0) {
         GL11.glEnable(3553);
         GL11.glBindTexture(3553, TextureEngine.getTerrain());

         ClientLevel level = Opencraft.getLevel();
         EntityPlayer player = Opencraft.getPlayerEntity();

         Vector2f rotation = player.getRotation();

         float xa = -((float)Math.cos((double)rotation.y * 3.141592653589793D / 180.0D));
         float za = -((float)Math.sin((double)rotation.y * 3.141592653589793D / 180.0D));
         float xa2 = -za * (float)Math.sin((double)rotation.x * 3.141592653589793D / 180.0D);
         float za2 = xa * (float)Math.sin((double)rotation.x * 3.141592653589793D / 180.0D);
         float ya = (float)Math.cos((double)rotation.x * 3.141592653589793D / 180.0D);
         BufferRenderer t = BufferRenderer.getGlobalInstance();
         GL11.glColor4f(0.8F, 0.8F, 0.8F, 1.0F);
         t.begin();

         for(int i = 0; i < this.particles.size(); ++i) {
            Particle p = (Particle)this.particles.get(i);
            try {
               p.render(t, a, xa, ya, za, xa2, za2);
            } catch (Exception e) {
               Logger.exception("Error occurred while rendering particle", e);
               p.destroy();
               this.particles.remove(p);
            }
         }

         t.end();
         GL11.glDisable(3553);
      }
   }
}
