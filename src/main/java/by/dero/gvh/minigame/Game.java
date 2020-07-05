package by.dero.gvh.minigame;

import by.dero.gvh.ChargesManager;
import by.dero.gvh.GameMob;
import by.dero.gvh.GamePlayer;
import by.dero.gvh.Plugin;
import by.dero.gvh.model.*;
import by.dero.gvh.utils.*;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public abstract class Game {
    public static Game getInstance() {
        return instance;
    }

    public enum State {
        GAME, FINISHING, WAITING, PREPARING
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

    public Stats getStats() {
        return stats;
    }

    protected Stats stats;

    public LinkedList<BukkitRunnable> getRunnables() {
        return runnables;
    }

    private final LinkedList<BukkitRunnable> runnables = new LinkedList<>();

    protected abstract void onPlayerRespawned(final GamePlayer gp);

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
        }
        state = State.GAME;
        Plugin.getInstance().getServerData().updateStatus(Plugin.getInstance().getSettings().getServerName(),
                state.toString());
        lobby = null;
        BukkitRunnable cooldownMessageUpdater = new BukkitRunnable() {
            @Override
            public void run() {
                for (GamePlayer player : getPlayers().values()) {
                    if (player.getPlayer().getGameMode() == GameMode.SPECTATOR) {
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

        BukkitRunnable borderChecker = new BukkitRunnable() {
            final DirectedPosition[] borders = getInfo().getMapBorders();
            final String desMsg = Lang.get("game.desertionMessage");

            @Override
            public void run() {
                for (LivingEntity entity : Minigame.getInstance().getWorld().getLivingEntities()) {
                    final Location loc = entity.getLocation();
                    Vector newVelocity = null;
                    if (loc.getX() < borders[0].getX()) {
                        newVelocity = new Vector(2, 0, 0);
                    } else if (loc.getX() > borders[1].getX()) {
                        newVelocity = new Vector(-2, 0, 0);
                    } else if (loc.getZ() < borders[0].getZ()) {
                        newVelocity = new Vector(0, 0, 2);
                    } else if (loc.getZ() > borders[1].getZ()) {
                        newVelocity = new Vector(0, 0, -2);
                    }
                    if (newVelocity != null) {
                        if (!entity.isInsideVehicle()) {
                            entity.setVelocity(newVelocity);
                        }
                        if (entity instanceof Player) {
                            entity.sendMessage(desMsg);
                        }
                    }
                }
            }
        };
        borderChecker.runTaskTimer(Plugin.getInstance(), 5, 5);
        runnables.add(borderChecker);
        stats = new Stats();

        new ChargesManager();
        Minigame.getInstance().getLootsManager().load();
    }

    public void onPlayerKilled(Player player, LivingEntity killer) {
        try {
            if (!player.equals(killer)) {
                rewardManager.give("killEnemy", (Player) killer,
                        rewardManager.getMessage("killEnemy").replace("%enemy%", player.getName()));
                stats.addKill(player, killer);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void chooseTeams() {
        List<String> playerNames = new ArrayList<>(players.keySet());
        Collections.shuffle(playerNames);
        final int cnt = getInfo().getTeamCount();
        for (int index = 0; index < playerNames.size(); ++index) {
            players.get(playerNames.get(index)).setTeam(cnt - index % cnt - 1);
        }
        mobs = new HashMap<>();
    }

    public void finish(int winnerTeam) {
        if (state != State.GAME) {
            System.err.println("Can't finish game, not in game! Current status: " + state);
            return;
        }

        state = State.FINISHING;
        Plugin.getInstance().getServerData().updateStatus(Plugin.getInstance().getSettings().getServerName(),
                state.toString());

        for (LivingEntity entity: Bukkit.getWorld(getInfo().getWorld()).getLivingEntities()) {
            if (!(entity instanceof Player)) {
                entity.remove();
            }
        }
        for (GamePlayer player : players.values()) {
            if (player.getTeam() == winnerTeam) {
                rewardManager.give("winGame", player.getPlayer());
            } else {
                rewardManager.give("loseGame", player.getPlayer());
            }
        }

        this.unload();
        afterParty = new AfterParty(this, winnerTeam);
        afterParty.start();
        final BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                Drawings.spawnFirework(MathUtils.randomCylinder(
                        getInfo().getLobbyPosition().toLocation(getInfo().getWorld()),
                        25, -10
                ), 2);
            }
        };
        runnable.runTaskTimer(Plugin.getInstance(), 0, 10);
        new BukkitRunnable() {
            @Override
            public void run() {
                afterParty.stop();
                afterParty = null;
                runnable.cancel();
                ServerInfo lobbyServer = Plugin.getInstance().getServerData().getLobbyServer();
                Set<String> playerNames = new HashSet<>(players.keySet());
                for (String playerName : playerNames) {
                    Player player = players.get(playerName).getPlayer();
                    removePlayer(playerName);
                    if (lobbyServer != null) {
                        BungeeUtils.redirectPlayer(player, lobbyServer.getName());
                    } else {
                        player.kickPlayer(Lang.get("game.gameFinished"));
                    }
                }
                stats.unload();
                state = State.PREPARING;
                Plugin.getInstance().getServerData().updateStatus(Plugin.getInstance().getSettings().getServerName(),
                        state.toString());
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
        rewardManager = new RewardManager();
        Plugin.getInstance().getData().loadRewards(rewardManager);
    }

    public abstract void load();

    public void unload() {
        for (final BukkitRunnable runnable : runnables) {
            runnable.cancel();
        }
        mapManager.finish();
        Minigame.getInstance().getLootsManager().unload();
        runnables.clear();

        Minigame.getInstance().getGameEvents().getDamageCause().clear();
    }

    public void addPlayer(Player player) {
        if (state == State.GAME) {
            player.kickPlayer(Lang.get("game.gameAlreadyStarted"));
            return;
        }
        if (state == State.PREPARING) {
            player.kickPlayer(Lang.get("game.gamePrepairing"));
            return;
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
        }
        players.remove(playerName);
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

        new BukkitRunnable() {
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
        }.runTaskTimer(Plugin.getInstance(), 0, 20);
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
}
