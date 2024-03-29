package com.artingl.opencraft.Resources.Lang;

import com.artingl.opencraft.Logger.Logger;
import com.artingl.opencraft.Opencraft;
import com.artingl.opencraft.Resources.Resources;
import com.artingl.opencraft.Utils.Utils;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Lang {

    public static String defaultLanguage = "en_us";

    protected static HashMap<String, String> languageList;

    public static void init() {
        languageList = new HashMap<>();
    }

    public static void loadLanguage(Class<?> c, String id) throws URISyntaxException {
        Logger.debug("Loading languages for " + id);
        String resourcesPath = Resources.convertToPath(id + ":lang/");


        Utils.foreachFiles(c, resourcesPath, (path, name) -> {
            if (path.contains(resourcesPath) && path.endsWith(".json")) {
                Logger.debug("Loading " + id + ":lang/" + name + " language");

                Opencraft.drawLoadingScreen(Utils.capitalizeString(id) + " Languages: Loading " + Utils.removeFileExtension(name) + " language");

                try {
                    JsonElement root = new JsonParser().parse(new String(Resources.load(c, id + ":lang/" + name).readAllBytes(), StandardCharsets.UTF_8));

                    for (Map.Entry<String, JsonElement> entry: root.getAsJsonObject().entrySet()) {
                        languageList.put(id + ":" + entry.getKey(), entry.getValue().getAsString());
                    }
                } catch (IOException e) {
                    Logger.exception("Error while loading " + name, e);
                }
            }
        });

    }

    public static String getTranslatedString(String str) {
        Resources.Link.validate(str);

        if (!languageList.containsKey(str))
            return str;

        return languageList.get(str);
    }

}
