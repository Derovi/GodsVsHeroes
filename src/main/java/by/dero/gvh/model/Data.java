package by.dero.gvh.model;

import by.dero.gvh.Utils;
import by.dero.gvh.model.items.FlyBow;
import by.dero.gvh.model.itemsinfo.FlyBowInfo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Data {
    public Data(StorageInterface storageInterface) {
        this.storageInterface = storageInterface;
        registerItems();
    }

    private void registerItems() {
        // IMPORTANT register all items
        registerItem("flybow", FlyBowInfo.class, FlyBow.class);
    }

    public static String loadOrDefault(StorageInterface storage, String collection, String name, String defaultObject) throws IOException {
        if (!storage.exists(collection, name)) {
            storage.save(collection, name, defaultObject);
        }
        return storage.load(collection, name);
    }

    public void load() {
        //load items
        try {
            for (String itemName : itemNameToClass.keySet()) {
                if (!storageInterface.exists("items", itemName)) {
                    storageInterface.save("items", itemName, Utils.readResourceFile("/items/" + itemName + ".json"));
                }
                String itemJson = storageInterface.load("items", itemName);
                Gson gson = new GsonBuilder().registerTypeAdapter(ItemDescription.class,
                        ItemDescription.getDeserializer(this)).setPrettyPrinting().create();
                items.put(itemName, gson.fromJson(itemJson, ItemDescription.class));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        //load unit classes
        try {
            if (!storageInterface.exists("data", "classes")) {
                storageInterface.save("data", "classes", Utils.readResourceFile("/classes.json"));
            }
            List<UnitClassDescription> unitList = new Gson().fromJson(storageInterface.load("data", "classes"),
                    new TypeToken<List<UnitClassDescription>>() {}.getType());
            for (UnitClassDescription description : unitList) {
                units.put(description.getName(), description);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void registerItem(String name, Class infoClass, Class<?> itemClass) {
        itemNameToInfo.put(name, infoClass);
        itemNameToClass.put(name, itemClass);
        String tag = Item.getTag(name);
        itemNameToTag.put(name, tag);
        tagToItemName.put(tag, name);
    }

    private final StorageInterface storageInterface;

    private final HashMap<String, UnitClassDescription> units = new HashMap<>();

    private HashMap<String, ItemDescription> items = new HashMap<>();
    private final HashMap<String, Class<?>> itemNameToInfo = new HashMap<>();
    private final HashMap<String, Class<?>> itemNameToClass = new HashMap<>();
    private final HashMap<String, String> itemNameToTag = new HashMap<>();
    private final HashMap<String, String> tagToItemName = new HashMap<>();

    public HashMap<String, UnitClassDescription> getUnits() {
        return units;
    }

    public HashMap<String, String> getItemNameToTag() {
        return itemNameToTag;
    }

    public HashMap<String, String> getTagToItemName() {
        return tagToItemName;
    }

    public HashMap<String, Class<?>> getItemNameToInfo() {
        return itemNameToInfo;
    }

    public HashMap<String, Class<?>> getItemNameToClass() {
        return itemNameToClass;
    }

    public StorageInterface getStorageInterface() {
        return storageInterface;
    }

    ItemDescription getItemDescription(String name) {
        return items.get(name);
    }

    public HashMap<String, ItemDescription> getItems() {
        return items;
    }

    public void setItems(HashMap<String, ItemDescription> items) {
        this.items = items;
    }
}
