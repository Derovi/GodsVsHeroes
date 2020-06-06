package by.dero.gvh.model;

import java.util.List;

public class ItemDescription {
    private String name;
    private int slot;
    private List<ItemInfo> levels;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public List<ItemInfo> getLevels() {
        return levels;
    }

    public void setLevels(List<ItemInfo> levels) {
        this.levels = levels;
    }
}
