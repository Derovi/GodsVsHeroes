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
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;

public class Plugin extends JavaPlugin implements Listener {
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
        StorageInterface serverDataStorage = new LocalStorage();
        if (settings.getServerDataStorageType().equals("mongodb")) {
            serverDataStorage = new MongoDBStorage(
                    settings.getServerDataMongodbConnection(), settings.getServerDataMongodbDatabase());
        }
        serverData = new ServerData(serverDataStorage);
        serverData.load();
        World world;
        if (settings.getMode().equals("minigame")) {
            pluginMode = new Minigame();
            pluginMode.onEnable();

            world = Bukkit.getWorld(Minigame.getInstance().getGame().getInfo().getWorld());
            for (final Entity ent : world.getLivingEntities()) {
                ent.remove();
            }
        } else {
            pluginMode = new Lobby();
            pluginMode.onEnable();

            world = Lobby.getInstance().getWorld();
            Bukkit.getPluginManager().registerEvents((Listener) pluginMode, this);
        }
        Bukkit.getPluginManager().registerEvents(this, this);
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

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        serverData.updateOnline(settings.getServerName(),
                Bukkit.getServer().getOnlinePlayers().size());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        serverData.updateOnline(settings.getServerName(),
                Bukkit.getServer().getOnlinePlayers().size() - 1);
    }
}
