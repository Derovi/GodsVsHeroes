package by.dero.gvh.model;

import java.util.LinkedList;
import java.util.List;

public class UnitClassDescription {
    private String name;
    private List<String> itemNames = new LinkedList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getItemNames() {
        return itemNames;
    }

    public void setItemNames(List<String> itemNames) {
        this.itemNames = itemNames;
    }
}
