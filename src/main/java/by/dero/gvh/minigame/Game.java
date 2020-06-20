package by.dero.gvh.minigame;

import by.dero.gvh.GamePlayer;
import by.dero.gvh.Plugin;
import by.dero.gvh.model.Lang;
import by.dero.gvh.model.PlayerInfo;
import by.dero.gvh.model.UnitClassDescription;
import by.dero.gvh.utils.Board;
import by.dero.gvh.utils.Position;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

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
    private RewardManager rewardManager;
    protected Board board;

    public void start() {
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
            player.getPlayer().setScoreboard(board.getScoreboard());
        }
        System.out.println("spawned");
        state = State.GAME;
        Plugin.getInstance().getServerData().updateStatus(Plugin.getInstance().getSettings().getServerName(),
                state.toString());
        lobby = null;
    }

    public void onPlayerKilled(Player player, LivingEntity killer) {
        try {
            if (!player.equals(killer)) {
                Reward reward = rewardManager.get("killEnemy");
                reward.give((Player) killer, reward.getMessage().replace("%enemy%", player.getName()));
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

        for (GamePlayer player : players.values()) {
            Reward reward;
            if (player.getTeam() == winnerTeam) {
                reward = rewardManager.get("winGame");
            } else {
                reward = rewardManager.get("loseGame");
            }
            reward.give(player.getPlayer());
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
            System.out.println("add item: " + itemName);
            player.addItem(itemName, player.getPlayerInfo().getItemLevel(player.getClassName(), itemName));
        }
    }

    public void spawnPlayer(GamePlayer player, int rebirthTime) {
        int locationIndex = new Random().nextInt(getInfo().getSpawnPoints()[player.getTeam()].length);
        Position spawnPosition = getInfo().getSpawnPoints()[player.getTeam()][locationIndex];
        player.getPlayer().teleport(new Location(Plugin.getInstance().getServer().getWorld(getInfo().getWorld()),
                spawnPosition.getX(), spawnPosition.getY(), spawnPosition.getY()));
        int maxHealth =  Plugin.getInstance().getData().getClassNameToDescription().get(player.getClassName()).getMaxHP();
        player.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxHealth);
        player.getPlayer().setHealth(maxHealth);
        addItems(player);
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
