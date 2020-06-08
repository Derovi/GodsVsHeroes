package by.dero.gvh.model;

import by.dero.gvh.Utils;
import by.dero.gvh.model.items.FlyBow;
import by.dero.gvh.model.itemsinfo.FlyBowInfo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.net.URL;
import java.util.HashMap;

public class Data {
    public Data(StorageInterface storageInterface) {
        this.storageInterface = storageInterface;
        registerItems();
    }

    public void load() {
        ClassLoader classLoader = getClass().getClassLoader();
        try {
            URL url = classLoader.getResource("items");
            String path = url.getPath();
            for (File file : new File(path).listFiles()) {
                String itemName = file.getName().substring(0, file.getName().lastIndexOf('.'));
                if (!storageInterface.exists("items", itemName)) {
                    storageInterface.save("items", itemName, Utils.getResourceFileText("items/" + itemName + ".json"));
                }
                String itemJson = storageInterface.load("items", itemName);
                Gson gson = new GsonBuilder().registerTypeAdapter(ItemDescription.class,
                        ItemDescription.getDeserializer(this)).setPrettyPrinting().create();
                items.put(itemJson, gson.fromJson(itemJson, ItemDescription.class));
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

    private void registerItems() {
        // IMPORTANT register all items
        registerItem("flybow", FlyBowInfo.class, FlyBow.class);
    }

    private final StorageInterface storageInterface;

    private HashMap<String, ItemDescription> items = new HashMap<>();
    private final HashMap<String, Class<?>> itemNameToInfo = new HashMap<>();
    private final HashMap<String, Class<?>> itemNameToClass = new HashMap<>();
    private final HashMap<String, String> itemNameToTag = new HashMap<>();
    private final HashMap<String, String> tagToItemName = new HashMap<>();

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
