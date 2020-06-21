package by.dero.gvh.lobby;

import by.dero.gvh.Plugin;
import by.dero.gvh.PluginMode;
import by.dero.gvh.lobby.interfaces.InterfaceManager;
import by.dero.gvh.lobby.monuments.ArmorStandMonument;
import by.dero.gvh.lobby.monuments.Monument;
import by.dero.gvh.lobby.monuments.MonumentManager;
import by.dero.gvh.minigame.Game;
import by.dero.gvh.model.PlayerInfo;
import by.dero.gvh.utils.VoidGenerator;
import by.dero.gvh.model.Lang;
import by.dero.gvh.model.ServerType;
import by.dero.gvh.model.StorageInterface;
import by.dero.gvh.model.storages.LocalStorage;
import by.dero.gvh.model.storages.MongoDBStorage;
import by.dero.gvh.utils.DataUtils;
import by.dero.gvh.utils.Position;
import by.dero.gvh.utils.ResourceUtils;
import com.google.gson.Gson;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.*;

import static by.dero.gvh.utils.DataUtils.getPlayer;

public class Lobby implements PluginMode, Listener {
    private static Lobby instance;
    private LobbyInfo info;
    private LobbyData data;
    private File lobbySchematicFile;
    private final String worldName = "Lobby";
    private World world;
    private final HashMap<String, PlayerLobby> activeLobbies = new HashMap<>();
    private MonumentManager monumentManager;
    private InterfaceManager interfaceManager;
    private PortalManager portalManager;
    private final List<BukkitRunnable> runnables = new ArrayList<>();
    private final HashMap<String, LobbyPlayer> players = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;
        Plugin.getInstance().getServerData().register(Plugin.getInstance().getSettings().getServerName(),
                ServerType.LOBBY);
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
        world.setTime(1000);
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        world.setStorm(false);
        world.setThundering(false);
        world.setDifficulty(Difficulty.PEACEFUL);
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
        interfaceManager = new InterfaceManager();
        portalManager = new PortalManager();
        Bukkit.getPluginManager().registerEvents(portalManager, Plugin.getInstance());
        Bukkit.getPluginManager().registerEvents(interfaceManager, Plugin.getInstance());
        Bukkit.getPluginManager().registerEvents(monumentManager, Plugin.getInstance());
        Bukkit.getPluginManager().registerEvents(new LobbyEvents(), Plugin.getInstance());
        System.out.println("Loading schematic");
        loadSchematic();
    }

    @Override
    public void onDisable() {
        for (final BukkitRunnable runnable : runnables) {
            runnable.cancel();
        }
        runnables.clear();
        for (final PlayerLobby playerLobby : activeLobbies.values()) {
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

    public void updateDisplays(Player player) {
        final PlayerLobby lobby = activeLobbies.get(player.getName());
        lobby.getScoreboardUpdater().run();
        lobby.getSelectedClass().setText(Lang.get("lobby.selectedClass")
                .replace("%class%", Lang.get("classes." + players.get(player.getName()).getPlayerInfo().getSelectedClass())));
        final PlayerInfo info = Lobby.getInstance().getPlayers().get(player.getName()).getPlayerInfo();
        for (final Monument monument : lobby.getMonuments().values()) {
            if (monument instanceof ArmorStandMonument) {
                final ArmorStand armorStand = ((ArmorStandMonument) monument).getArmorStand();
                final String clname = Lang.get("classes." + monument.getClassName());

                if (info.getSelectedClass().equals(monument.getClassName())) {
                    armorStand.setCustomName(Lang.get("lobby.heroSelected").
                            replace("%class%", clname));
                } else
                if (info.isClassUnlocked(monument.getClassName())) {
                    armorStand.setCustomName(Lang.get("lobby.standTitle").
                            replace("%class%", clname));
                } else {
                    armorStand.setCustomName(Lang.get("lobby.heroLocked").
                            replace("%class%", clname));
                }
            }
        }
    }

    public void playerJoined(Player player) {
        LobbyPlayer lobbyPlayer = new LobbyPlayer(player);
        lobbyPlayer.loadInfo();
        players.put(player.getName(), lobbyPlayer);
        new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1).apply(player);
        new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 0).apply(player);
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
        Lobby.getInstance().updateDisplays(player);
    }

    public void playerLeft(Player player) {
        activeLobbies.get(player.getName()).unload();
        activeLobbies.remove(player.getName());
        players.remove(player.getName());
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

    public InterfaceManager getInterfaceManager() {
        return interfaceManager;
    }

    public MonumentManager getMonumentManager() {
        return monumentManager;
    }

    public HashMap<String, PlayerLobby> getActiveLobbies() {
        return activeLobbies;
    }

    public HashMap<String, LobbyPlayer> getPlayers() {
        return players;
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
    public void onPlayerMove(final PlayerMoveEvent event) {
        final Player p = event.getPlayer();
        if (!p.getAllowFlight()) {
            groundUpdate(p);
        }

        if (p.isOnGround()) {
            onGround.put(p.getUniqueId(), p.getLocation());
            if (p.getLocation().clone().subtract(0,1,0).getBlock().getType() == Material.GOLD_BLOCK) {
                p.setVelocity(new Vector(0,0,2));
            }
        } else {
            if (p.getLocation().getY() < 30) {
                p.teleport(onGround.get(p.getUniqueId()));
            }
        }
    }

    @EventHandler
    public void removeDamage(EntityDamageEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void infiniteFood(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    private void groundUpdate (final Player player) {
        final Block block = player.getLocation().clone().subtract(0,1,0).getBlock();;
        if (block.getType ().isSolid ()) {
            player.setAllowFlight (true);
        }
    }

    @EventHandler (priority = EventPriority.HIGH)
    public void onPlayerDamage (final EntityDamageEvent event) {
        if (event.getEntityType () == EntityType.PLAYER &&
                event.getCause () == EntityDamageEvent.DamageCause.FALL) {
            event.setCancelled (true);
        }
    }

    @EventHandler (priority = EventPriority.HIGH)
    public void onPlayerToggleFlight (final PlayerToggleFlightEvent event) {
        final Player p = event.getPlayer();
        if (p.getGameMode () == GameMode.SURVIVAL) {
            p.setAllowFlight (false);
            p.setVelocity(p.getVelocity().add(new Vector(0,1,0)));
            event.setCancelled (true);
        }
    }
}
