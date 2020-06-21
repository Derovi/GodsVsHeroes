package by.dero.gvh.minigame;

import by.dero.gvh.GamePlayer;
import by.dero.gvh.Plugin;
import by.dero.gvh.model.*;
import by.dero.gvh.utils.Board;
import by.dero.gvh.utils.DirectedPosition;
import org.bukkit.GameMode;
import by.dero.gvh.utils.MessagingUtils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public abstract class Game implements Listener {
    public enum State {
        GAME, WAITING, PREPARING
    }

    public Game(GameInfo info) {
        this.info = info;
    }

    private GameLobby lobby;
    private final GameInfo info;
    private State state;
    private final HashMap<String, GamePlayer> players = new HashMap<>();
    private final HashMap<String, Location> playerDeathLocations = new HashMap<>();
    private RewardManager rewardManager;
    private BukkitRunnable cooldownMessageUpdater;
    protected Board board;

    public LinkedList<BukkitRunnable> getRunnables() {
        return runnables;
    }

    private final LinkedList<BukkitRunnable> runnables = new LinkedList<>();

    public void start() {
        GameEvents.setGame(this);
        System.out.println("start game");
        if (state == State.GAME) {
            System.err.println("Can't start game, already started!");
            return;
        }
        if (state == State.PREPARING) {
            System.err.println("Can't start game, status is PREPARING!");
            return;
        }
        chooseTeams();
        System.out.println("starting");
        for (GamePlayer player : players.values()) {
            spawnPlayer(player, 0);
        }
        System.out.println("spawned");
        state = State.GAME;
        Plugin.getInstance().getServerData().updateStatus(Plugin.getInstance().getSettings().getServerName(),
                state.toString());
        lobby = null;
        cooldownMessageUpdater = new BukkitRunnable() {
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
    }

    public void onPlayerKilled(Player player, LivingEntity killer) {
        try {
            if (!player.equals(killer)) {
                rewardManager.give("killEnemy", (Player) killer,
                        rewardManager.getMessage("killEnemy").replace("%enemy%", player.getName()));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void chooseTeams() {
        List<String> playerNames = new ArrayList<>(players.keySet());
        Collections.shuffle(playerNames);
        for (int index = 0; index < playerNames.size(); ++index) {
            players.get(playerNames.get(index)).setTeam(index % getInfo().getTeamCount());
        }
    }

    public void finish(int winnerTeam) {
        if (state != State.GAME) {
            System.err.println("Can't finish game, not in game! Current status: " + state);
            return;
        }

        for (final BukkitRunnable runnable : runnables) {
            runnable.cancel();
        }
        runnables.clear();

        for (GamePlayer player : players.values()) {
            if (player.getTeam() == winnerTeam) {
                rewardManager.give("winGame", player.getPlayer());
            } else {
                rewardManager.give("loseGame", player.getPlayer());
            }
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                Set<String> playerNames = new HashSet<>(players.keySet());
                for (String playerName : playerNames) {
                    Player player = players.get(playerName).getPlayer();
                    removePlayer(playerName);
                    player.kickPlayer(Lang.get("game.gameFinished"));
                }
                state = State.PREPARING;
                Plugin.getInstance().getServerData().updateStatus(Plugin.getInstance().getSettings().getServerName(),
                        state.toString());
                prepare();
            }
        }.runTaskLater(Plugin.getInstance(), 40);
        cooldownMessageUpdater.cancel();
    }

    public void prepare() {
        load();
        lobby = new GameLobby(this);
        state = State.WAITING;
        rewardManager = new RewardManager();
        Plugin.getInstance().getData().loadRewards(rewardManager);
        Plugin.getInstance().getServerData().updateStatus(Plugin.getInstance().getSettings().getServerName(),
                state.toString());
    }

    abstract void load();

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

    public void spawnPlayer(GamePlayer player, int respawnTime) {
        new BukkitRunnable() {
            int counter = respawnTime;

            @Override
            public void run() {
                if (counter == 0) {
                    player.getPlayer().setGameMode(GameMode.SURVIVAL);
                    final int locationIndex = new Random().nextInt(getInfo().getSpawnPoints()[player.getTeam()].length);
                    final DirectedPosition spawnPosition = getInfo().getSpawnPoints()[player.getTeam()][locationIndex];
                    player.getPlayer().teleport(spawnPosition.toLocation(getInfo().getWorld()));
                    final int maxHealth =  Plugin.getInstance().getData().getClassNameToDescription().get(player.getClassName()).getMaxHP();
                    player.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxHealth);
                    player.getPlayer().setHealth(maxHealth);
                    MessagingUtils.sendTitle("", player.getPlayer(), 0, 1, 0);
                    MessagingUtils.sendActionBar("", player.getPlayer());
                    addItems(player);
                    this.cancel();
                    return;
                }
                if (counter == respawnTime) {
                    player.getPlayer().setGameMode(GameMode.SPECTATOR);
                    player.getPlayer().teleport(playerDeathLocations.get(player.getPlayer().getName()));
                    player.getPlayer().setVelocity(new Vector(0,4,0));
                    MessagingUtils.sendTitle(Lang.get("game.dead"), player.getPlayer(), 0, 20 * respawnTime, 0);
                }
                MessagingUtils.sendActionBar(Lang.get("game.deathTime").
                        replace("%time%", MessagingUtils.getTimeString(counter, false)), player.getPlayer());
                --counter;
            }
        }.runTaskTimer(Plugin.getInstance(), 0, 20);

    }

    public HashMap<String, Location> getPlayerDeathLocations() {
        return playerDeathLocations;
    }

    public RewardManager getRewardManager() {
        return rewardManager;
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
}
