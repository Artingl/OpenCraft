package com.artingl.opencraft;

import com.artingl.opencraft.Logger.Logger;

public class Start
{
    public static void main(String[] args) {
        try {
            new Opencraft(args);
        } catch (Exception e) {
            Logger.exception("Critical error while running the game!", e);
            Logger.closeOutput();
            System.exit(-1);
        }
    }

}
