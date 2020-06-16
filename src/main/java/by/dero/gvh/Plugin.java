package by.dero.gvh;

import by.dero.gvh.lobby.Lobby;
import by.dero.gvh.minigame.Minigame;
import by.dero.gvh.model.Data;
import by.dero.gvh.model.storages.LocalStorage;
import by.dero.gvh.model.PlayerData;
import by.dero.gvh.model.StorageInterface;
import by.dero.gvh.model.storages.MongoDBStorage;
import by.dero.gvh.utils.DataUtils;
import by.dero.gvh.utils.ResourceUtils;
import com.google.gson.Gson;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class Plugin extends JavaPlugin {
    private static Plugin instance;
    private Data data;
    private PlayerData playerData;
    private PluginMode pluginMode;
    private Settings settings;

    @Override
    public void onEnable() {
        super.onEnable();
        instance = this;
        try {
            settings = new Gson().fromJson(DataUtils.loadOrDefault(new LocalStorage(),
                    "settings", "settings", ResourceUtils.readResourceFile("/settings.json")),
                    Settings.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        StorageInterface dataStorage = new LocalStorage();
        if (settings.getDataStorageType().equals("mongodb")) {
            dataStorage = new MongoDBStorage(settings.getDataMongodbConnection(), settings.getDataMongodbDatabase());
        }
        data = new Data(dataStorage);
        data.load();
        StorageInterface playerDataStorage = new LocalStorage();
        if (settings.getPlayerDataStorageType().equals("mongodb")) {
            playerDataStorage = new MongoDBStorage(
                    settings.getPlayerDataMongodbConnection(), settings.getPlayerDataMongodbDatabase());
        }
        playerData = new PlayerData(playerDataStorage);
        if (settings.getMode() == "minigame") {
            pluginMode = new Minigame();
        } else {
            pluginMode = new Lobby();
        }
        pluginMode.onEnable();
    }

    @Override
    public void onDisable() {
        pluginMode.onDisable();
    }

    public Settings getSettings() {
        return settings;
    }

    public PlayerData getPlayerData() {
        return playerData;
    }

    public static Plugin getInstance() {
        return instance;
    }

    public Data getData() {
        return data;
    }
}
