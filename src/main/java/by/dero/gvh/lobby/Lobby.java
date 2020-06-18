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
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.*;

public class Lobby implements PluginMode, Listener {
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

        for (Entity obj : world.getEntities()) {
            obj.remove();
        }
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
    public void onDisable() {
        for (PlayerLobby playerLobby : activeLobbies.values()) {
            playerLobby.unload();
        }
    }

    private void loadSchematic() {
        lobbySchematicFile = new File(LocalStorage.getPrefix() + "lobby/lobby.schem");
        try {
            ResourceUtils.exportResource("/lobby/lobby.schem", LocalStorage.getPrefix() + "lobby/lobby.schem");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void playerJoined(Player player) {
        new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0).apply(player);
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
        final Position recPos = record.getPosition();
        new BukkitRunnable() {
            double angle = 0;
            final double turnsPerSec = 0.25;
            final double radius = 1.2;
            final int parts = 3;
            final Location center = recPos.toLocation(world).clone().add(15.5,1.5,29.5);
            @Override
            public void run() {
                if (!player.isOnline()) {
                    this.cancel();
                }
                for (int i = 0; i < parts; i++) {
                    final double cur = angle + Math.PI * 2 * i / parts;
                    final Location at = center.clone().add(Math.cos(cur) * radius, Math.sin(cur) * radius,0);
                    world.spawnParticle(Particle.FLAME, at, 0, 0, 0, 0);
                }
                angle += Math.PI * turnsPerSec / 20 * 2;
            }
        }.runTaskTimer(Plugin.getInstance(), 0, 2);

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
        activeLobbies.get(player.getName()).unload();
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


    private final HashMap<UUID, Location> onGround = new HashMap<>();
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player p = event.getPlayer();
        if (p.isOnGround()) {
            onGround.put(p.getUniqueId(), p.getLocation());
        } else {
            if (p.getLocation().getY() < 30) {
                p.teleport(onGround.get(p.getUniqueId()));
            }
        }

    }
}
