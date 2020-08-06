package by.dero.gvh.lobby;

import by.dero.gvh.AdviceManager;
import by.dero.gvh.FlyingText;
import by.dero.gvh.Plugin;
import by.dero.gvh.PluginMode;
import by.dero.gvh.books.GameStatsBook;
import by.dero.gvh.lobby.interfaces.CompassInterface;
import by.dero.gvh.lobby.interfaces.DonateSelectorInterface;
import by.dero.gvh.lobby.interfaces.InterfaceManager;
import by.dero.gvh.lobby.interfaces.cosmetic.BuyCosmeticInterface;
import by.dero.gvh.lobby.monuments.DonatePackChest;
import by.dero.gvh.lobby.monuments.MonumentManager;
import by.dero.gvh.lobby.monuments.Totem;
import by.dero.gvh.model.*;
import by.dero.gvh.model.storages.LocalStorage;
import by.dero.gvh.model.storages.MongoDBStorage;
import by.dero.gvh.stats.GameStats;
import by.dero.gvh.stats.IntTopEntry;
import by.dero.gvh.stats.PlayerStats;
import by.dero.gvh.utils.*;
import com.google.gson.Gson;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
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
import org.bukkit.util.EulerAngle;
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
    @Getter private static Lobby instance;
    @Getter private LobbyInfo info;
    @Getter private final String worldName = "Lobby";
    @Getter private World world;
    @Getter private final HashMap<String, PlayerLobby> activeLobbies = new HashMap<>();
    private final static PlayerRunnable[] activates = new PlayerRunnable[9];
    @Getter private MonumentManager monumentManager;
    @Getter private InterfaceManager interfaceManager;
    @Getter
    private TopManager topManager;
    private PortalManager portalManager;
    private final List<BukkitRunnable> runnables = new ArrayList<>();
    @Getter private final HashMap<String, LobbyPlayer> players = new HashMap<>();
    private final HashSet<UUID> hidePlayers = new HashSet<>();
    private final HashMap<Player, Long> hideShowUsed = new HashMap<>();
    @Getter private DonatePackChest chest;
    @Getter private Totem totem;
    @Getter private CristallixTop expTop;
    
    @Override
    public void onEnable() {
        instance = this;
        Plugin.getInstance().getServerData().register(Plugin.getInstance().getSettings().getServerName(),
                ServerType.LOBBY, 300);
        if (Plugin.getInstance().getSettings().getServerName().startsWith("EW")) {
            RealmInfo info = IRealmService.get().getCurrentRealmInfo();
            info.setLobbyServer(true);
            info.setServicedServers(new String[]{"EWP"});
        } else {
            //IRealmService.get().getCurrentRealmInfo().setLobbyServer(true);
        }

        topManager = new TopManager();

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
        
        chest = new DonatePackChest(info.getDonateChest());
        Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
            for (Map.Entry<String, DirectedPosition> banner : info.getCosmeticToBanner().entrySet()) {
                spawnBanner(banner.getKey(), banner.getValue());
            }
        }, 1);
        
        totem = new Totem(info.getDailyTotem().toLocation(world));
        new FlyingText(info.getDailyTotem().toLocation(world), Lang.get("interfaces.totem"));
        
        Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
            expTop = new CristallixTop(() -> {
                List<IntTopEntry> from = topManager.getTop();
                ArrayList<Pair<String, String> > top = new ArrayList<>(from.size());
                for (IntTopEntry intTopEntry : from) {
                    top.add(Pair.of(intTopEntry.getName(), String.valueOf(new PlayerLevel(intTopEntry.getValue()).getLevel())));
                }
                return top;
            }, "Игрок", "Опыт", "Топ игроков по опыту", 175, 0, info.getTopsPositions().get("exp").toV3(), world.getUID());
            Bukkit.getScheduler().runTaskTimer(Plugin.getInstance(), expTop::update, 2, 100);
        }, 100);
    }
    
    
    private final HashMap<Vector, PlayerRunnable> blockRunnables = new HashMap<>();
    private void spawnBanner(String name, DirectedPosition pos) {
        Location loc = pos.toLocation(world);
        world.getBlockAt(loc.clone().add(0, 2, -pos.getDz())).setType(Material.GLOWSTONE);
        world.getBlockAt(loc.clone().add(0, 1, -pos.getDz())).setType(Material.DARK_OAK_FENCE);
        world.getBlockAt(loc.clone().add(0, 0, -pos.getDz())).setType(Material.WOOD);
        world.getBlockAt(loc.clone().add(0, 0, -pos.getDz())).setData((byte) 5);
        world.getBlockAt(loc.clone().add(0, 2, 0)).setType(Material.WALL_BANNER);
    
        CraftArmorStand invStand = (CraftArmorStand) world.spawnEntity(
                loc.clone().add(0.5, 1, 0.5), EntityType.ARMOR_STAND);
        GameUtils.setInvisibleFlags(invStand);
        
        CraftArmorStand stand;
        world.getBlockAt(loc).setType(Material.WALL_SIGN);
        if (pos.getDz() < 0) {
            stand = (CraftArmorStand) world.spawnEntity(loc.clone().add(-0.2, -0.1, 0.7), EntityType.ARMOR_STAND);
            world.getBlockAt(loc.clone().add(0, 2, 0)).setData((byte) 2);
            world.getBlockAt(loc).setData((byte) 2);
        } else {
            stand = (CraftArmorStand) world.spawnEntity(loc.clone().add(1.2, -0.1, 0.3), EntityType.ARMOR_STAND);
            world.getBlockAt(loc.clone().add(0, 2, 0)).setData((byte) 3);
            world.getBlockAt(loc).setData((byte) 3);
        }
        if (!name.equals("fairySword")) {
            stand.setHeadPose(new EulerAngle(0, 0, -Math.PI / 4));
        } else {
            stand.getHandle().locX += 0.7;
        }
        CosmeticInfo item = Plugin.getInstance().getCosmeticManager().getCustomizations().get(name);
        Sign sign = (Sign) world.getBlockAt(loc).getState();
        sign.setLine(0, item.getDisplayName());
        sign.setLine(1, "§6" + Lang.get("classes." + item.getHero()));
        sign.setLine(3, "§6[Пкм - открыть]");
        sign.update();
        
        stand.setHelmet(item.getItemStack(true));
        GameUtils.setInvisibleFlags(stand);
        PlayerRunnable onClick = (p) -> {
            PlayerInfo info = Plugin.getInstance().getPlayerData().getPlayerInfo(p.getName());
            if (info.getCosmetics().containsKey(name)) {
                p.sendMessage(Lang.get("cosmetic.alreadyUnlocked"));
            } else {
                BuyCosmeticInterface inter = new BuyCosmeticInterface(interfaceManager, p, name);
                inter.open();
            }
        };
        blockRunnables.put(loc.toBlockLocation().toVector(), onClick);
        monumentManager.getOnClick().put(invStand.getUniqueId(), onClick);
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
        chest.unload();
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
                    interfaceManager, player);
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
        
        cosmeticitem = new ItemStack(Material.ENDER_CHEST);
        meta = cosmeticitem.getItemMeta();
        meta.setDisplayName(Lang.get("lobby.donate"));
        cosmeticitem.setItemMeta(meta);
    
        activates[4] = player -> {
            player.playSound(player.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
            DonateSelectorInterface inter = new DonateSelectorInterface(interfaceManager, player);
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
        players.put(player.getName(), lobbyPlayer);
        new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1).apply(player);
        new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 0).apply(player);
        PlayerLobby playerLobby = new PlayerLobby(player);

        player.teleport(info.getSpawnPosition().toLocation(world));
        playerLobby.load();
        activeLobbies.put(player.getName(), playerLobby);
        updateDisplays(player);

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
        
