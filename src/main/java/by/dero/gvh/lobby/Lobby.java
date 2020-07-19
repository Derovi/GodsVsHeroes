package by.dero.gvh.lobby;

import by.dero.gvh.AdviceManager;
import by.dero.gvh.Plugin;
import by.dero.gvh.PluginMode;
import by.dero.gvh.lobby.interfaces.CompassInterface;
import by.dero.gvh.lobby.interfaces.InterfaceManager;
import by.dero.gvh.lobby.monuments.ArmorStandMonument;
import by.dero.gvh.lobby.monuments.Monument;
import by.dero.gvh.lobby.monuments.MonumentManager;
import by.dero.gvh.model.Lang;
import by.dero.gvh.model.PlayerInfo;
import by.dero.gvh.model.ServerType;
import by.dero.gvh.model.StorageInterface;
import by.dero.gvh.model.storages.LocalStorage;
import by.dero.gvh.model.storages.MongoDBStorage;
import by.dero.gvh.utils.DataUtils;
import by.dero.gvh.utils.Position;
import by.dero.gvh.utils.ResourceUtils;
import by.dero.gvh.utils.VoidGenerator;
import com.google.gson.Gson;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import ru.cristalix.core.realm.IRealmService;
import ru.cristalix.core.realm.RealmInfo;
import ru.cristalix.core.realm.RealmStatus;

import java.util.*;

public class Lobby implements PluginMode, Listener {
    private static Lobby instance;
    private LobbyInfo info;
    private final String worldName = "Lobby";
    private World world;
    private final HashMap<String, PlayerLobby> activeLobbies = new HashMap<>();
    private MonumentManager monumentManager;
    private InterfaceManager interfaceManager;
    private PortalManager portalManager;
    private final List<BukkitRunnable> runnables = new ArrayList<>();
    private final HashMap<String, LobbyPlayer> players = new HashMap<>();
    private final HashSet<UUID> hidePlayers = new HashSet<>();
    
