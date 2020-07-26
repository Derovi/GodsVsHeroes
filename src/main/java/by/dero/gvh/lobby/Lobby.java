package by.dero.gvh.lobby;

import by.dero.gvh.AdviceManager;
import by.dero.gvh.Plugin;
import by.dero.gvh.PluginMode;
import by.dero.gvh.books.GameStatsBook;
import by.dero.gvh.lobby.interfaces.CompassInterface;
import by.dero.gvh.lobby.interfaces.CosmeticSelectorInterface;
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
import by.dero.gvh.stats.GameStats;
import by.dero.gvh.stats.PlayerStats;
import by.dero.gvh.utils.*;
import com.google.gson.Gson;
import net.minecraft.server.v1_12_R1.EnumItemSlot;
import net.minecraft.server.v1_12_R1.PacketPlayOutEntityEquipment;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
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
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import ru.cristalix.core.CoreApi;
import ru.cristalix.core.IPlatformEventExecutor;
import ru.cristalix.core.IServerPlatform;
import ru.cristalix.core.display.data.DataDrawData;
import ru.cristalix.core.display.data.StringDrawData;
import ru.cristalix.core.math.V2;
import ru.cristalix.core.math.V3;
import ru.cristalix.core.realm.IRealmService;
import ru.cristalix.core.realm.RealmInfo;
import ru.cristalix.core.realm.RealmStatus;
import ru.cristalix.core.render.IRenderService;
import ru.cristalix.core.render.VisibilityTarget;
import ru.cristalix.core.render.WorldRenderData;

import java.util.*;

public class Lobby implements PluginMode, Listener {
    private static Lobby instance;
    private LobbyInfo info;
    private final String worldName = "Lobby";
    private World world;
    private final HashMap<String, PlayerLobby> activeLobbies = new HashMap<>();
    private final static ItemFunc[] activates = new ItemFunc[9];
    private MonumentManager monumentManager;
    private InterfaceManager interfaceManager;
    private PortalManager portalManager;
    private final List<BukkitRunnable> runnables = new ArrayList<>();
    private final HashMap<String, LobbyPlayer> players = new HashMap<>();
    private final HashSet<UUID> hidePlayers = new HashSet<>();
    private final HashMap<Player, Long> hideShowUsed = new HashMap<>();
    
    @FunctionalInterface
    private interface ItemFunc {
        void run(Player player);
    }
    
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

        IPlatformEventExecutor<Object, Object, Object> eventExecutor = IServerPlatform.get().getPlatformEventExecutor();
        /*eventExecutor.registerListener(PlayerJoinEvent.class, this, (e) -> {
            //for (Player p : Bukkit.getOnlinePlayers()) {
            Player p = e.getPlayer();
                if (p.isOnline()) {
                    Position pos = new Position(p.getLocation());
                    pos.add(0, 2, 0);
                    List<DataDrawData> dataList = new ArrayList<>();
                    dataList.add(DataDrawData.builder()
                            .strings(
                                    Collections.singletonList(StringDrawData.builder().string(p.getName()).position(new V2(10, 1)).scale(4).build())
                            ).position(new V3(pos.getX(), pos.getY(), pos.getZ()))
                            .dimensions(new V2(10, 1))
                            .rotation(90)
                            .scale(1.7)
                            .build());
                    IDisplayService.get().sendWorld(p.getUniqueId(), new WorldRenderMessage(dataList));
                }
            //}
        }, EventPriority.MONITOR, false);*/
        eventExecutor.registerListener(PlayerChangedWorldEvent.class, this, (e) -> {
            Player p = e.getPlayer();
            updateHolograms(p);
           // }
        }, EventPriority.MONITOR, true);
        
        initItems();
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

