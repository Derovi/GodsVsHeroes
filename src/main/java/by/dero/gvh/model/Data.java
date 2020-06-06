package by.dero.gvh.model;

import java.util.HashMap;

public class Data {
    Data() {}

    public void registerItem(String name, Class infoClass, Class itemClass) {

    }

    private HashMap<String, ItemDescription> items;

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
