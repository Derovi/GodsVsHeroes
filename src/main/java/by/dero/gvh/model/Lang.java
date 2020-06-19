package by.dero.gvh.model;

import by.dero.gvh.Plugin;
import by.dero.gvh.utils.DataUtils;
import by.dero.gvh.utils.ResourceUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.HashMap;
import java.util.Map;

public class Lang {
    private final HashMap<String, String> literals = new HashMap<>();
    private final StorageInterface storage;

    public Lang(StorageInterface storage) {
        this.storage = storage;
    }

    public void load(String locale) {
        System.out.println("/lang/" + locale + ".json");
        try {
            DataUtils.loadOrDefault(storage, "lang", locale,
                    ResourceUtils.readResourceFile("/lang/" + locale + ".json"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        JsonObject jsonObject = new JsonParser().parse(storage.load("lang", locale)).getAsJsonObject();
        loadRecursively("", jsonObject);
    }

    private void loadRecursively(String prefix, JsonElement element) {
        if (element.isJsonPrimitive()) {
            literals.put(prefix, element.getAsString());
            return;
        }
        for (Map.Entry<String, JsonElement> child : element.getAsJsonObject().entrySet()) {
            String newPrefix = prefix;
            if (!newPrefix.isEmpty()) {
                newPrefix += '.';
            }
            newPrefix += child.getKey();
            loadRecursively(newPrefix, child.getValue());
        }
    }

    public HashMap<String, String> getLiterals() {
        return literals;
    }

    public static String get(String key) {
        if (!Plugin.getInstance().getLang().getLiterals().containsKey(key)) {
            return key;
        }
        return Plugin.getInstance().getLang().getLiterals().get(key);
    }
}
