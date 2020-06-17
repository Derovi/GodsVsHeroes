package by.dero.gvh.model;

import by.dero.gvh.Plugin;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;

public class PlayerInfo {
    private String name;
    private int balance = 0;
    private final Map<String, Map<String, Integer>> classes = new HashMap<>(); // class name and its items (name and level)

    public PlayerInfo(String name) {
        this.name = name;
    }

    public void unlockClass(String className) {
        if (classes.containsKey(className)) {
            return;
        }
        Map<String, Integer> items = new HashMap<>();
        UnitClassDescription unitClassDescription = Plugin.getInstance().getData().getClassNameToDescription().get(className);
        for (String itemName : unitClassDescription.getItemNames()) {
            items.put(itemName, 0);
        }
        classes.put(className, items);
    }

    public Map<String, Integer> getItems(String className) {
        return classes.get(className);
    }

    public void setItemLevel(String className, String item, int level) {
        classes.get(className).put(item, level);
    }

    public int getItemLevel(String className, String item) {
        System.out.println("" + className + " " + item);
        return classes.get(className).get(item);
    }

    public void upgradeItem(String className, String item) {
        Map<String, Integer> items = getItems(className);
        int level = items.get(item);
        if (level + 1 < Plugin.getInstance().getData().getItemDescription(item).getLevels().size()) {
            items.put(item, level + 1);
        }
    }

    public boolean isClassUnlocked(String className) {
        return classes.containsKey(className);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public String getName() {
        return name;
    }

    public int getBalance() {
        return balance;
    }

    public Map<String, Map<String, Integer>> getClasses() {
        return classes;
    }
}
