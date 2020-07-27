package by.dero.gvh.model;

import by.dero.gvh.BoosterManager;
import by.dero.gvh.Plugin;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerInfo {
    private String name;
    private String selectedClass = "assassin";
    @Getter
    private HashMap<String, Cosmetic> cosmetics = null;
    @Getter @Setter
    private List<Booster> boosters = new ArrayList<>();
    private int balance = 100;
    private final Map<String, Map<String, Integer>> classes = new HashMap<>(); // class name and its items (name and level)

    public PlayerInfo() {
        this.cosmetics = new HashMap<>();
    }

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

    public void unlockCosmetic(String name) {
        cosmetics.put(name, new Cosmetic(name));
    }

    public void disableCosmetic(String name) {
        if (!cosmetics.containsKey(name)) {
            return;
        }
        cosmetics.get(name).setEnabled(false);
    }

    public void activateBooster(String booster) {
        activateBooster(Plugin.getInstance().getBoosterManager().getBoosters().get(booster));
    }

    public void activateBooster(BoosterInfo info) {
        if (info.getName().equals("L5")) {
            for (Booster booster : boosters) {
                if (booster.getName().equals("L5")) {
                    booster.setBonus(booster.getBonus() + 0.1);
                    return;
                }
            }
            boosters.add(new Booster("L5", -1, -1, 0.1));
            return;
        }
        long currentTime = System.currentTimeMillis();
        boosters.add(new Booster(info.getName(),
                currentTime, currentTime + info.getDurationSec() * 1000, 0));
    }

    public void removeExpiredBoosters() {
        BoosterManager.removeExpiredBoosters(boosters);
    }

    public void enableCosmetic(String name) {
        if (!cosmetics.containsKey(name)) {
            return;
        }
        CosmeticInfo info =
                Plugin.getInstance().getCosmeticManager().getCustomizations().get(name);
        for (Cosmetic other : cosmetics.values()) {
            CosmeticInfo otherInfo =
                    Plugin.getInstance().getCosmeticManager().getCustomizations().get(other.getName());
            if (info.getGroupID() == otherInfo.getGroupID()) {
                other.setEnabled(false);
            }
        }
        System.out.println("Enabled: " + name);
        cosmetics.get(name).setEnabled(true);
    }

    public int getItemLevel(String className, String item) {
        if (!classes.containsKey(className)) {
            return 0;
        }
        return classes.get(className).getOrDefault(item, 0);
    }

    public boolean canUpgradeItem(String className, String item) {
        Map<String, Integer> items = getItems(className);
        if (!items.containsKey(item)) {
            items.put(item, 0);
        }
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
