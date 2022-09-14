package com.artingl.opencraft.Resources.Options;

import com.artingl.opencraft.Logger.Logger;
import com.artingl.opencraft.Opencraft;
import com.google.gson.*;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class OptionsRegistry {
    public static class Option {

        public enum Types {
            STRING, INTEGER, BOOLEAN
        }

        private Object value;
        private Types type;
        private final String optionName;

        public Option(String optionName, Object value) {
            this.optionName = optionName;
            this.setValue(value);
        }

        public Types getType() {
            return type;
        }

        public int getInt() {
            return (int) this.value;
        }

        public String getString() {
            return (String) this.value;
        }

        public boolean getBoolean() {
            return (boolean) this.value;
        }

        public String getOptionName() {
            return optionName;
        }

        public Option setValue(Object value) {
            this.value = value;

            if (value instanceof Integer) {
                this.type = Types.INTEGER;
            }

            if (value instanceof Boolean) {
                this.type = Types.BOOLEAN;
            }

            if (value instanceof String) {
                this.type = Types.STRING;
            }

            return this;
        }

        public String packValue() {
            if (this.type == Types.INTEGER) {
                return ""+((int)this.value);
            }
            else if (this.type == Types.BOOLEAN) {
                return (boolean) this.value ? "1" : "0";
            }
            else if (this.type == Types.STRING) {
                return (String) this.value;
            }

            return "0";
        }

        public void unpackValue(String type, JsonElement value) {
            switch (type) {
                case "INTEGER" -> {
                    this.type = Types.INTEGER;
                    this.value = value.getAsInt();
                }
                case "BOOLEAN" -> {
                    this.type = Types.BOOLEAN;
                    this.value = value.getAsInt() == 1;
                }
                case "STRING" -> {
                    this.type = Types.STRING;
                    this.value = value.getAsString();
                }
            }
        }
    }

    public static class Values {
        private static HashMap<String, Option> options;

        protected static JsonElement jsonElement;
        public static String createDefaultConfig() {
            return "{}";
        }

        public static Option getOption(String name) {
            return options.get(name);
        }

        public static int getIntOption(String name) {
            return options.get(name).getInt();
        }

        public static boolean getBooleanOption(String name) {
            return options.get(name).getBoolean();
        }

        public static String getStringOption(String name) {
            return options.get(name).getString();
        }

        public static void init() {
            options = new HashMap<>();

            options.put("renderDistance",           new Option("renderDistance", 6));
            options.put("FOV",                      new Option("FOV", 70));
            options.put("guiScale",                 new Option("guiScale", 2));
            options.put("soundVolume",              new Option("soundVolume", 100));
            options.put("enableViewBobbing",        new Option("enableViewBobbing", true));
            options.put("showInformation",          new Option("showInformation", false));
            options.put("enableFog",                new Option("enableFog", true));
        }
    }

    public static void init() {
        Values.init();
    }

    public static void loadOptions() throws IOException {
        File settingsFile = new File(Opencraft.getGameDirectory() + "options.json");
        if (!settingsFile.isFile()) {
            FileOutputStream fos = new FileOutputStream(settingsFile);
            byte[] buffer = Values.createDefaultConfig().getBytes();
            fos.write(buffer, 0, buffer.length);
            fos.flush();
            fos.close();
        }

        try {
            Values.jsonElement = new JsonParser().parse(new String(new FileInputStream(settingsFile).readAllBytes(), StandardCharsets.UTF_8));

            for (Map.Entry<String, JsonElement> entry : Values.jsonElement.getAsJsonObject().entrySet()) {
                Option option = Values.getOption(entry.getKey());

                if (option == null) {
                    Logger.error("Invalid option " + entry.getKey() + "! Ignore it");
                    continue;
                }

                option.unpackValue(entry.getValue().getAsJsonObject().get("type").getAsString(), entry.getValue().getAsJsonObject().get("value"));
            }
        } catch (Exception e) {
            Logger.exception("Unable to parse options!", e);
            Logger.debug("Writing default options");

            FileOutputStream fos = new FileOutputStream(settingsFile);
            byte[] buffer = Values.createDefaultConfig().getBytes();
            fos.write(buffer, 0, buffer.length);
            fos.flush();
            fos.close();

            Logger.debug("Trying to load options again");
            loadOptions();
        }

        System.gc();
    }

    public static void updateOption(Option field) {
        Opencraft.getThreadsManager().execute(() -> {
            try {
                Writer writer = new FileWriter(Opencraft.getGameDirectory() + "options.json");
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                JsonObject jsonObject = gson.fromJson(Values.jsonElement, JsonObject.class);
                jsonObject.add(field.optionName, gson.fromJson("{\"type\": \"" + field.type + "\", \"value\": " + field.packValue() + "}", JsonObject.class));
                gson.toJson(jsonObject, writer);
                writer.close();

                for (Map.Entry<Integer, OptionsListener> entry: Opencraft.getOptionsListeners().entrySet()) {
                    entry.getValue().optionUpdated(field);
                }
            } catch (Exception e) {
                Logger.exception("Unable to set setting field " + field, e);
            }
        });
    }

}
