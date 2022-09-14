package com.artingl.opencraft.Multiplayer;

import com.artingl.opencraft.Math.Vector2f;
import com.artingl.opencraft.Math.Vector2i;
import com.artingl.opencraft.Math.Vector3f;
import com.artingl.opencraft.Math.Vector3i;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class SocketVectorArraylist<E> extends ArrayList<E> {

    public void writeToStream(DataOutputStream outputStream) throws IOException {
        outputStream.writeInt(size());

        if (size() > 0) {
            E vector = get(0);

            if (vector instanceof Vector3f) outputStream.writeInt(0);
            if (vector instanceof Vector3i) outputStream.writeInt(1);
            if (vector instanceof Vector2f) outputStream.writeInt(2);
            if (vector instanceof Vector2i) outputStream.writeInt(3);

            for (E v : this) {
                if (v instanceof Vector3f) ((Vector3f) v).writeToStream(outputStream);
                if (v instanceof Vector3i) ((Vector3i) v).writeToStream(outputStream);
                if (v instanceof Vector2f) ((Vector2f) v).writeToStream(outputStream);
                if (v instanceof Vector2i) ((Vector2i) v).writeToStream(outputStream);
            }
        }
    }

    public void readFromStream(DataInputStream inputStream) throws IOException {
        clear();
        int size = inputStream.readInt();

        if (size > 0) {
            int type = inputStream.readInt();

            for (int i = 0; i < size; i++) {
                if (type == 0) add((E) Vector3f.readFromStream(inputStream));
                if (type == 1) add((E) Vector3i.readFromStream(inputStream));
                if (type == 2) add((E) Vector2f.readFromStream(inputStream));
                if (type == 3) add((E) Vector2i.readFromStream(inputStream));
            }
        }
    }

}
