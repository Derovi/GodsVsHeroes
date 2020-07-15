package by.dero.gvh.minigame;

import by.dero.gvh.AdviceManager;
import by.dero.gvh.GameMob;
import by.dero.gvh.GamePlayer;
import by.dero.gvh.Plugin;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.*;
import by.dero.gvh.utils.*;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
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
    private final HashMap<String, GamePlayer> players = new HashMap<>();
    private HashMap<UUID, GameMob> mobs;
    private final HashMap<String, Location> playerDeathLocations = new HashMap<>();
    private RewardManager rewardManager;
    private MapManager mapManager;
    protected boolean loaded = false;

    public Stats getStats() {
        return stats;
    }

    protected Stats stats;

    public LinkedList<BukkitRunnable> getRunnables() {
        return runnables;
    }

    private final LinkedList<BukkitRunnable> runnables = new LinkedList<>();

    protected void onPlayerRespawned(final GamePlayer gp) { }

    public void start() {
        mapManager = new MapManager(Bukkit.getWorld(getInfo().getWorld()));
        if (state == State.GAME) {
            System.err.println("Can't start game, already started!");
            return;
        }
        if (state == State.PREPARING) {
            System.err.println("Can't start game, status is PREPARING!");
            return;
        }
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
                    Item item = player.getSelectedItem();
                    if (item == null || item.getCooldown().getDuration() == 0) {
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

        SafeRunnable borderChecker = new SafeRunnable() {
            final DirectedPosition[] borders = getInfo().getMapBorders();
            final String desMsg = Lang.get("game.desertionMessage");
            final HashMap<UUID, Vector> lastPos = new HashMap<>();
            @Override
            public void run() {
                for (LivingEntity entity : Minigame.getInstance().getWorld().getLivingEntities()) {
                    final Location loc = entity.getLocation();
                    Vector newVelocity = null;
                    if (loc.getX() < borders[0].getX()) {
                        newVelocity = new Vector(1.5, 0, 0);
                    } else if (loc.getX() > borders[1].getX()) {
                        newVelocity = new Vector(-1.5, 0, 0);
                    } else if (loc.getZ() < borders[0].getZ()) {
                        newVelocity = new Vector(0, 0, 1.5);
                    } else if (loc.getZ() > borders[1].getZ()) {
                        newVelocity = new Vector(0, 0, -1.5);
                    }
                    if (newVelocity != null) {
                        if (!entity.isInsideVehicle()) {
                            entity.teleport(lastPos.get(entity.getUniqueId()).toLocation(entity.getWorld()));
//                            entity.setVelocity(newVelocity);
                        } else {
                            newVelocity = newVelocity.multiply(0.5);
                            if (entity.getVehicle() instanceof Chicken) {
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        if (!entity.isDead()) {
                                            entity.getVehicle().setVelocity(new Vector(0, 0, 0));
                                        }
                                    }
                                }.runTaskLater(Plugin.getInstance(), 10);
                            } else
                            if (entity.getVehicle() instanceof SkeletonHorse) {
                                ArmorStand armorStand = (ArmorStand) entity.getWorld().spawnEntity(entity.getLocation(),
                                        EntityType.ARMOR_STAND);
                                armorStand.setVisible(false);
                                armorStand.setInvulnerable(true);
                                armorStand.setSmall(true);
                                armorStand.setVelocity(newVelocity);
                                armorStand.addPassenger(entity.getVehicle());
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        armorStand.remove();
                                    }
                                }.runTaskLater(Plugin.getInstance(), 10);
                            } else {
                                entity.getVehicle().setVelocity(newVelocity);
                            }
                        }
                        if (entity instanceof Player) {
                            entity.sendMessage(desMsg);
                        }
                    } else {
                        lastPos.put(entity.getUniqueId(), entity.getLocation().toVector());
                    }
                }
            }
        };
        borderChecker.runTaskTimer(Plugin.getInstance(), 5, 10);
        runnables.add(borderChecker);
        stats = new Stats();

        Minigame.getInstance().getLootsManager().load();
        Minigame.getInstance().getLiftManager().load();
        for (GamePlayer gp : getPlayers().values()) {
            gp.updateInventory();
        }
    }

    public void onPlayerKilled(Player player, Player killer) {
        try {
            if (!player.equals(killer)) {
                rewardManager.give("killEnemy", killer,
                        rewardManager.getMessage("killEnemy").replace("%enemy%", player.getName()));
                GamePlayer gpKiller = GameUtils.getPlayer(killer.getName());
                GamePlayer gpTarget = GameUtils.getPlayer(player.getName());
                String kilCode = Lang.get("commands." + (char)('1' + gpKiller.getTeam())).substring(0, 2);
                String tarCode = Lang.get("commands." + (char)('1' + gpTarget.getTeam())).substring(0, 2);
                String kilClass = Lang.get("classes." + gpKiller.getClassName());
                String tarClass = Lang.get("classes." + gpTarget.getClassName());
                Bukkit.getServer().broadcastMessage(Lang.get("game.killGlobalMessage").
                        replace("%kilCode%", kilCode).replace("%kilClass%", kilClass).replace("%killer%", killer.getName()).
                        replace("%tarCode%", tarCode).replace("%tarClass%", tarClass).replace("%target%", player.getName()));

                stats.addKill(player, killer);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void chooseTeams() {
        final int cnt = getInfo().getTeamCount();
        int[] teams = new int[cnt];

        final Stack<GamePlayer> left = new Stack<>();
        for (GamePlayer gp : getPlayers().values()) {
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
        this.unload();
        Plugin.getInstance().getServerData().updateStatus(Plugin.getInstance().getSettings().getServerName(),
                state.toString());
        if (Plugin.getInstance().getSettings().isCristalix()) {
            RealmInfo info = IRealmService.get().getCurrentRealmInfo();
            info.setStatus(RealmStatus.GAME_ENDING);
        }

        for (GamePlayer gp : players.values()) {
            Player player = gp.getPlayer();
            player.setGameMode(GameMode.SURVIVAL);
            player.leaveVehicle();
            if (gp.getTeam() == winnerTeam) {
                rewardManager.give("winGame", player);
            } else {
                rewardManager.give("loseGame", player);
            }
            player.setFireTicks(0);
        }

        for (LivingEntity entity: Bukkit.getWorld(getInfo().getWorld()).getLivingEntities()) {
            if (!(entity instanceof Player)) {
                entity.remove();
            } else {
                ((Player) entity).setAllowFlight(false);
            }
        }

        afterParty = new AfterParty(this, winnerTeam);
        afterParty.start();
        final BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (needFireworks) {
                    Drawings.spawnFirework(MathUtils.randomCylinder(
                            getInfo().getLobbyPosition().toLocation(getInfo().getWorld()),
                            13, -10
                    ), 2);
                }
                Bukkit.getServer().broadcastMessage("§6Спасибо за участие! Все заходим в группу вк §cvk.com/etherwar§6 и проходим опрос в закрепе!");
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
                stats.unload();
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
        player.teleport(getInfo().getLobbyPosition().toLocation(getInfo().getWorld()));
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
        player.teleport(spawnPosition.toLocation(getInfo().getWorld()));
        final int maxHealth =  Plugin.getInstance().getData().getClassNameToDescription().get(gp.getClassName()).getMaxHP();
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxHealth);
        player.setHealth(maxHealth);
        player.setInvulnerable(false);

        MessagingUtils.sendTitle("", player, 0, 1, 0);
        MessagingUtils.sendActionBar("", player);
        addItems(gp);
    }

    public void spawnPlayer(GamePlayer gp, int respawnTime) {
        final Player player = gp.getPlayer();
        if (respawnTime == 0) {
            toSpawn(gp);
            return;
        }

        player.setGameMode(GameMode.SPECTATOR);
        player.teleport(playerDeathLocations.get(player.getName()));
        player.setVelocity(new Vector(0,4,0));
        if (respawnTime == -1) {
            MessagingUtils.sendTitle(Lang.get("game.livesNotLeft"), player, 0, 20, 0);
            return;
        }
        MessagingUtils.sendTitle(Lang.get("game.dead"), player, 0, 20, 0);

        SafeRunnable runnable = new SafeRunnable() {
            int counter = respawnTime;
            @Override
            public void run() {
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
