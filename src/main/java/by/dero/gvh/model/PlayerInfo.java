package by.dero.gvh.model;

import by.dero.gvh.Plugin;

import java.util.HashMap;
import java.util.Map;

public class PlayerInfo {
    private String name;
    private String selectedClass = "paladin";
    private int balance = 7000;
    private final Map<String, Map<String, Integer>> classes = new HashMap<>(); // class name and its items (name and level)

    public PlayerInfo(String name) {
        this.name = name;
    }

    public void unlockClass(String className) {
        if (classes.containsKey(className) || !canUnlock(className)) {
            return;
        }
        Map<String, Integer> items = new HashMap<>();
        UnitClassDescription unitClassDescription = Plugin.getInstance().getData().getClassNameToDescription().get(className);
        for (String itemName : unitClassDescription.getItemNames()) {
            items.put(itemName, 0);
        }
        balance -= unitClassDescription.getCost();
        classes.put(className, items);
    }

    public boolean canUnlock(String className) {
        return balance >= Plugin.getInstance().getData().getClassNameToDescription().get(className).getCost();
    }

    public Map<String, Integer> getItems(String className) {
        return classes.get(className);
    }

    public void setItemLevel(String className, String item, int level) {
        classes.get(className).put(item, level);
    }

    public int getItemLevel(String className, String item) {
        return classes.get(className).getOrDefault(item, 0);
    }

    public boolean canUpgradeItem(String className, String item) {
        Map<String, Integer> items = getItems(className);
        int level = items.get(item);
        if (level + 1 >= Plugin.getInstance().getData().getItemDescription(item).getLevels().size()) {
            return false;
        }
        int cost = Plugin.getInstance().getData().getItemDescription(item).getLevels().get(level + 1).getCost();
        return cost <= getBalance();
    }

    public void upgradeItem(String className, String item) {
        if (canUpgradeItem(className, item)) {
            Map<String, Integer> items = getItems(className);
            int newLevel = items.get(item) + 1;
            items.put(item, newLevel);
            balance -= Plugin.getInstance().getData().getItems().get(item).getLevels().get(newLevel).getCost();
        }
    }

    public boolean isClassUnlocked(String className) {
        return classes.containsKey(className);
    }

    public String getSelectedClass() {
        return selectedClass;
    }

    public void selectClass(String selectedClass) {
        this.selectedClass = selectedClass;
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
