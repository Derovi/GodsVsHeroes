package by.dero.gvh.lobby;

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
