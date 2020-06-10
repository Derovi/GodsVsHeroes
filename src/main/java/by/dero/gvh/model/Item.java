package by.dero.gvh.model;

import by.dero.gvh.Plugin;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public abstract class Item {
    private Player owner;
    private String name;
    private int level;

    private final Set<UUID> summonedEntityIds = new HashSet<>();

    public Item(String name, int level, Player owner) {
        this.name = name;
        this.level = level;
        this.owner = owner;
    }

    public static String getTag(String name) {
        StringBuilder tag = new StringBuilder();
        int hashcode = Math.abs(name.hashCode());
        while (hashcode > 0) {
            tag.append(hashcode % 10);
            hashcode /= 10;
        }
        return tag.toString();
    }

    public Set<UUID> getSummonedEntityIds() {
        return summonedEntityIds;
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
