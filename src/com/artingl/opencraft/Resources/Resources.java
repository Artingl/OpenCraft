package com.artingl.opencraft.Resources;

import com.artingl.opencraft.Logger.Logger;
import com.artingl.opencraft.OpenCraft;
import com.artingl.opencraft.Rendering.TextureEngine;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

public class Resources {

    public static final String RESOURCES_PATH = "assets";

    public static String convertToPath(String resourcePath) {
        String id = resourcePath.split(":")[0];
        String path = resourcePath.split(":")[1];

        id = id.replace("\\", "/");
        path = path.replace("\\", "/");

        return RESOURCES_PATH + "/" + id + "/" + path;
    }

    public static InputStream load(Class<?> c, String resourcePath) {
        try {
            if (!OpenCraft.getJarLocation(c).endsWith(".jar")) {
                return new FileInputStream(OpenCraft.getJarLocation() + "../../../src/" + convertToPath(resourcePath));
            }

            return c.getClassLoader().getResourceAsStream(convertToPath(resourcePath));
        } catch (Exception e) {
            Logger.exception("Error occurred while loading resource " + resourcePath, e);
            Logger.closeOutput();
            System.exit(-1);
        }

        return null;
    }

    public static void loadNamespaceResources(Class<?> namespace, String id) throws IOException, URISyntaxException {
        Lang.loadLanguage(namespace, id);
        TextureEngine.loadTextures(namespace, id);
    }

}