//        AdviceManager.sendAdvice(player, "unlockClass", 30, 400,
//                (pl) -> (!players.containsKey(pl.getName()) ||
//                        Plugin.getInstance().getPlayerData().getPlayerInfo(player.getName()).getClasses().size() > 1));

        AdviceManager.sendAdvice(player, "startGame", 30, 400,
                (pl) -> (!players.containsKey(pl.getName())));
    }

    public void updateItems(Player player) {
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            if (i >= activates.length || activates[i] == null) {
                ItemStack item = player.getInventory().getItem(i);
                if (item != null && item.getType() != Material.AIR) {
                    player.getInventory().removeItem(player.getInventory().getItem(i));
                }
            }
        }
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
    
    @EventHandler (priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction().equals(Action.PHYSICAL)) {
            event.setCancelled(true);
            return;
        }
        if (event.getAction().equals(Action.RIGHT_CLICK_AIR) ||
            event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (event.getClickedBlock() != null) {
                PlayerRunnable runnable = blockRunnables.getOrDefault(event.getClickedBlock().getLocation().toVector(), null);
                if (runnable != null) {
                    runnable.run(player);
                    event.setCancelled(true);
                    return;
                }
            }
            
            int slot = player.getInventory().getHeldItemSlot();
            if (event.getClickedBlock() != null && event.getClickedBlock().getType().equals(Material.ENDER_CHEST)) {
                DonateSelectorInterface inter = new DonateSelectorInterface(interfaceManager, player);
                inter.open();
                event.setCancelled(true);
            } else
            if (activates[slot] != null) {
                activates[slot].run(player);
                event.setCancelled(true);
            }
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
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInv(InventoryClickEvent event) {
        if (event.getAction() != InventoryAction.PLACE_ALL &&
                event.getAction() != InventoryAction.PICKUP_ALL &&
                event.getAction() != InventoryAction.SWAP_WITH_CURSOR) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryMove(InventoryMoveItemEvent event) {
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
