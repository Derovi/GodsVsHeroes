package by.dero.gvh.minigame;

import by.dero.gvh.*;
import by.dero.gvh.model.*;
import by.dero.gvh.model.interfaces.DoubleSpaceInterface;
import by.dero.gvh.stats.GameStats;
import by.dero.gvh.stats.GameStatsManager;
import by.dero.gvh.utils.*;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import ru.cristalix.core.build.BuildWorldState;
import ru.cristalix.core.build.models.Point;
import ru.cristalix.core.display.IDisplayService;
import ru.cristalix.core.display.enums.EnumPosition;
import ru.cristalix.core.display.enums.EnumUpdateType;
import ru.cristalix.core.display.messages.ProgressMessage;
import ru.cristalix.core.map.BukkitWorldLoader;
import ru.cristalix.core.map.IMapService;
import ru.cristalix.core.map.LoadedMap;
import ru.cristalix.core.realm.IRealmService;
import ru.cristalix.core.realm.RealmInfo;
import ru.cristalix.core.realm.RealmStatus;

import java.util.*;

public abstract class Game implements Listener {
    public enum State {
        GAME, FINISHING, WAITING, PREPARING, GAME_FULL
    }

    public Game(GameInfo info) {
        this.info = info;
        instance = this;
        GameEvents.setGame(this);
    }

    @Getter private static Game instance;
    @Getter private GameLobby lobby;
    private AfterParty afterParty;
    @Getter private final GameInfo info;
    @Getter private State state;
    @Getter private World world = null;
    @Getter private final HashMap<String, GamePlayer> players = new HashMap<>();
    @Getter private HashMap<UUID, GameMob> mobs;
    @Getter private final HashMap<String, Location> playerDeathLocations = new HashMap<>();
    @Getter private RewardManager rewardManager;
    @Getter private MapManager mapManager;
    private DeathAdviceManager deathAdviceManager;
    @Getter private GameStatsManager gameStatsManager;
    @Getter private final HashMap<GamePlayer, Double> boosterMult = new HashMap<>();
    @Getter private final HashSet<GamePlayer> boosterTeamSpent = new HashSet<>();
    @Getter private final HashSet<GamePlayer> boosterGlobalSpent = new HashSet<>();
    @Getter @Setter
    private GameStats stats;
    protected boolean loaded = false;

    public LinkedList<BukkitRunnable> getRunnables() {
        return runnables;
    }

    private final LinkedList<BukkitRunnable> runnables = new LinkedList<>();

    protected void onPlayerRespawned(final GamePlayer gp) { }

