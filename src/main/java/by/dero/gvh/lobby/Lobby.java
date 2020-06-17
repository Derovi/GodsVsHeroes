package by.dero.gvh.lobby;

import by.dero.gvh.Plugin;
import by.dero.gvh.PluginMode;
import by.dero.gvh.lobby.utils.VoidGenerator;
import by.dero.gvh.model.StorageInterface;
import by.dero.gvh.model.storages.LocalStorage;
import by.dero.gvh.model.storages.MongoDBStorage;
import by.dero.gvh.utils.DataUtils;
import by.dero.gvh.utils.ResourceUtils;
import com.google.gson.Gson;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class Lobby implements PluginMode {
    private static Lobby instance;
    private LobbyInfo info;
    private LobbyData data;
    private File lobbySchematicFile;
    private final String worldName = "Lobby";
    private World world;

    @Override
    public void onEnable() {
        instance = this;
        registerEvents();
        try {
            info = new Gson().fromJson(DataUtils.loadOrDefault(new LocalStorage(), "lobby", "lobby",
                    ResourceUtils.readResourceFile("/lobby/lobby.json")), LobbyInfo.class);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        // create world
        if (Plugin.getInstance().getServer().getWorld(worldName) == null) {
            WorldCreator creator = new WorldCreator(worldName);
            creator.generator(new VoidGenerator());
            world = creator.createWorld();
        }
        world = Plugin.getInstance().getServer().getWorld(worldName);
        StorageInterface dataStorage = new LocalStorage();
        if (Plugin.getInstance().getSettings().getPlayerDataStorageType().equals("mongodb")) {
            dataStorage = new MongoDBStorage(
                    Plugin.getInstance().getSettings().getLobbyDataMongodbConnection(),
                    Plugin.getInstance().getSettings().getLobbyDataMongodbDatabase());
        }
        data = new LobbyData(dataStorage);
        System.out.println("Loading schematic");
        loadSchematic();
    }

    @Override
    public void onDisable() {}

    private void loadSchematic() {
        lobbySchematicFile = new File(LocalStorage.getPrefix() + "lobby/lobby.schem");
        try {
            ResourceUtils.exportResource("/lobby/lobby.schem", LocalStorage.getPrefix() + "lobby/lobby.schem");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return;
        /*
        try {
            String data = ResourceUtils.readResourceFile("/lobby/lobby.schem");
            File directory = new File(LocalStorage.getPrefix());
            directory.mkdirs();
            BufferedWriter writer = new BufferedWriter(new FileWriter(LocalStorage.getPrefix() + "lobby/lobby.schem"));
            writer.write(data);
            writer.close();
            System.out.println("Schematic loaded");
        } catch (Exception ex) {
            ex.printStackTrace();
        }*/
    }

    public static Lobby getInstance() {
        return instance;
    }

    public LobbyData getData() {
        return data;
    }

    private void registerEvents() {
        Bukkit.getPluginManager().registerEvents(new LobbyEvents(), Plugin.getInstance());
    }

    public LobbyInfo getInfo() {
        return info;
    }

    public String getWorldName() {
        return worldName;
    }

    public World getWorld() {
        return world;
    }

    public File getLobbySchematicFile() {
        return lobbySchematicFile;
    }
}
