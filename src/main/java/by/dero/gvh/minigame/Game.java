package by.dero.gvh.minigame;

import by.dero.gvh.*;
import by.dero.gvh.minigame.ethercapture.EtherCapture;
import by.dero.gvh.model.*;
import by.dero.gvh.model.interfaces.DoubleSpaceInterface;
import by.dero.gvh.stats.GameStats;
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
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import ru.cristalix.core.build.BuildWorldState;
import ru.cristalix.core.build.models.Point;
import ru.cristalix.core.map.BukkitWorldLoader;
import ru.cristalix.core.map.IMapService;
import ru.cristalix.core.map.LoadedMap;
import ru.cristalix.core.realm.IRealmService;
import ru.cristalix.core.realm.RealmInfo;
import ru.cristalix.core.realm.RealmStatus;

import java.util.*;

public abstract class Game implements Listener {
    public static Game getInstance() {
        return instance;
    }

    public enum State {
        GAME, FINISHING, WAITING, PREPARING, GAME_FULL
    }

    public Game(GameInfo info) {
        this.info = info;
        instance = this;
        GameEvents.setGame(this);
    }

    private static Game instance;
    private GameLobby lobby;
    private AfterParty afterParty;
    private final GameInfo info;
    private State state;
    private World world = null;
    private final HashMap<String, GamePlayer> players = new HashMap<>();
    private HashMap<UUID, GameMob> mobs;
    private final HashMap<String, Location> playerDeathLocations = new HashMap<>();
    private RewardManager rewardManager;
    private MapManager mapManager;
    private DeathAdviceManager deathAdviceManager;
    @Getter @Setter protected GameStats stats;
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
        getInfo().setSpeedPoints(new Position[speedPositions.size()]);
        for (int index = 0; index < speedPositions.size(); ++index) {
            getInfo().getSpeedPoints()[index] = speedPositions.get(index);
        }
        getInfo().setResistancePoints(new Position[resPositions.size()]);
        for (int index = 0; index < resPositions.size(); ++index) {
            getInfo().getResistancePoints()[index] = resPositions.get(index);
        }
        getInfo().setHealPoints(new Position[healPositions.size()]);
        for (int index = 0; index < healPositions.size(); ++index) {
            getInfo().getHealPoints()[index] = healPositions.get(index);
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
        if (!isMapPrepared()) {
            prepareMap(lobby.getSelectedMap());
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            Plugin.getInstance().getCosmeticManager().loadPlayer(player);
        }
        mapManager = new MapManager(world);
        deathAdviceManager = new DeathAdviceManager();
        if (state == State.GAME) {
            System.err.println("Can't start game, already started!");
            return;
        }
        if (state == State.PREPARING) {
            System.err.println("Can't start game, status is PREPARING!");
            return;
        }
        stats = new GameStats();
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
                for (GamePlayer player : getPlayers().values()) {
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
        for (GamePlayer gp : getPlayers().values()) {
            gp.updateInventory();
        }
    }

    public void onPlayerKilled(GamePlayer player, GamePlayer killer, Collection<GamePlayer> assists) {
        try {
            if (!player.equals(killer)) {
                rewardManager.give("killEnemy", killer.getPlayer(), "");

                MessagingUtils.sendSubtitle(Lang.get("rewmes.kill").
                                replace("%exp%", Integer.toString(rewardManager.get("killEnemy").getCount()))
                                .replace("%eth%", Integer.toString(((EtherCapture) this).getEtherCaptureInfo().getEtherForKill())),
                        killer.getPlayer(), 0, 20, 0);

                String kilCode = GameUtils.getTeamColor(killer.getTeam());
                String tarCode = GameUtils.getTeamColor(player.getTeam());
                String kilClass = Lang.get("classes." + killer.getClassName());
                String tarClass = Lang.get("classes." + player.getClassName());
                Bukkit.getServer().broadcastMessage(Lang.get("game.killGlobalMessage").
                        replace("%kilCode%", kilCode).replace("%kilClass%", kilClass).
                        replace("%killer%", killer.getPlayer().getName()).
                        replace("%tarCode%", tarCode).replace("%tarClass%", tarClass).
                        replace("%target%", player.getPlayer().getName()));

                if (assists != null) {
                    for (GamePlayer pl : assists) {
                        rewardManager.give("assist", pl.getPlayer(), "");
                        MessagingUtils.sendSubtitle(Lang.get("rewmes.assist").
                                        replace("%exp%", Integer.toString(rewardManager.get("assist").getCount()))
                                .replace("%eth%", Integer.toString(((EtherCapture) this).getEtherCaptureInfo().getEtherForKill())),
                                pl.getPlayer(), 0, 20, 0);
                    }
                }
                stats.addKill(player, killer, assists);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void chooseTeams() {
        final int cnt = getInfo().getTeamCount();
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
        stats.setGameDurationSec((int) (System.currentTimeMillis() / 1000 - stats.getStartTime()));
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
                rewardManager.give("winGame", player);
            } else {
                rewardManager.give("loseGame", player);
            }
            player.setFireTicks(0);
        }

        Plugin.getInstance().getGameStatsData().saveGameStats(stats);

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
                    Drawings.spawnFirework(MathUtils.randomCylinder(
                            getInfo().getLobbyPosition().toLocation(getInfo().getLobbyWorld()),
                            13, -10
                    ), 2);
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
        }.runTaskLater(Plugin.getInstance(), 20 * getInfo().getFinishTime());
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
        if (getInfo().getMaxPlayerCount() <= getPlayers().size()) {
            //BridgeUtils.toLobby(player, Lang.get("game.overflow"));
            player.kickPlayer(Lang.get("game.overflow"));
            return;
        }
        if (getPlayers().size() >= info.getMaxPlayerCount()) {
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
            if (getPlayers().size() == info.getMaxPlayerCount()) {
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
            spawnPlayer(gamePlayer, getInfo().getRespawnTime());
        }
    }

    private void teleportToLobby(Player player) {
        player.teleport(getInfo().getLobbyPosition().toLocation(getInfo().getLobbyWorld()));
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

        final int locationIndex = new Random().nextInt(getInfo().getSpawnPoints()[gp.getTeam()].length);
        final DirectedPosition spawnPosition = getInfo().getSpawnPoints()[gp.getTeam()][locationIndex];
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
            MessagingUtils.sendTitle(Lang.get("game.dead"), deathAdviceManager.nextAdvice(gp), player, 0, 80, 0);
        }

        SafeRunnable runnable = new SafeRunnable() {
            int counter = respawnTime;
            @Override
            public void run() {
                if (counter < respawnTime && (respawnTime - counter) % 80 == 0) {
                    MessagingUtils.sendSubtitle(deathAdviceManager.nextAdvice(gp), gp.getPlayer(), 0, 80, 0);
                }

                if (state == State.FINISHING) {
                    this.cancel();
                    return;
                }
                if (counter <= 0) {
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

    public World getWorld() {
        return world;
    }

    public MapManager getMapManager() {
        return mapManager;
    }

    public HashMap<String, Location> getPlayerDeathLocations() {
        return playerDeathLocations;
    }

    public GameLobby getLobby() {
        return lobby;
    }

    public GameInfo getInfo() {
        return info;
    }

    public State getState() {
        return state;
    }

    public HashMap<String, GamePlayer> getPlayers() {
        return players;
    }

    public HashMap<UUID, GameMob> getMobs() {
        return mobs;
    }

    public RewardManager getRewardManager () {
        return rewardManager;
    }

    protected void setState (State state) {
        this.state = state;
    }
}
