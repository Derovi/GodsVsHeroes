package by.dero.gvh;

import by.dero.gvh.lobby.Lobby;
import by.dero.gvh.minigame.Minigame;
import by.dero.gvh.model.Data;
import by.dero.gvh.model.ServerData;
import by.dero.gvh.model.storages.LocalStorage;
import by.dero.gvh.model.PlayerData;
import by.dero.gvh.model.StorageInterface;
import by.dero.gvh.model.storages.MongoDBStorage;
import by.dero.gvh.utils.DataUtils;
import by.dero.gvh.utils.ResourceUtils;
import com.google.gson.Gson;
import net.minecraft.server.v1_15_R1.GameRules;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;

public class Plugin extends JavaPlugin {
    private static Plugin instance;
    private Data data;
    private PlayerData playerData;
    private ServerData serverData;
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
        World world = Bukkit.getWorld("world");
        if (settings.getMode().equals("minigame")) {
            pluginMode = new Minigame();
            for (final Entity ent : world.getLivingEntities()) {
                ent.remove();
            }
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "difficulty normal");
        } else {
            pluginMode = new Lobby();

            Bukkit.getPluginManager().registerEvents((Listener) pluginMode, this);
            world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
            world.setDifficulty(Difficulty.PEACEFUL);
        }
        pluginMode.onEnable();
        StorageInterface serverDataStorage = new LocalStorage();
        if (settings.getServerDataStorageType().equals("mongodb")) {
            serverDataStorage = new MongoDBStorage(
                    settings.getServerDataMongodbConnection(), settings.getServerDataMongodbDatabase());
        }
        serverData = new ServerData(serverDataStorage);
        world.setGameRule(GameRule.DO_MOB_LOOT, false);
    }

    @Override
    public void onDisable() {
        pluginMode.onDisable();
    }

    public ServerData getServerData() {
        return serverData;
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
