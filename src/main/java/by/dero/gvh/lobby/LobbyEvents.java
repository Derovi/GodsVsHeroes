package by.dero.gvh.lobby;

import by.dero.gvh.Plugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class LobbyEvents implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        if (!Plugin.getInstance().getPlayerData().isPlayerRegistered(event.getPlayer().getName())) {
            Plugin.getInstance().getPlayerData().registerPlayer(event.getPlayer().getName());
            Plugin.getInstance().getPlayerData().unlockClass(event.getPlayer().getName(), "warrior");
        }
        Player player = event.getPlayer();
        Lobby.getInstance().playerJoined(player);
    }

    @EventHandler
    public void onPlayerLeft(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        Player player = event.getPlayer();
        Lobby.getInstance().playerLeft(player);
    }
}
