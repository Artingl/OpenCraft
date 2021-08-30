/*
 * The MIT License (MIT)
 *
 * Copyright © 2014-2015, Heiko Brumme
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package OpenCraft.World;


public class TickTimer {
    private static final long NS_PER_SECOND = 1000000000L;
    private static final long MAX_NS_PER_UPDATE = 1000000000L;
    private static final int MAX_TICKS_PER_UPDATE = 100;
    private float ticksPerSecond;
    private long lastTime;
    public int ticks;
    public float a;
    public float timeScale = 1.0F;
    public float fps = 0.0F;
    public float passedTime = 0.0F;

    public TickTimer(float ticksPerSecond) {
        this.ticksPerSecond = ticksPerSecond;
        this.lastTime = System.nanoTime();
    }

    public void advanceTime() {
        long now = System.nanoTime();
        long passedNs = now - this.lastTime;
        this.lastTime = now;
        if (passedNs < 0L) {
            passedNs = 0L;
        }

        if (passedNs > 1000000000L) {
            passedNs = 1000000000L;
        }

        this.fps = (float)(1000000000L / passedNs);
        this.passedTime += (float)passedNs * this.timeScale * this.ticksPerSecond / 1.0E9F;
        this.ticks = (int)this.passedTime;
        if (this.ticks > 100) {
            this.ticks = 100;
        }

        this.passedTime -= (float)this.ticks;
        this.a = this.passedTime;
    }
}