    public void updateHolograms(Player player) {
        /*List<DataDrawData> dataList = new ArrayList<>();
        LobbyPlayer lp = players.get(player.getName());
        {   // title under portal
            Position pos = info.getPortalPosition().clone();
            pos.add(0, 2.5, 1);
            //"Выбран: " + Lang.get("classes." + lp.getPlayerInfo().getSelectedClass())
            dataList.add(DataDrawData.builder()
                    .rotation(270)
                    .strings(
                            Arrays.asList(StringDrawData.builder().align(2).string("Выбран класс:").scale(2).
                                    position(new V2(60, 10)).build(),
                                    StringDrawData.builder().align(2).string("§6" + Lang.get("classes." + lp.getPlayerInfo().getSelectedClass())).scale(2).
                                            position(new V2(100, 50)).build())
                    ).position(new V3(pos.getX(), pos.getY(), pos.getZ()))
                    .dimensions(new V2(2, 1))
                    .scale(1)
                    .build());
        }

        IDisplayService.get().sendWorld(player.getUniqueId(), new WorldRenderMessage(dataList));*/
    }

    public void updateDisplays(Player player) {
        final PlayerLobby lobby = activeLobbies.get(player.getName());
        lobby.getScoreboardUpdater().run();
        updateHolograms(player);
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

        /*IPlatformEventExecutor<Object, Object, Object> eventExecutor = IServerPlatform.get().getPlatformEventExecutor();
        eventExecutor.registerListener(PlayerJoinEvent.class, this, (e) -> {
            Player p = e.getPlayer();
            if (p.isOnline()) {
                System.out.println("Render: " + dataList.size());
                IDisplayService.get().sendWorld(p.getUniqueId(), new WorldRenderMessage(dataList));
            }

        }, EventPriority.MONITOR, false);
        eventExecutor.registerListener(PlayerChangedWorldEvent.class, this, (e) -> {
            System.out.println("Remder: " + dataList.size());
            Player p = e.getPlayer();
            IDisplayService.get().sendWorld(p.getUniqueId(), new WorldRenderMessage(dataList));
        }, EventPriority.MONITOR, true);
*/

        /*for (Player pl : Bukkit.getOnlinePlayers()) {
            IDisplayService.get().sendWorld(player.getUniqueId(), new WorldRenderMessage(dataList));
        }*/
        /*new BukkitRunnable() {
            @Override
            public void run() {

            }
        }.runTaskLater(Plugin.getInstance(), 1);*/


        //createText(player.getLocation().add(0, 2, 0), "Лесопилка",
//                90, 10, 1, 1.7);
    }

    public static void createText(Location loc, String text, int rotation, double x, double y, double scale){
        String name = UUID.randomUUID().toString();
        IRenderService render = CoreApi.get().getService(IRenderService.class);
        render.createGlobalWorldRenderData(loc.getWorld().getUID(), name, WorldRenderData.builder()
                .name("SomeTextHologram").visibilityTarget(VisibilityTarget.BLACKLIST).dataDrawData(
                        DataDrawData.builder()
                                .strings(
                                        Collections.singletonList(StringDrawData.builder().string(text).position(new V2(x, y)).scale(4).build())
                                ).position(new V3(loc.getX(), loc.getY(), loc.getZ()))
                                .dimensions(new V2(x,y))
                                .rotation(rotation)
                                .scale(scale)
                                .build()
                )
                .build());
        render.setRenderVisible(loc.getWorld().getUID(), name, true);
    }

    private static List<StringDrawData> getStrings() {
        return Collections.singletonList(
                StringDrawData.builder().align(1).scale(4).string("test").position(new V2(135, 10)).build()
        );
    }
    
    private static ItemStack compassitem = null;
    private static ItemStack cosmeticitem = null;
    private static ItemStack hideitem = null;
    private static ItemStack showitem = null;
    private static ItemStack statItem = null;
    
