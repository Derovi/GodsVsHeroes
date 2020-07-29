package by.dero.gvh.model;

import by.dero.gvh.Plugin;
import by.dero.gvh.model.storages.MongoDBStorage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerData {
    public PlayerData(MongoDBStorage storage) {
        this.storage = storage;
        // cleaning stored Data
        new BukkitRunnable() {
            @Override
            public void run() {
                List<String> shouldRemove = new ArrayList<>();
                for (String playerName : storedData.keySet()) {
                    if (Bukkit.getPlayerExact(playerName) == null) {
                        shouldRemove.add(playerName);
                    }
                }
                for (String name : shouldRemove) {
                    storedData.remove(name);
                }
            }
        }.runTaskTimer(Plugin.getInstance(), 1200, 1200);
    }

    private final MongoDBStorage storage;
    private final Map<String, PlayerInfo> storedData = new HashMap<>();

    public void registerPlayer(String playerName) {
        try {
            savePlayerInfo(playerName, new PlayerInfo(playerName));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean isPlayerRegistered(String playerName) {
        return storage.exists("players", playerName);
    }

    public PlayerInfo getPlayerInfo(String playerName) {
        PlayerInfo result = new Gson().fromJson(storage.load("players", playerName), PlayerInfo.class);
        storedData.put(playerName, result);
        return result;
    }

    public PlayerInfo getStoredPlayerInfo(String playerName) {
        if (storedData.containsKey(playerName)) {
            return storedData.get(playerName);
        }
        return getPlayerInfo(playerName);
    }

    public void savePlayerInfo(String playerName, PlayerInfo playerInfo) {
        try {
            storedData.put(playerName, playerInfo);
            storage.save("players", playerName, new GsonBuilder().setPrettyPrinting().create().toJson(playerInfo));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public int getPlayerBalance(String playerName) {
        return getPlayerInfo(playerName).getBalance();
    }

    public void selectClass(String playerName, String className) {
        PlayerInfo playerInfo = getPlayerInfo(playerName);
        playerInfo.selectClass(className);
        savePlayerInfo(playerName, playerInfo);
        // TODO for database
    }

    public void updatePlayerBalance(String playerName, int balance) {
        PlayerInfo playerInfo = getPlayerInfo(playerName);
        playerInfo.setBalance(balance);
        savePlayerInfo(playerName, playerInfo);
        // TODO for database
    }

    public void increaseBalance(String playerName, int count) {
        PlayerInfo playerInfo = getPlayerInfo(playerName);
        playerInfo.setBalance(playerInfo.getBalance() + count);
        savePlayerInfo(playerName, playerInfo);
    }

    public void unlockClass(String playerName, String className) {
        PlayerInfo playerInfo = getPlayerInfo(playerName);
        playerInfo.unlockClass(className);
        savePlayerInfo(playerName, playerInfo);
        // TODO for database
    }

    public void updateItem(String playerName, String className, String itemName) {
        PlayerInfo playerInfo = getPlayerInfo(playerName);
        playerInfo.upgradeItem(className, itemName);
        savePlayerInfo(playerName, playerInfo);
        // TODO for database
    }

    public StorageInterface getStorage() {
        return storage;
    }

    public void savePlayerInfo(PlayerInfo info) {
        savePlayerInfo(info.getName(), info);
    }
}
