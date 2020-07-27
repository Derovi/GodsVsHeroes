package by.dero.gvh.lobby;

import by.dero.gvh.Plugin;
import by.dero.gvh.model.PlayerInfo;
import org.bukkit.entity.Player;

public class LobbyPlayer {
    private final Player player;
    public LobbyPlayer(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}
