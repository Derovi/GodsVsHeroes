package by.dero.gvh.model;

import java.util.HashMap;

public class Data {
    Data(StorageInterface storageInterface) {
        this.storageInterface = storageInterface;
    }

    public void registerItem(String name, Class infoClass, Class<?> itemClass) {
        itemNameToInfo.put(name, infoClass);
        itemNameToClass.put(name, itemClass);
    }

    private StorageInterface storageInterface;

    private HashMap<String, ItemDescription> items = new HashMap<>();
    private HashMap<String, Class<?>> itemNameToInfo = new HashMap<>();
    private HashMap<String, Class<?>> itemNameToClass = new HashMap<>();

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
