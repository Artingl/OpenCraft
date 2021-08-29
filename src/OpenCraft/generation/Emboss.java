package OpenCraft.generation;

public class Emboss extends Synth {
   private Synth synth;

   public Emboss(Synth synth) {
      this.synth = synth;
   }

   public double getValue(double x, double y) {
      return this.synth.getValue(x, y) - this.synth.getValue(x + 1.0D, y + 1.0D);
   }
}
