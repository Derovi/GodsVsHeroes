package by.dero.gvh.lobby;

import by.dero.gvh.Plugin;
import by.dero.gvh.PluginMode;
import by.dero.gvh.lobby.monuments.MonumentManager;
import by.dero.gvh.lobby.utils.VoidGenerator;
import by.dero.gvh.model.StorageInterface;
import by.dero.gvh.model.storages.LocalStorage;
import by.dero.gvh.model.storages.MongoDBStorage;
import by.dero.gvh.utils.DataUtils;
import by.dero.gvh.utils.Position;
import by.dero.gvh.utils.ResourceUtils;
import com.google.gson.Gson;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import java.io.File;
import java.util.HashMap;
import java.util.Random;

public class Lobby implements PluginMode {
    private static Lobby instance;
    private LobbyInfo info;
    private LobbyData data;
    private File lobbySchematicFile;
    private final String worldName = "Lobby";
    private World world;
    private final HashMap<String, PlayerLobby> activeLobbies = new HashMap<>();
    private MonumentManager monumentManager;

    @Override
    public void onEnable() {
        instance = this;
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
        data.load();
        monumentManager = new MonumentManager();
        Bukkit.getPluginManager().registerEvents(monumentManager, Plugin.getInstance());
        Bukkit.getPluginManager().registerEvents(new LobbyEvents(), Plugin.getInstance());
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
    }

    public void playerJoined(Player player) {
        LobbyRecord record;
        PlayerLobby playerLobby;
        if (!data.recordExists(player.getName())) {
            // if record not exists (player is new), create record and loby
            record = generateNewRecord(player.getName());
            playerLobby = new PlayerLobby(record);
            playerLobby.create();
        } else {
            record = data.getRecord(player.getName());
            playerLobby = new PlayerLobby(record);
        }
        if (record.getVersion() != info.getVersion()) {
            // if player lobby is old, update
            playerLobby.destroy();
            playerLobby.create();
            record.setVersion(info.getVersion());
            data.saveRecord(player.getName(), record);
        }
        player.teleport(playerLobby.transformFromLobbyCord(info.getSpawnPosition()).toLocation(world));
        playerLobby.load();
        activeLobbies.put(player.getName(), playerLobby);
    }

    public void playerLeft(Player player) {
        LobbyRecord record = data.getRecord(player.getName());
        PlayerLobby playerLobby = new PlayerLobby(record);
        playerLobby.unload();
        activeLobbies.remove(player.getName());
    }

    public LobbyRecord generateNewRecord(String playerName) {
        LobbyRecord record = new LobbyRecord();
        record.setOwner(playerName);
        record.setPosition(getNextLobbyPosition(data.getLastLobbyPosition()));
        record.setVersion(info.getVersion());
        data.saveRecord(playerName, record);
        data.updateLastLobbyPosition(record.getPosition());
        return record;
    }


    private Position getNextLobbyPosition(Position position) {
        int xIdx = (int) position.getX() / 96;
        int yIdx = (int) position.getZ() / 96;
        if (-yIdx < xIdx && xIdx < yIdx && yIdx > 0) {
            ++xIdx;
        } else
        if (-xIdx < yIdx && yIdx <= xIdx && xIdx > 0) {
            --yIdx;
        } else
        if (xIdx > yIdx && yIdx < 0) {
            --xIdx;
        } else {
            ++yIdx;
        }
        return new Position(xIdx * 96, 68 + Math.abs(new Random().nextInt()) % 20, yIdx * 96);
    }

    public static Lobby getInstance() {
        return instance;
    }

    public MonumentManager getMonumentManager() {
        return monumentManager;
    }

    public HashMap<String, PlayerLobby> getActiveLobbies() {
        return activeLobbies;
    }

    public LobbyData getData() {
        return data;
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
