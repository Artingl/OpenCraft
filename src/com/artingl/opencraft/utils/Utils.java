package com.artingl.opencraft.utils;

import com.artingl.opencraft.OpenCraft;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Utils {

    public static void foreachFiles(Class<?> c, String resource, BiConsumer<String, String> callback) throws URISyntaxException {
        String jarLocation = OpenCraft.getJarLocation(c);
        if (jarLocation.endsWith(".jar")) {
            // game is running inside jar file
            try (ZipFile zipFile = new ZipFile(jarLocation)) {
                Enumeration zipEntries = zipFile.entries();
                while (zipEntries.hasMoreElements()) {
                    String path = ((ZipEntry) zipEntries.nextElement()).getName();
                    String name = Paths.get(path).getFileName().toString();

                    callback.accept(path, name);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            // outside jar file (probably running in sort of IDE)
            List<File> files = new ArrayList<>();

            listf(jarLocation + "../../../src/", files);

            for (File f: files) {
                callback.accept(f.getPath().replace("\\", "/"), f.getName());
            }

            files.clear();
        }

    }

    public static void listf(String directoryName, List<File> files) {
        File directory = new File(directoryName);

        // Get all files from a directory.
        File[] fList = directory.listFiles();
        if(fList != null)
            for (File file : fList) {
                if (file.isFile()) {
                    files.add(file);
                } else if (file.isDirectory()) {
                    listf(file.getAbsolutePath(), files);
                }
            }
    }

}
