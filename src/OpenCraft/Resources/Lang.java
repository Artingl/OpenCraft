package OpenCraft.Resources;

import OpenCraft.Logger.Logger;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Lang {

    protected static HashMap<String, String> languageList;

    public static void init() {
        languageList = new HashMap<>();
    }

    public static void loadLanguage(String id, String languagePath) throws IOException {
        Logger.info("Loading " + languagePath);

        JsonElement root = new JsonParser().parse(new String(Resources.load(languagePath).readAllBytes(), StandardCharsets.UTF_8));

        for (Map.Entry<String, JsonElement> entry: root.getAsJsonObject().entrySet()) {
            languageList.put(id + ":" + entry.getKey(), entry.getValue().getAsString());
        }
    }

    public static String getLanguageString(String str) {
        if (!languageList.containsKey(str))
            return "";

        return languageList.get(str);
    }

}