    public void prepareMap(String mapName) {
        LoadedMap<World> map = IMapService.get().
                loadMap(IMapService.get().
                        getMapByGameTypeAndMapName("EtherWar",mapName).get().getLatest(), BukkitWorldLoader.INSTANCE).join();
        world = map.getWorld();

        WorldUtils.prepareWorld(world);

        BuildWorldState state = map.getBuildWorldState();
        List<List<DirectedPosition>> positions = new ArrayList<>();
        for (int idx = 0; idx < info.getTeamCount(); ++idx) {
            positions.add(new ArrayList<>());
        }
        for (Point point : state.getPoints().get("team")) {
            String[] tag = point.getTag().split(",");
            DirectedPosition position = new DirectedPosition(point.getV3().getX(), point.getV3().getY(), point.getV3().getZ(),
                    new Vector(Double.parseDouble(tag[1]), Double.parseDouble(tag[2]), Double.parseDouble(tag[3])));
            positions.get(Integer.parseInt(tag[0]) - 1).add(position);
        }
        info.setSpawnPoints(new DirectedPosition[info.getTeamCount()][]);
        for (int idx = 0; idx < info.getTeamCount(); ++idx) {
            info.getSpawnPoints()[idx] = new DirectedPosition[positions.get(idx).size()];
            for (int i = 0; i < positions.get(idx).size(); ++i) {
                info.getSpawnPoints()[idx][i] = positions.get(idx).get(i);
            }
        }
        List<Position> speedPositions = new ArrayList<>();
        List<Position> healPositions = new ArrayList<>();
        List<Position> resPositions = new ArrayList<>();
        for (Point point : state.getPoints().get("buff")) {
            Position position = new Position(point.getV3().getX() + 0.5,
                    point.getV3().getY(),
                    point.getV3().getZ() + 0.5);
            if (point.getTag().equals("speed")) {
                speedPositions.add(position);
            } else if (point.getTag().equals("res")) {
                resPositions.add(position);
            } else {
                healPositions.add(position);
            }
        }
        info.setSpeedPoints(new Position[speedPositions.size()]);
        for (int index = 0; index < speedPositions.size(); ++index) {
            info.getSpeedPoints()[index] = speedPositions.get(index);
        }
        info.setResistancePoints(new Position[resPositions.size()]);
        for (int index = 0; index < resPositions.size(); ++index) {
            info.getResistancePoints()[index] = resPositions.get(index);
        }
        info.setHealPoints(new Position[healPositions.size()]);
        for (int index = 0; index < healPositions.size(); ++index) {
            info.getHealPoints()[index] = healPositions.get(index);
        }
        HashMap<String, LiftManager.Lift> lifts = new HashMap<>();
        for (Point point : state.getPoints().get("lift")) {
            String[] tag = point.getTag().split(",");
            Position position = new Position(point.getV3().getX(), point.getV3().getY(), point.getV3().getZ());
            double radius = 1;
            try {
                if (tag.length != 1) {
                    radius = 2;
                    position.add(Double.parseDouble(tag[1]) + 0.5,
                            Double.parseDouble(tag[2]),
                            Double.parseDouble(tag[3]) + 0.5);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            lifts.put(tag[0], new LiftManager.Lift(position.toVector(), null, radius));
        }
        for (Point point : state.getPoints().get("dest")) {
            String[] tag = point.getTag().split(",");
            Position position = new Position(point.getV3().getX(), point.getV3().getY(), point.getV3().getZ());
            if (tag.length != 1) {
                position.add(Double.parseDouble(tag[1]) + + 0.5,
                        Double.parseDouble(tag[2]),
                        Double.parseDouble(tag[3]) + 0.5);
            }
            lifts.get(tag[0]).setTo(position.toVector());
        }
        for (LiftManager.Lift lift : lifts.values()) {
            Minigame.getInstance().getLiftManager().addLift(lift);
        }
        prepareMap(state);
    }

    public boolean isMapPrepared() {
        return world != null;
    }

    public void prepareMap(BuildWorldState state) {}

    public void start() {
        if (state == State.GAME) {
            System.err.println("Can't start game, already started!");
            return;
        }
        if (state == State.PREPARING) {
            System.err.println("Can't start game, status is PREPARING!");
            return;
        }
        stats = new GameStats();
        stats.setMode(info.getMode());
        gameStatsManager = new GameStatsManager(stats);
        Plugin.getInstance().getBoosterManager().load(Bukkit.getOnlinePlayers());
        Plugin.getInstance().getBoosterManager().precalcMultipliers(this);
    
        if (!isMapPrepared()) {
            prepareMap(lobby.getMapVoting().getMostVoted().getBuildName());
        }
        stats.setMap(lobby.getMapVoting().getMostVoted().getDisplayName());
        for (Player player : Bukkit.getOnlinePlayers()) {
            Plugin.getInstance().getCosmeticManager().loadPlayer(player);
        }
        mapManager = new MapManager(world);
        deathAdviceManager = new DeathAdviceManager();
        chooseTeams();
    
        for (GamePlayer player : players.values()) {
            spawnPlayer(player, 0);
            AdviceManager.sendAdvice(player.getPlayer(), "gameStarted");
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                for (GamePlayer player : players.values()) {
                    AdviceManager.sendAdvice(player.getPlayer(), "toWin");
                }
            }
        }.runTaskLater(Plugin.getInstance(), 60);
        new BukkitRunnable() {
            @Override
            public void run() {
                for (GamePlayer player : players.values()) {
                    AdviceManager.sendAdvice(player.getPlayer(), "bug");
                }
            }
        }.runTaskLater(Plugin.getInstance(), 120);
        new BukkitRunnable() {
            @Override
            public void run() {
                for (GamePlayer player : players.values()) {
                     AdviceManager.sendAdvice(player.getPlayer(), "advice");
                }
            }
        }.runTaskLater(Plugin.getInstance(), 180);
        state = State.GAME;
        Plugin.getInstance().getServerData().updateStatus(Plugin.getInstance().getSettings().getServerName(), state.toString());
        if (Plugin.getInstance().getSettings().isCristalix()) {
            RealmInfo info = IRealmService.get().getCurrentRealmInfo();
            info.setStatus(RealmStatus.GAME_STARTED_RESTRICTED);
        }
        lobby = null;
        SafeRunnable cooldownMessageUpdater = new SafeRunnable() {
            @Override
            public void run() {
                for (GamePlayer player : players.values()) {
                    if (player.getPlayer().getGameMode() == GameMode.SPECTATOR ||
                        player.isActionBarBlocked()) {
                        continue;
                    }
                    
                    for (DoubleSpaceInterface cur : GameUtils.selectItems(player, DoubleSpaceInterface.class)) {
                        Item c = (Item) cur;
                        player.getPlayer().setLevel((int) c.getCooldown().getSecondsRemaining());
                        float prog = 1.0f - (float)c.getCooldown().getSecondsRemaining() * 20 / c.getCooldown().getDuration();
                        prog = Math.max(Math.min(prog, 1.0f), 0.0f);
                        player.getPlayer().setExp(prog);
                    }
                    
                    Item item = player.getSelectedItem();
                    if (item == null || item.getCooldown().getDuration() == 0 || item instanceof DoubleSpaceInterface) {
                        player.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(""));
                    } else if (item.getCooldown().isReady()) {
                        player.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Lang.get("game.itemReady")));
                    } else {
                        MessagingUtils.sendCooldownMessage(player.getPlayer(), item.getName(), item.getCooldown().getSecondsRemaining());
                    }
                }
            }
        };
        cooldownMessageUpdater.runTaskTimer(Plugin.getInstance(), 5, 5);
        runnables.add(cooldownMessageUpdater);

        Minigame.getInstance().getLootsManager().load();
        Minigame.getInstance().getLiftManager().load();
        for (GamePlayer gp : players.values()) {
            gp.updateInventory();
        }
        
        SafeRunnable checkAfk = new SafeRunnable() {
            final HashMap<Player, Integer> counter = new HashMap<>();
            final HashMap<Player, Vector> prevLoc = new HashMap<>();
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (counter.containsKey(player)) {
                        Vector was = prevLoc.get(player);
                        if (was.distance(player.getLocation().toVector()) < 1) {
                            int cnt = counter.get(player) + 1;
                            counter.put(player, cnt);
                            if (cnt >= 6) {
                                player.kickPlayer(Lang.get("game.afkKickMessage"));
                            }
                        } else {
                            counter.put(player, 0);
                        }
                    } else {
                        counter.put(player, 0);
                    }
                    prevLoc.put(player, player.getLocation().toVector());
                }
            }
        };
        checkAfk.runTaskTimer(Plugin.getInstance(), info.getAfkTime() / 6, info.getAfkTime() / 6);
        runnables.add(checkAfk);
    
        HashMap<String, Pair<Double, Double>> multipliers = new HashMap<>();
        for (GamePlayer gp : players.values()) {
            for (Booster booster : Plugin.getInstance().getBoosterManager().getBoosters(gp.getPlayer())) {
                if (booster.getStartTime() > System.currentTimeMillis() / 1000 ||
                        booster.getExpirationTime() < System.currentTimeMillis() / 1000) {
                    continue;
                }
                Pair<Double, Double> cur = multipliers.getOrDefault(gp.getPlayer().getName(), Pair.of(1.0, 1.0));
                switch (booster.getName()) {
                    case "G1" :
                        cur.setKey(cur.getKey() + 1);
                        break;
                    case "G2" :
                        cur.setKey(cur.getKey() + 0.5);
                        break;
                    case "G3" :
                        cur.setValue(cur.getValue() + 1);
                        break;
                }
                if (cur.getKey() > 1 || cur.getValue() > 1) {
                    multipliers.put(gp.getPlayer().getName(), cur);
                }
            }
        }
        ArrayList<ArrayList<Player>> byTeam = new ArrayList<>();
        ArrayList<ArrayList<String>> boosterTitles = new ArrayList<>();
        for (int i = 0; i < info.getTeamCount(); i++) {
            boosterTitles.add(new ArrayList<>());
            byTeam.add(new ArrayList<>());
        }
        for (Map.Entry<String, Pair<Double, Double>> cur : multipliers.entrySet()) {
            GamePlayer gp = players.get(cur.getKey());
            int team = gp.getTeam();
            if (cur.getValue().getKey() > 1) {
                boosterTeamSpent.add(gp);
                boosterTitles.get(team).add(Lang.get("game.boosterBarTeam").
                        replace("%val%", String.format("%.1f", cur.getValue().getKey())).
                        replace("%pl%", cur.getKey()));
            }
            if (cur.getValue().getValue() > 1) {
                boosterGlobalSpent.add(gp);
                for (int i = 0; i < info.getTeamCount(); i++) {
                    boosterTitles.get(i).add(Lang.get("game.boosterBarGlobal").
                            replace("%val%", String.format("%.1f", cur.getValue().getValue())).
                            replace("%pl%", cur.getKey()));
                }
            }
        }
        
        for (GamePlayer gp : players.values()) {
            byTeam.get(gp.getTeam()).add(gp.getPlayer());
        }
        BukkitRunnable runnable = new BukkitRunnable() {
            final int[] boosterIdx = {0, 0};
            @Override
            public void run() {
                for (int i = 0; i < 2; i++) {
                    if (boosterTitles.get(i).isEmpty()) {
                        continue;
                    }
                    for (Player pl : byTeam.get(i)) {
                        IDisplayService.get().sendProgress(pl.getUniqueId(), ProgressMessage.builder().
                                updateType(EnumUpdateType.ADD).name(boosterTitles.get(i).get(boosterIdx[i])).percent(1).
                                color(GameUtils.getBrightColors()[(int) (Math.random() * GameUtils.getBrightColors().length)]).
                                position(EnumPosition.TOPTOP).build());
                    }
                    boosterIdx[i] = (boosterIdx[i] + 1) % boosterTitles.get(i).size();
                }
            }
        };
        runnable.runTaskTimer(Plugin.getInstance(), 0, 200);
        runnables.add(runnable);
    
        Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
            for (Entity entity : world.getEntities()) {
                if (entity instanceof org.bukkit.entity.Item) {
                    entity.remove();
                }
            }
        }, 20);
    }

    public void onPlayerKilled(GamePlayer player, GamePlayer killer, Collection<GamePlayer> assists) {
    
    }

    private void chooseTeams() {
        final int cnt = info.getTeamCount();
        int[] teams = new int[cnt];

        final Stack<GamePlayer> left = new Stack<>();
        for (GamePlayer gp : players.values()) {
            if (gp.getTeam() != -1) {
                teams[gp.getTeam()]++;
            } else {
                left.add(gp);
            }
        }
        ArrayList<Integer> idxs = new ArrayList<>(cnt);
        for (int i = 0; i < cnt; i++) {
            idxs.add(i);
        }
        idxs.sort(Comparator.comparingInt(a -> teams[a]));
        for (int t = 0; t < cnt-1; t++) {
            while (teams[t] != teams[t+1] && !left.isEmpty()) {
                for (int i = 0; i <= t && !left.isEmpty(); i++) {
                    int team = idxs.get(i);
                    teams[team]++;
                    left.peek().setTeam(team);
                    left.pop();
                }
            }
        }
        for (int t = 0; !left.isEmpty(); t = (t + 1) % cnt) {
            teams[t]++;
            left.peek().setTeam(t);
            left.pop();
        }
        
        for (GamePlayer gp : players.values()) {
            stats.getPlayers().get(gp.getPlayer().getName()).setTeam(gp.getTeam());
        }
        mobs = new HashMap<>();
    }

    public void finish(int winnerTeam) {
        finish(winnerTeam, true);
    }

    public void finish(int winnerTeam, boolean needFireworks) {
        if (state != State.GAME) {
            System.err.println("Can't finish game, not in game! Current status: " + state);
            return;
        }

        state = State.FINISHING;
        Plugin.getInstance().getServerData().updateStatus(Plugin.getInstance().getSettings().getServerName(),
                state.toString());
        if (Plugin.getInstance().getSettings().isCristalix()) {
            RealmInfo info = IRealmService.get().getCurrentRealmInfo();
            info.setStatus(RealmStatus.GAME_ENDING);
        }
        stats.setGameDurationSec((int) (System.currentTimeMillis() / 1000 - gameStatsManager.getStartTime()));
        stats.setWonTeam(winnerTeam);
        for (GamePlayer gp : players.values()) {
            if (gp.getPlayer().isOnline()) {
                stats.getPlayers().get(gp.getPlayer().getName()).setPlayTimeSec(stats.getGameDurationSec());
            }
            Player player = gp.getPlayer();
            player.setGameMode(GameMode.SURVIVAL);
            player.leaveVehicle();
            for (PotionEffect effect : player.getActivePotionEffects()) {
                player.removePotionEffect(effect.getType());
            }
            if (gp.getTeam() == winnerTeam) {
                rewardManager.give("winGame", player, "");
                MessagingUtils.sendTitle(Lang.get("game.won"), Lang.get("game.winSubtitle").
                        replace("%exp%", GameUtils.getString(getMultiplier(gp) * rewardManager.get("winGame").getCount())), player, 0, 60, 0);
                Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
                    if (player.isOnline()) {
                        MessagingUtils.sendTitle(Lang.get("game.won"), Lang.get("game.endResult").
                                        replace("%exp%", String.valueOf(stats.getPlayers().get(player.getName()).getExpGained())),
                                player, 0, 60, 0);
                    }
                }, 60);
            } else {
                MessagingUtils.sendTitle(Lang.get("game.lost"), Lang.get("game.endResult").
                                replace("%exp%", String.valueOf(stats.getPlayers().get(player.getName()).getExpGained())),
                        player, 0, 120, 0);
            }
            player.setFireTicks(0);
            Plugin.getInstance().getPlayerData().increaseBalance(player.getName(), rewardManager.getExp(player.getName()));
        }

        for (LivingEntity entity: world.getLivingEntities()) {
            if (!(entity instanceof Player)) {
                entity.remove();
            } else {
                ((Player) entity).setAllowFlight(false);
            }
        }
    
        this.unload();
        afterParty = new AfterParty(this, winnerTeam);
        afterParty.start();

        final BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (needFireworks) {
                    for (int i = 0; i < 2; i++) {
                        Drawings.spawnFireworks(MathUtils.randomCylinder(
                                info.getLobbyPosition().toLocation(info.getLobbyWorld()),
                                13, -10));
                    }
                }
                //Bukkit.getServer().broadcastMessage("§6Спасибо за участие! Все заходим в группу вк §cvk.com/etherwar§6 и проходим опрос в закрепе!");
            }
        };
        runnable.runTaskTimer(Plugin.getInstance(), 0, 40);
        new BukkitRunnable() {
            @Override
            public void run() {
                afterParty.stop();
                afterParty = null;
                runnable.cancel();
                ServerInfo lobbyServer = Plugin.getInstance().getServerData().getSavedLobbyServer();
                Set<String> playerNames = new HashSet<>(players.keySet());
                for (String playerName : playerNames) {
                    Player player = players.get(playerName).getPlayer();
                    removePlayer(playerName);
                    if (lobbyServer != null) {
                        BridgeUtils.redirectPlayer(player, lobbyServer.getName());
                    } else {
                        player.kickPlayer(Lang.get("game.gameFinished"));
                    }
                }
                //stats.unload();
                if (Plugin.getInstance().getSettings().isStopAfterGame()) {
                    Bukkit.shutdown();
                    return;
                }
                state = State.PREPARING;
                Plugin.getInstance().getServerData().updateStatus(Plugin.getInstance().getSettings().getServerName(),
                        state.toString());
                if (Plugin.getInstance().getSettings().isCristalix()) {
                    RealmInfo info = IRealmService.get().getCurrentRealmInfo();
                    info.setStatus(RealmStatus.STARTING_GAME);
                }
                prepare();
            }
        }.runTaskLater(Plugin.getInstance(), 20 * info.getFinishTime());
    }

    public void prepare() {
        load();
        lobby = new GameLobby(this);
        state = State.WAITING;
        Plugin.getInstance().getServerData().updateStatus(Plugin.getInstance().getSettings().getServerName(),
                state.toString());
        if (Plugin.getInstance().getSettings().isCristalix()) {
            RealmInfo info = IRealmService.get().getCurrentRealmInfo();
            info.setStatus(RealmStatus.WAITING_FOR_PLAYERS);
        }
        rewardManager = new RewardManager();
        Plugin.getInstance().getData().loadRewards(rewardManager);
    }

    public boolean load() {
        if (loaded) {
            return false;
        }
        loaded = true;
        return true;
    }

    public boolean unload() {
        if (!loaded) {
            return false;
        }
        loaded = false;
        for (final BukkitRunnable runnable : runnables) {
            runnable.cancel();
        }
        runnables.clear();
        if (mapManager != null) {
            mapManager.finish();
        }
        Minigame.getInstance().getLiftManager().unload();
        Minigame.getInstance().getLootsManager().unload();

        Minigame.getInstance().getGameEvents().getDamageCause().clear();
        return true;
    }

    public void addPlayer(Player player) {
        if (state == State.GAME) {
            //BridgeUtils.toLobby(player, Lang.get("game.gameAlreadyStarted"));
            player.kickPlayer(Lang.get("game.gameAlreadyStarted"));
            return;
        }
        if (state == State.PREPARING) {
            //BridgeUtils.toLobby(player, Lang.get("game.gamePrepairing"));
            player.kickPlayer(Lang.get("game.gamePrepairing"));
            return;
        }
        if (info.getMaxPlayerCount() <= players.size()) {
            //BridgeUtils.toLobby(player, Lang.get("game.overflow"));
            player.kickPlayer(Lang.get("game.overflow"));
            return;
        }
        if (players.size() >= info.getMaxPlayerCount()) {
            state = State.GAME_FULL;
            Plugin.getInstance().getServerData().updateStatus(Plugin.getInstance().getSettings().getServerName(),
                    state.toString());
        }
        GamePlayer gamePlayer = new GamePlayer(player);
        PlayerInfo info = Plugin.getInstance().getPlayerData().getPlayerInfo(player.getName());
        gamePlayer.setClassName(info.getSelectedClass());
        players.put(player.getName(), gamePlayer);
        teleportToLobby(player);
        lobby.onPlayerJoined(players.get(player.getName()));
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(10);
        player.setHealth(10);
        for (PotionEffect pt : player.getActivePotionEffects()) {
            player.removePotionEffect(pt.getType());
        }
    }

    public void removePlayer(String playerName) {
        if (!players.containsKey(playerName)) {
            return;
        }
        GamePlayer player = players.get(playerName);
        player.getPlayer().getInventory().clear();
        if (state == State.WAITING) {
            lobby.onPlayerLeft(player);
            if (players.size() == info.getMaxPlayerCount()) {
                state = State.WAITING;
                Plugin.getInstance().getServerData().updateStatus(Plugin.getInstance().getSettings().getServerName(),
                        state.toString());
            }
        }
        players.remove(playerName);
        if (state == State.GAME) {
            deathAdviceManager.forgetPlayer(player.getPlayer().getUniqueId());
            int tt = -1;
            for (GamePlayer gp : players.values()) {
                if (tt == -1) {
                    tt = gp.getTeam();
                }
                if (tt != gp.getTeam()) {
                    return;
                }
            }
            finish(tt);
        }
    }

    public void respawnPlayer(GamePlayer gamePlayer) {
        if (state == State.WAITING) {
            teleportToLobby(gamePlayer.getPlayer());
        } else {
            spawnPlayer(gamePlayer, info.getRespawnTime());
        }
    }

    private void teleportToLobby(Player player) {
        player.teleport(info.getLobbyPosition().toLocation(info.getLobbyWorld()));
    }

    private void addItems(GamePlayer player) {
        player.getItems().clear();
        player.getPlayer().getInventory().clear();
        UnitClassDescription classDescription = Plugin.getInstance().getData().getClassNameToDescription().get(player.getClassName());
        for (String itemName : classDescription.getItemNames()) {
            player.addItem(itemName, player.getPlayerInfo().getItemLevel(player.getClassName(), itemName));
        }
        player.updateInventory();
    }

    private void toSpawn(final GamePlayer gp) {
        final Player player = gp.getPlayer();

        player.setGameMode(GameMode.SURVIVAL);
        new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0).apply(player);

        final int locationIndex = new Random().nextInt(info.getSpawnPoints()[gp.getTeam()].length);
        final DirectedPosition spawnPosition = info.getSpawnPoints()[gp.getTeam()][locationIndex];
        player.teleport(spawnPosition.toLocation(world));
        final int maxHealth =  Plugin.getInstance().getData().getClassNameToDescription().get(gp.getClassName()).getMaxHP();
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxHealth);
        player.setHealth(maxHealth);
        player.setInvulnerable(false);

        MessagingUtils.sendTitle("", player, 0, 1, 0);
        MessagingUtils.sendActionBar("", player);
        addItems(gp);
    }

    public void spawnPlayer(GamePlayer gp, int respawnTime) {
        //System.out.println("o1: " + gp.getPlayer().getLocation().getWorld().getName());
        final Player player = gp.getPlayer();
        if (respawnTime == 0) {
            toSpawn(gp);
            return;
        }
        player.setGameMode(GameMode.SPECTATOR);
        player.setVelocity(new Vector(0,4,0));
        //System.out.println("o2: " + gp.getPlayer().getLocation().getWorld().getName());
        if (respawnTime == -1) {
            MessagingUtils.sendTitle(Lang.get("game.livesNotLeft"), deathAdviceManager.nextAdvice(gp), player, 0, 80, 0);
            return;
        } else {
            if (!gp.isLockedTitles()) {
                MessagingUtils.sendTitle(Lang.get("game.dead"), deathAdviceManager.nextAdvice(gp), player, 0, 80, 0);
            }
        }

        SafeRunnable runnable = new SafeRunnable() {
            int counter = respawnTime;
            @Override
            public void run() {
                if (!gp.isLockedTitles() && counter < respawnTime && (respawnTime - counter) % 80 == 0) {
                    MessagingUtils.sendSubtitle(deathAdviceManager.nextAdvice(gp), gp.getPlayer(), 0, 80, 0);
                }

                if (state == State.FINISHING) {
                    this.cancel();
                    return;
                }
                if (counter <= 0) {
                    gp.setLockedTitles(false);
                    toSpawn(gp);
                    onPlayerRespawned(gp);
                    this.cancel();
                    return;
                }
                MessagingUtils.sendActionBar(Lang.get("game.deathTime").
                        replace("%time%", MessagingUtils.getTimeString(counter / 20, false)), player.getPlayer());
                counter -= 20;
            }
        };
        runnable.runTaskTimer(Plugin.getInstance(), 0, 20);
        runnables.add(runnable);
    }
    
    public double getMultiplier(GamePlayer gp) {
        double mult = getBoosterMult().getOrDefault(gp, -1.0);
        if (mult == -1) {
            mult = Plugin.getInstance().getBoosterManager().calculateMultiplier(this, gp);
            getBoosterMult().put(gp, mult);
        }
        return mult;
    }
}
