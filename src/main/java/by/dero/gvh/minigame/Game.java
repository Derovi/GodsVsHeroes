package by.dero.gvh.minigame;

import by.dero.gvh.GamePlayer;
import by.dero.gvh.Plugin;
import by.dero.gvh.model.UnitClassDescription;
import by.dero.gvh.utils.Board;
import by.dero.gvh.utils.Position;
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
        }
        System.out.println("spawned");
        state = State.GAME;
        lobby = null;
    }

    public void onPlayerKilled(Player player, LivingEntity killer) {
        player.sendMessage("Вы были обдристаны " + killer.getName());
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
            player.kickPlayer("§cGame finished!");
        }
        state = State.PREPARING;
        prepare();
    }

    public void prepare() {
        load();
        lobby = new GameLobby(this);
        state = State.WAITING;
    }

    abstract void load();

    public void addPlayer(Player player) {
        if (state == State.GAME) {
            player.kickPlayer("§cGame already started, try later!");
            return;
        }
        if (state == State.PREPARING) {
            player.kickPlayer("§cGame preparing, try later!");
            return;
        }
        players.put(player.getName(), new GamePlayer(player));
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

    public void spawnPlayer(GamePlayer player, int rebirthTime) {
        System.out.println("Spawn player " + player.getPlayer());
        int locationIndex = new Random().nextInt(getInfo().getSpawnPoints()[player.getTeam()].length);
        Position spawnPosition = getInfo().getSpawnPoints()[player.getTeam()][locationIndex];
        player.getPlayer().teleport(new Location(Plugin.getInstance().getServer().getWorld(getInfo().getWorld()),
                spawnPosition.getX(), spawnPosition.getY(), spawnPosition.getY()));
        player.getItems().clear();
        player.getPlayer().getInventory().clear();
        UnitClassDescription classDescription = Plugin.getInstance().getData().getClassNameToDescription().get(player.getClassName());
        System.out.println("class: " + player.getClassName());
        System.out.println("null: " + (classDescription == null));
        for (String itemName : classDescription.getItemNames()) {
            System.out.println("add item: " + itemName);
            player.addItem(itemName, player.getPlayerInfo().getItemLevel(player.getClassName(), itemName));
        }
        player.getPlayer().setScoreboard(board.getScoreboard());
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