    @Override
    public void onEnable() {
        instance = this;
        Plugin.getInstance().getServerData().register(Plugin.getInstance().getSettings().getServerName(),
                ServerType.LOBBY, 300);
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
        world.setGameRuleValue("doDaylightCycle", "false");
        world.setGameRuleValue("doMobSpawning", "false");
        world.setGameRuleValue("doWeatherCycle", "false");

        world.setTime(1000);
        world.setStorm(false);
        world.setThundering(false);
        world.setDifficulty(Difficulty.PEACEFUL);
        for (Entity obj : world.getEntities()) {
            obj.remove();
        }
        StorageInterface dataStorage = new LocalStorage();
        if (Plugin.getInstance().getSettings().getLobbyDataStorageType().equals("mongodb")) {
            dataStorage = new MongoDBStorage(
                    Plugin.getInstance().getSettings().getLobbyDataMongodbConnection(),
                    Plugin.getInstance().getSettings().getLobbyDataMongodbDatabase());
        }
        monumentManager = new MonumentManager();
        monumentManager.load();
        interfaceManager = new InterfaceManager();
        portalManager = new PortalManager();
        registerEvents();
        if (Plugin.getInstance().getSettings().isCristalix()) {
            RealmInfo info = IRealmService.get().getCurrentRealmInfo();
            info.setStatus(RealmStatus.WAITING_FOR_PLAYERS);
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                world.save();
            }
        }.runTaskTimer(Plugin.getInstance(), 6000, 6000);
    }

    private void registerEvents() {
        Plugin p = Plugin.getInstance();
        Bukkit.getPluginManager().registerEvents(portalManager, p);
        Bukkit.getPluginManager().registerEvents(interfaceManager, p);
        Bukkit.getPluginManager().registerEvents(monumentManager, p);
        Bukkit.getPluginManager().registerEvents(new LobbyEvents(), p);
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
        monumentManager.unload();
        Plugin.getInstance().getServerData().unregister(Plugin.getInstance().getSettings().getServerName());
    }

    public void updateDisplays(Player player) {
        final PlayerLobby lobby = activeLobbies.get(player.getName());
        lobby.getScoreboardUpdater().run();
        //lobby.getSelectedClass().setText(Lang.get("lobby.selectedClass")
        //        .replace("%class%", Lang.get("classes." + players.get(player.getName()).getPlayerInfo().getSelectedClass())));
        final PlayerInfo info = Lobby.getInstance().getPlayers().get(player.getName()).getPlayerInfo();
        for (final Monument monument : Lobby.getInstance().getMonumentManager().getMonuments().values()) {
            if (monument instanceof ArmorStandMonument) {
                final ArmorStand armorStand = ((ArmorStandMonument) monument).getArmorStand();
                final String clname = Lang.get("classes." + monument.getClassName());
                final String customName;
                if (info.getSelectedClass().equals(monument.getClassName())) {
                    customName = Lang.get("lobby.heroSelected").
                            replace("%class%", clname);
                } else
                if (info.isClassUnlocked(monument.getClassName())) {
                    customName = Lang.get("lobby.standTitle").
                            replace("%class%", clname);
                } else {
                    customName = Lang.get("lobby.heroLocked").
                            replace("%class%", clname);
                }
            }
        }
    }

    private static ItemStack compassitem;
    private static ItemStack hideitem;
    private static ItemStack showitem;
    public void playerJoined(Player player) {
        player.getInventory().setHeldItemSlot(0);
        LobbyPlayer lobbyPlayer = new LobbyPlayer(player);
        lobbyPlayer.loadInfo();
        players.put(player.getName(), lobbyPlayer);
        new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1).apply(player);
        new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 0).apply(player);
        PlayerLobby playerLobby = new PlayerLobby(player);

        player.teleport(info.getSpawnPosition().toLocation(world));
        playerLobby.load();
        activeLobbies.put(player.getName(), playerLobby);
        Lobby.getInstance().updateDisplays(player);

        for (Player other : Bukkit.getOnlinePlayers()) {
            if (hidePlayers.contains(player.getUniqueId())) {
                player.hidePlayer(Plugin.getInstance(), other);
            } else {
                player.showPlayer(Plugin.getInstance(), other);
            }
            if (hidePlayers.contains(other.getUniqueId())) {
                other.hidePlayer(Plugin.getInstance(), player);
            } else {
                other.showPlayer(Plugin.getInstance(), player);
            }
        }
        
        player.getInventory().clear();
        if (compassitem == null) {
            compassitem = new ItemStack(Material.COMPASS);
            ItemMeta meta = compassitem.getItemMeta();
            meta.setDisplayName(Lang.get("lobby.compass"));
            compassitem.setItemMeta(meta);
            
            showitem = new ItemStack(Material.EYE_OF_ENDER);
            ItemMeta meta1 = showitem.getItemMeta();
            meta1.setDisplayName(Lang.get("lobby.showPlayers"));
            showitem.setItemMeta(meta1);
            
            hideitem = new ItemStack(Material.ENDER_PEARL);
            ItemMeta meta2 = hideitem.getItemMeta();
            meta2.setDisplayName(Lang.get("lobby.hidePlayers"));
            hideitem.setItemMeta(meta2);
        }
        player.getInventory().setItem(0, compassitem);
        if (hidePlayers.contains(player.getUniqueId())) {
            player.getInventory().setItem(8, showitem);
        } else {
            player.getInventory().setItem(8, hideitem);
        }
        AdviceManager.sendAdvice(player, "unlockClass", 30, 400,
                (pl) -> (!players.containsKey(pl.getName()) || players.get(pl.getName()).getPlayerInfo().getClasses().size() > 1));

        AdviceManager.sendAdvice(player, "startGame", 30, 400,
                (pl) -> (!players.containsKey(pl.getName())),
                (pl) -> (players.get(pl.getName()).getPlayerInfo().getClasses().size() > 1));
    }

    public void playerLeft(Player player) {
        activeLobbies.get(player.getName()).unload();
        activeLobbies.remove(player.getName());
        players.remove(player.getName());
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

    public LobbyInfo getInfo() {
        return info;
    }

    public String getWorldName() {
        return worldName;
    }

    public World getWorld() {
        return world;
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
                p.setVelocity(new Vector(-2,0,0));
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
    public void onPlayerToggleFlight (final PlayerToggleFlightEvent event) {
        final Player p = event.getPlayer();
        if (p.getGameMode () == GameMode.SURVIVAL) {
            p.setAllowFlight (false);
            p.setVelocity(p.getVelocity().add(new Vector(0,1,0)));
            event.setCancelled (true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction().equals(Action.RIGHT_CLICK_AIR) ||
            event.getAction().equals(Action.RIGHT_CLICK_BLOCK) &&
            player.getInventory().getHeldItemSlot() == 0) {
            switch (event.getPlayer().getInventory().getHeldItemSlot()) {
                case 0:
                    CompassInterface compassInterface = new CompassInterface(
                            Lobby.getInstance().getInterfaceManager(), player);
                    compassInterface.open();
                    break;
                case 8:
                    if (hidePlayers.contains(player.getUniqueId())) {
                        hidePlayers.remove(player.getUniqueId());
                        player.getInventory().setItem(8, hideitem);
                        for (Player other : Bukkit.getOnlinePlayers()) {
                            player.showPlayer(Plugin.getInstance(), other);
                        }
                    } else {
                        hidePlayers.add(player.getUniqueId());
                        player.getInventory().setItem(8, showitem);
                        for (Player other : Bukkit.getOnlinePlayers()) {
                            player.hidePlayer(Plugin.getInstance(), other);
                        }
                    }
                    break;
            }
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak (BlockBreakEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onThunderChange(ThunderChangeEvent e) {
        if (e.toThunderState()) {
            e.setCancelled(true);
        }
    }



    @EventHandler
    public void onWeatherChange(WeatherChangeEvent e) {
        if (e.toWeatherState()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void removeSwapHand(PlayerSwapHandItemsEvent event) {
        event.setCancelled(true);
    }
}
