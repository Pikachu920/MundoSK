package com.pie.tlatoani.WorldManagement.WorldLoader;

import com.pie.tlatoani.Generator.ChunkGeneratorWithID;
import com.pie.tlatoani.Mundo;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.function.BiConsumer;

/**
 * Created by Tlatoani on 7/3/16.
 */
public final class UtilWorldLoader {
    private static Map<String, WorldCreator> worldLoaderSaver = new HashMap<String, WorldCreator>();
    private final static String filename = "worldloader.json";
    private static boolean success = true;

    private UtilWorldLoader() {} //Cannot be initialized

    public static void load() {
        File file = getLoaderFile();
        if (file != null) readJSONObject(file).forEach(new BiConsumer() {
            @Override
            public void accept(Object key, Object value) {
                String s = (String) key;
                JSONObject jsonObject = (JSONObject) value;
                WorldCreator creator = getCreatorFromJSON(s, jsonObject);
                setCreator(creator);
                creator.createWorld();
            }
        });
        if (success) {
            if (getAllCreators().isEmpty()) {
                Mundo.info("No worlds were assigned to load automatically");
            } else {
                Mundo.info("Worlds to automatically load were loaded successfully!");
            }
        }
    }

    public static void save() throws IOException {
        FileWriter writer = new FileWriter(getLoaderFile());
        writer.write(getJSONOfData().toString());
        writer.flush();
        writer.close();
    }

    public static File getLoaderFile() {
        try {
            File result = new File(Mundo.pluginFolder + File.separator + filename);
            if (!result.exists()) {
                result.createNewFile();
                //JsonObject emptyObject = Json.createObjectBuilder().build();
                JSONObject emptyObject = new JSONObject();
                FileWriter writer = new FileWriter(result);
                writer.write(emptyObject.toString());
                writer.flush();
                writer.close();
            }
            return result;
        } catch (IOException e) {
            Mundo.info("MundoSK encountered problems while reading the file for automatically loaded worlds");
            Mundo.info("Any worlds set to automatically load were not loaded");
            Mundo.debug(UtilWorldLoader.class, e);
            success = false;
        }
        return null;
    }

    public static JSONObject readJSONObject(File jsonFile) {
        JSONParser parser = new JSONParser();
        JSONObject result = null;
        try {
            result = (JSONObject) parser.parse(new FileReader(jsonFile));
        } catch (IOException | ParseException | ClassCastException e) {
            Mundo.info("MundoSK encountered problems while reading the file for automatically loaded worlds");
            Mundo.info("Any worlds set to automatically load were not loaded");
            Mundo.debug(UtilWorldLoader.class, e);
            success = false;
        }
        return result;
    }

    public static JSONObject getJSONOfData() {
        //JsonObjectBuilder builder = Json.createObjectBuilder();
        JSONObject jsonObject = new JSONObject();
        worldLoaderSaver.forEach(new BiConsumer<String, WorldCreator>() {
            @Override
            public void accept(String s, WorldCreator worldCreator) {
                jsonObject.put(s, getCreatorJSON(worldCreator));
            }
        });
        //return builder.build();
        return jsonObject;
    }

    //Map Interactions

    public static WorldCreator getCreator(String worldname) {
        return worldLoaderSaver.get(worldname);
    }

    public static void setCreator(WorldCreator worldCreator) {
        worldLoaderSaver.put(worldCreator.name(), worldCreator);
    }

    public static void removeCreator(String worldname) {
        worldLoaderSaver.remove(worldname);
    }

    public static List<WorldCreator> getAllCreators() {
        List<WorldCreator> result = new ArrayList<>();
        worldLoaderSaver.forEach(new BiConsumer<String, WorldCreator>() {
            @Override
            public void accept(String s, WorldCreator creator) {
                result.add(creator);
            }
        });
        return result;
    }

    public static void clearAllCreators() {
        worldLoaderSaver.clear();
    }

    //Conversion

    public static JSONObject getCreatorJSON(WorldCreator creator) {
        //JsonObjectBuilder creatorJsonBuilder = Json.createObjectBuilder();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("environment", creator.environment().toString());
        jsonObject.put("worldtype", creator.type().toString());
        jsonObject.put("structures", creator.generateStructures());
        jsonObject.put("seed", Long.toString(creator.seed()));
        jsonObject.put("generatorsettings", creator.generatorSettings());
        if (creator.generator() instanceof ChunkGeneratorWithID) {
            jsonObject.put("generator", ((ChunkGeneratorWithID) creator.generator()).id);
        }
        //return creatorJsonBuilder.build();
        return jsonObject;
    }

    public static WorldCreator getCreatorFromJSON(String worldname, JSONObject creatorJSON) {
        WorldCreator creator = new WorldCreator(worldname);
        creator.environment(World.Environment.valueOf((String) creatorJSON.get("environment")));
        creator.type(WorldType.valueOf((String) creatorJSON.get("worldtype")));
        creator.generateStructures((Boolean) creatorJSON.get("structures"));
        creator.seed(Long.parseLong((String) creatorJSON.get("seed")));
        String generator;
        if ((generator = (String) creatorJSON.get("generator")) != null) {
            creator.generator(generator);
        }
        creator.generatorSettings((String) creatorJSON.get("generatorsettings"));
        return creator;
    }
}
