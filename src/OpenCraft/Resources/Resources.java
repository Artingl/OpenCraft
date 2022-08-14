package OpenCraft.Resources;

import OpenCraft.Logger.Logger;
import OpenCraft.OpenCraft;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class Resources {

    public static final String RESOURCES_PATH = "resources";

    public static String convertToPath(String resourcePath) {
        String id = resourcePath.split(":")[0];
        String path = resourcePath.split(":")[1];

        id = id.replace("\\", "/");
        path = path.replace("\\", "/");

        return RESOURCES_PATH + "/" + id + "/" + path;
    }

    public static InputStream load(String resourcePath) {
        return load(OpenCraft.class, resourcePath);
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

}
