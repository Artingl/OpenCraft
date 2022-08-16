package com.artingl.opencraft.Utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;

public class JavaUtils {
    public static void loadJar(String path) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        URLClassLoader child = new URLClassLoader(
                new URL[] {new File(path).toURI().toURL()},
                JavaUtils.class.getClassLoader()
        );

        // load modinfo
        InputStream modinfoStream = child.getResourceAsStream("modinfo.json");
        JsonElement root = new JsonParser().parse(new String(modinfoStream.readAllBytes(), StandardCharsets.UTF_8));

        Class<?> classToLoad = child.loadClass(root.getAsJsonObject().get("main_class").getAsString());
        classToLoad.newInstance();
    }

    public static void modifyLibraryPath(String s) throws IOException {
        try {
            // This enables the java.library.path to be modified at runtime
            // From a Sun engineer at http://forums.sun.com/thread.jspa?threadID=707176
            //
            Field field = ClassLoader.class.getDeclaredField("usr_paths");
            field.setAccessible(true);
            String[] paths = (String[])field.get(null);
            for (int i = 0; i < paths.length; i++) {
                if (s.equals(paths[i])) {
                    return;
                }
            }
            String[] tmp = new String[paths.length+1];
            System.arraycopy(paths,0,tmp,0,paths.length);
            tmp[paths.length] = s;
            field.set(null,tmp);
            System.setProperty("java.library.path", System.getProperty("java.library.path") + File.pathSeparator + s);
        } catch (IllegalAccessException e) {
            throw new IOException("Failed to get permissions to set library path");
        } catch (NoSuchFieldException e) {
            throw new IOException("Failed to get field handle to set library path");
        }
    }

}
