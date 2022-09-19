package com.artingl.opencraft.Control.Render;

import java.util.ArrayList;

public class VericiesBuffer extends BufferRenderer {

    private ArrayList<Float> buffer;

    public VericiesBuffer() {
        this.buffer = new ArrayList<>();
    }

    public void end() {
    }


    public void clear() {
        super.clear();
        this.buffer.clear();
    }

    public void begin() {
        super.begin();
        this.clear();
    }

    public void vertex(float x, float y, float z) {
        if (this.hasTexture) {
            this.buffer.add(this.u); this.p++;
            this.buffer.add(this.v); this.p++;
        }

        if (this.hasColor) {
            this.buffer.add(this.r); this.p++;
            this.buffer.add(this.g); this.p++;
            this.buffer.add(this.b); this.p++;
        }

        this.buffer.add(x); this.p++;
        this.buffer.add(y); this.p++;
        this.buffer.add(z); this.p++;
    }

    public ArrayList<Float> getBuffer() {
        return buffer;
    }
}
