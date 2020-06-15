package by.dero.gvh.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class PlayerData {
    public PlayerData(StorageInterface storage) {
        this.storage = storage;
    }

    private final StorageInterface storage;

    public void registerPlayer(String playerName) {
        try {
            storage.save("players", playerName, new GsonBuilder().setPrettyPrinting().create().toJson(
                    new PlayerInfo(playerName)
            ));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean isPlayerRegistered(String playerName) {
        return storage.exists("players", playerName);
    }

    public PlayerInfo getPlayerInfo(String playerName) {
        return new Gson().fromJson(storage.load("players", playerName), PlayerInfo.class);
    }

    public void savePlayerInfo(String playerName, PlayerInfo playerInfo) {
        try {
            storage.save("players", playerName, new GsonBuilder().setPrettyPrinting().create().toJson(playerInfo));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public int getPlayerBalance(String playerName) {
        return getPlayerInfo(playerName).getBalance();
    }

    public void upgradePlayerBalance(String playerName, int balance) {
        PlayerInfo playerInfo = getPlayerInfo(playerName);
        playerInfo.setBalance(balance);
        savePlayerInfo(playerName, playerInfo);
        // TODO for database
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
}