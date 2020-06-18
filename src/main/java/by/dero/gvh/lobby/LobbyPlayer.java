package by.dero.gvh.lobby;

import by.dero.gvh.Plugin;
import by.dero.gvh.model.PlayerInfo;
import org.bukkit.entity.Player;

public class LobbyPlayer {
    private final Player player;
    private PlayerInfo playerInfo;

    public LobbyPlayer(Player player) {
        this.player = player;
    }

    public void loadInfo() {
        playerInfo = Plugin.getInstance().getPlayerData().getPlayerInfo(player.getName());
    }

    public void saveInfo() {
        Plugin.getInstance().getPlayerData().savePlayerInfo(player.getName(), playerInfo);
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayerInfo(PlayerInfo playerInfo) {
        this.playerInfo = playerInfo;
    }

    public PlayerInfo getPlayerInfo() {
        return playerInfo;
    }
}
