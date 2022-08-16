package com.artingl.opencraft.World.Generation;

import java.util.Random;

public class PerlinNoise extends Synth {
   private ImprovedNoise[] noiseLevels;
   private int levels;

   public PerlinNoise(int levels, int seed) {
      this(new Random(seed), levels);
   }

   public PerlinNoise(Random random, int levels) {
      this.levels = levels;
      this.noiseLevels = new ImprovedNoise[levels];

      for(int i = 0; i < levels; ++i) {
         this.noiseLevels[i] = new ImprovedNoise(random);
      }

   }

   public double getValue(double x, double y) {
      double value = 0.0D;
      double pow = 1.0D;

      for(int i = 0; i < this.levels; ++i) {
         value += this.noiseLevels[i].getValue(x / pow, y / pow) * pow;
         pow *= 2.0D;
      }

      return value;
   }

   public void destroy() {
      this.noiseLevels = null;
   }

   public int getNoiseValue(int num_iterations, float x, float y, float persistence, float scale, float low, float high)
   {
      float maxAmp = 0;
      float amp = 1;
      float freq = scale;
      float noise = 0;

      //add successively smaller, higher-frequency terms
      for(int i = 0; i < num_iterations; ++i)
      {
         noise += getValue(x * freq, y * freq) * amp;
         maxAmp += amp;
         amp *= persistence;
         freq *= 2;
      }

      //take the average value of the iterations
      noise /= maxAmp;

      //normalize the result
      noise = noise * (high - low) / 2 + (high + low) / 2;

      return (int) noise;
   }
}
