package by.dero.gvh;

import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.List;

public class Game {
    private List<GamePlayer> players = new LinkedList<>();

    public void addPlayer(Player player, String className) {
        players.add(new GamePlayer(player, className));
    }

    public List<GamePlayer> getPlayers() {
        return players;
    }

    public void setPlayers(List<GamePlayer> players) {
        this.players = players;
    }
}
