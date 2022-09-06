package com.artingl.opencraft.World.Entity.Models;

public class ZombieModel extends Model {

   public ZombieModel() {
      this.head.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8);
      this.body = new Box(16, 16);
      this.body.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4);
      this.arm0 = new Box(40, 16);
      this.arm0.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4);
      this.arm0.setPos(-5.0F, 2.0F, 0.0F);
      this.arm1 = new Box(40, 16);
      this.arm1.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4);
      this.arm1.setPos(5.0F, 2.0F, 0.0F);
      this.leg0 = new Box(0, 16);
      this.leg0.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4);
      this.leg0.setPos(-2.0F, 12.0F, 0.0F);
      this.leg1 = new Box(0, 16);
      this.leg1.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4);
      this.leg1.setPos(2.0F, 12.0F, 0.0F);
   }

   @Override
   public void render() {
      this.head.render();
      this.body.render();
      this.arm0.render();
      this.arm1.render();
      this.leg0.render();
      this.leg1.render();
   }

}
