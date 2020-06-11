package by.dero.gvh.game;

import by.dero.gvh.GamePlayer;
import by.dero.gvh.model.Item;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Game {
    private HashMap<String, GamePlayer> players = new HashMap<>();

    public void addPlayer(Player player) {
        players.put(player.getName(), new GamePlayer(player));
    }

    public HashMap<String, GamePlayer> getPlayers() {
        return players;
    }
}
