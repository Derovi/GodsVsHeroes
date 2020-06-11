package by.dero.gvh.game;

import by.dero.gvh.GamePlayer;
import by.dero.gvh.Plugin;
import by.dero.gvh.model.UnitClassDescription;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.*;

public abstract class Game implements Listener {
    public enum State {
        GAME, WAITING, PREPARING
    }

    public Game(GameInfo info) {
        this.info = info;
        prepare();
    }

    private Lobby lobby;
    private final GameInfo info;
    private State state;
    private final HashMap<String, GamePlayer> players = new HashMap<>();

    public void start() {
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
        lobby = null;
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
            removePlayer(playerName);
        }
        state = State.PREPARING;
        prepare();
    }

    public void prepare() {
        load();
        lobby = new Lobby(this);
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
        lobby.onPlayerJoined(players.get(player.getName()));
    }

    public void removePlayer(String playerName) {
        lobby.onPlayerLeft(players.get(playerName));
        players.remove(playerName);
    }

    public void spawnPlayer(GamePlayer player, int rebirthTime) {
        int locationIndex = new Random().nextInt(getInfo().getSpawnPoints()[player.getTeam()].length);
        Position spawnPosition = getInfo().getSpawnPoints()[player.getTeam()][locationIndex];
        player.getPlayer().teleport(new Location(Plugin.getInstance().getServer().getWorld(getInfo().getWorld()),
                spawnPosition.getX(), spawnPosition.getY(), spawnPosition.getY()));
        player.getItems().clear();
        player.getPlayer().getInventory().clear();
        UnitClassDescription classDescription = Plugin.getInstance().getData().getUnits().get(player.getClassName());
        for (String itemName : classDescription.getItemNames()) {
            player.addItem(itemName, 0);
        }
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
