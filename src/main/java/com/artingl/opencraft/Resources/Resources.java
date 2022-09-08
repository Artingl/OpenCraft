package com.artingl.opencraft.Resources;

import com.artingl.opencraft.Logger.Logger;
import com.artingl.opencraft.Opencraft;
import com.artingl.opencraft.Rendering.Game.TextureEngine;
import com.artingl.opencraft.Resources.Lang.Lang;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URISyntaxException;

public class Resources {

    public static class Link {
        private final String link;
        private String id;
        private String path;

        public static Link create(String value) {
            return new Link(value);
        }

        public static void validate(String value) {
            // todo: check if a link in the format of ID:PATH
        }

        private Link(String value) {
            validate(value);

            this.link = value;
            this.id = value.split(":")[0];
            this.path = value.split(":")[1];

            this.id = this.id.replace("\\", "/");
            this.path = this.path.replace("\\", "/");
        }

        public String getString() {
            return link;
        }

        public String getId() {
            return id;
        }

        public String getPath() {
            return path;
        }

        @Override
        public String toString() {
            return getString();
        }
    }

    public static final String RESOURCES_PATH = "assets";

    public static String convertToPath(String path) {
        Resources.Link.validate(path);
        Link link = Link.create(path);

        return RESOURCES_PATH + "/" + link.getId() + "/" + link.getPath();
    }

    public static InputStream load(Class<?> c, String resourcePath) {
        try {
            if (!Opencraft.getJarLocation(c).endsWith(".jar")) {
                return new FileInputStream(Opencraft.getJarLocation() + "../../../../src/main/resources/" + convertToPath(resourcePath));
            }

            return c.getClassLoader().getResourceAsStream(convertToPath(resourcePath));
        } catch (Exception e) {
            Logger.exception("Error occurred while loading resource " + resourcePath, e);
            Logger.closeOutput();
            System.exit(-1);
        }

        return null;
    }

    public static void loadNamespaceResources(Class<?> namespace, String id) throws URISyntaxException {
        Lang.loadLanguage(namespace, id);
        TextureEngine.loadTextures(namespace, id);
    }

}
