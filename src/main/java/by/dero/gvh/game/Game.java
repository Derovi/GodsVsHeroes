package by.dero.gvh.game;

import by.dero.gvh.GamePlayer;
import org.bukkit.entity.Player;

import java.util.EventListener;
import java.util.HashMap;

public class Game implements EventListener {
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

        state = State.GAME;
        lobby = null;
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
        lobby = new Lobby(this);
        state = State.WAITING;
    }

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
