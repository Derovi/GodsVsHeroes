package by.dero.gvh;

import by.dero.gvh.model.Item;
import org.bukkit.entity.Player;

import java.util.List;

public class GamePlayer {
    private Player player;
    private String className;
    private List<Item> items;
    private int team;

    public GamePlayer(Player player, String className) {
        this.player = player;
        this.className = className;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public int getTeam() {
        return team;
    }

    public void setTeam(int team) {
        this.team = team;
    }
}
