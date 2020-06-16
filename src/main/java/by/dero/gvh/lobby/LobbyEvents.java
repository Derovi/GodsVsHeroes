package by.dero.gvh.lobby;

import by.dero.gvh.utils.Position;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class LobbyEvents implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.setGameMode(GameMode.CREATIVE);
        player.teleport(new Location(Lobby.getInstance().getWorld(), 0, 60, 0));
        LobbyRecord record = new LobbyRecord();
        record.setPosition(new Position(0, 50, 0));
        PlayerLobby lobby = new PlayerLobby(record);
        lobby.create();
    }
}
