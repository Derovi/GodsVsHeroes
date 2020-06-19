package by.dero.gvh.minigame;

import by.dero.gvh.GamePlayer;
import by.dero.gvh.Plugin;
import by.dero.gvh.model.PlayerInfo;
import by.dero.gvh.model.UnitClassDescription;
import by.dero.gvh.utils.Board;
import by.dero.gvh.utils.Position;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

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
            addItems(player);
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
            Reward reward = rewardManager.get("killEnemy");
            reward.give((Player) killer, reward.getMessage().replace("%enemy%", player.getName()));
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
        for (String playerName : players.keySet()) {
            Player player = players.get(playerName).getPlayer();
            removePlayer(playerName);
            player.kickPlayer("§cИгра окончена!");
        }
        state = State.PREPARING;
        Plugin.getInstance().getServerData().updateStatus(Plugin.getInstance().getSettings().getServerName(),
                state.toString());
        prepare();
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
            player.kickPlayer("§cИгра уже началась");
            return;
        }
        if (state == State.PREPARING) {
            player.kickPlayer("§cИгра готовится");
            return;
        }
        GamePlayer gamePlayer = new GamePlayer(player);
        PlayerInfo info = Plugin.getInstance().getPlayerData().getPlayerInfo(player.getName());
        gamePlayer.setClassName(info.getSelectedClass());
        players.put(player.getName(), gamePlayer);
        teleportToLobby(player);
        lobby.onPlayerJoined(players.get(player.getName()));
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

    public void addItems(GamePlayer player) {
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
