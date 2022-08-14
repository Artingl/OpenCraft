package OpenCraft.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;

public class JarLoader {
    public static void loadJar(String path) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        URLClassLoader child = new URLClassLoader(
                new URL[] {new File(path).toURI().toURL()},
                JarLoader.class.getClassLoader()
        );

        // load modinfo
        InputStream modinfoStream = child.getResourceAsStream("modinfo.json");
        JsonElement root = new JsonParser().parse(new String(modinfoStream.readAllBytes(), StandardCharsets.UTF_8));

        Class<?> classToLoad = child.loadClass(root.getAsJsonObject().get("main_class").getAsString());
        classToLoad.newInstance();
    }
}