    private void initItems() {
        compassitem = new ItemStack(Material.COMPASS);
        ItemMeta meta = compassitem.getItemMeta();
        meta.setDisplayName(Lang.get("lobby.compass"));
        compassitem.setItemMeta(meta);
        activates[0] = player -> {
            CompassInterface compassInterface = new CompassInterface(
                    Lobby.getInstance().getInterfaceManager(), player);
            compassInterface.open();
        };
    
        showitem = new ItemStack(Material.EYE_OF_ENDER);
        meta = showitem.getItemMeta();
        meta.setDisplayName(Lang.get("lobby.showPlayers"));
        showitem.setItemMeta(meta);
    
        hideitem = new ItemStack(Material.ENDER_PEARL);
        meta = hideitem.getItemMeta();
        meta.setDisplayName(Lang.get("lobby.hidePlayers"));
        hideitem.setItemMeta(meta);
        activates[8] = player -> {
            if (System.currentTimeMillis() - hideShowUsed.getOrDefault(player, 0L) < 3000) {
                return;
            }
            hideShowUsed.put(player, System.currentTimeMillis());
            player.setCooldown(Material.EYE_OF_ENDER, 60);
            player.setCooldown(Material.ENDER_PEARL, 60);
            player.playSound(player.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 1, 1);
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
        };
        
        cosmeticitem = new ItemStack(Material.EMERALD);
        meta = cosmeticitem.getItemMeta();
        meta.setDisplayName(Lang.get("lobby.cosmetics"));
        cosmeticitem.setItemMeta(meta);
    
        activates[4] = player -> {
            player.playSound(player.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
            CosmeticSelectorInterface inter = new CosmeticSelectorInterface(interfaceManager, player);
            inter.open();
        };
        
        statItem = new ItemStack(Material.ENCHANTED_BOOK);
        meta = statItem.getItemMeta();
        meta.setDisplayName(Lang.get("lobby.lastGame"));
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        statItem.setItemMeta(meta);
        activates[1] = player -> {
            PlayerStats playerStats = Plugin.getInstance().getGameStatsData().getPlayerStats(player.getName());
            if (playerStats != null && !playerStats.getGames().isEmpty()) {
                int id = playerStats.getGames().get(playerStats.getGames().size() - 1);
                GameStats gameStats = Plugin.getInstance().getGameStatsData().getGameStats(id);
                GameStatsBook gameStatsBook = new GameStatsBook(Plugin.getInstance().getBookManager(),
                        player, player.getName(), gameStats);
                gameStatsBook.build();
                gameStatsBook.open();
            }
        };
    }
    
    public void playerJoined(Player player) {
        PlayerInventory inv = player.getInventory();
        inv.setHeldItemSlot(0);
        player.setGameMode(GameMode.ADVENTURE);
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
        
        for (Monument monument : monumentManager.getMonuments().values()) {
            if (monument instanceof ArmorStandMonument) {
                ArmorStand stand = ((ArmorStandMonument) monument).getArmorStand();
                ItemStack weapon = GameUtils.getMeleeWeapon(player, monument.getClassName());
//                stand.setItemInHand(weapon);
                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(
                        new PacketPlayOutEntityEquipment(stand.getEntityId(), EnumItemSlot.MAINHAND, CraftItemStack.asNMSCopy(weapon)));
            }
        }

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
        
        inv.clear();
        inv.setItem(0, compassitem);
        PlayerStats playerStats = Plugin.getInstance().getGameStatsData().getPlayerStats(player.getName());
        if (playerStats != null && !playerStats.getGames().isEmpty()) {
            inv.setItem(1, statItem);
        }
        inv.setItem(4, cosmeticitem);
        if (hidePlayers.contains(player.getUniqueId())) {
            inv.setItem(8, showitem);
        } else {
            inv.setItem(8, hideitem);
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
                p.setVelocity(p.getLocation().getDirection().multiply(2).setY(0));
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
        if (p.getGameMode () != GameMode.CREATIVE) {
            p.setAllowFlight (false);
            p.setVelocity(p.getVelocity().add(new Vector(0,1,0)));
            event.setCancelled (true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction().equals(Action.PHYSICAL)) {
            event.setCancelled(true);
            return;
        }
        if (event.getAction().equals(Action.RIGHT_CLICK_AIR) ||
            event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            int slot = player.getInventory().getHeldItemSlot();
            if (activates[slot] != null) {
                activates[slot].run(player);
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
    public void onBlockBreak(BlockBreakEvent event) {
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
