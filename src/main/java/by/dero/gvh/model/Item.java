package by.dero.gvh.model;

import by.dero.gvh.Plugin;
import org.bukkit.entity.Player;

public abstract class Item {
    private Player owner;
    private String name;
    private int level;

    public Item(String name, int level, Player owner) {
        this.name = name;
        this.level = level;
        this.owner = owner;
    }

    public ItemInfo getInfo() {
        return Plugin.getInstance().getData().getItemDescription(name).getLevels().get(level);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }
}
