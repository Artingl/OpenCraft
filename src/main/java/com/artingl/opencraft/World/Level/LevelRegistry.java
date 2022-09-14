package com.artingl.opencraft.World.Level;

import com.artingl.opencraft.Logger.Logger;
import com.artingl.opencraft.World.Level.Generation.LevelGeneration;

import java.util.HashMap;

public class LevelRegistry {

    private static HashMap<LevelTypes, Class<?>> generationMap;

    public static void init() {
        Logger.debug("Initializing LevelRegistry");
        generationMap = new HashMap<>();
    }

    public static void destroy() {
        generationMap.clear();
    }

    public static LevelGeneration createLevelGenerationInstance(LevelTypes type, ClientLevel.Generation generation) {
        try {
            LevelGeneration levelGeneration = (LevelGeneration) generationMap.get(type).getConstructor().newInstance();
            levelGeneration.initialize(generation);

            return levelGeneration;
        } catch (Exception e) {
            Logger.exception("Unable to create LevelGeneration instance!", e);
        }

        return null;
    }

    public static void setLevelGeneration(LevelTypes type, Class<?> generation) {
        Logger.debug("Setting " + generation + " for " + type);
        generationMap.put(type, generation);
    }

}
