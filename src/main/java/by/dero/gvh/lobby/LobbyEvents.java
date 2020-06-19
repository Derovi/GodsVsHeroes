package by.dero.gvh.lobby;

import by.dero.gvh.Plugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class LobbyEvents implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        if (!Plugin.getInstance().getPlayerData().isPlayerRegistered(event.getPlayer().getName())) {
            Plugin.getInstance().getPlayerData().registerPlayer(event.getPlayer().getName());
            //Plugin.getInstance().getPlayerData().unlockClass(event.getPlayer().getName(), "default");

            // TODO remove debug
            for (String className : Plugin.getInstance().getData().getClassNameToDescription().keySet()) {
                Plugin.getInstance().getPlayerData().unlockClass(event.getPlayer().getName(), className);
            }
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

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        LobbyPlayer player = Lobby.getInstance().getPlayers().get(event.getPlayer().getName());
        if (Lobby.getInstance().getActiveLobbies().get(player.getPlayer().getName()).isInPortal()) {
            Lobby.getInstance().playerEnteredPortal(player);
        }
    }
}
