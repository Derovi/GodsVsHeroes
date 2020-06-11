package by.dero.gvh.game;

import by.dero.gvh.GamePlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class Game {
    public enum State {
        GAME, WAITING, PREPARING
    }

    public Game(GameInfo info) {
        this.info = info;
        state = State.WAITING;
    }

    private GameInfo info;
    private State state;
    private HashMap<String, GamePlayer> players = new HashMap<>();

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
    }

    public void finish() {
        if (state != State.GAME) {
            System.err.println("Can't finish game, not in game! Current status: " + state);
            return;
        }

        state = State.PREPARING;
        prepare();
    }

    public void prepare() {
        state = State.WAITING;
    }

    public GameInfo getInfo() {
        return info;
    }

    public State getState() {
        return state;
    }

    public void addPlayer(Player player) {
        players.put(player.getName(), new GamePlayer(player));
    }

    public HashMap<String, GamePlayer> getPlayers() {
        return players;
    }


}
