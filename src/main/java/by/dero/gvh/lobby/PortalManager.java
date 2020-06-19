package by.dero.gvh.lobby;

import by.dero.gvh.Plugin;
import by.dero.gvh.utils.BungeeUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class PortalManager implements Listener {
    private final int timeout = 2000;
    private final HashMap<String, Long> playersInPortal = new HashMap<>();

    public PortalManager() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Map.Entry<String, Long> entry : playersInPortal.entrySet()) {
                    if (System.currentTimeMillis() - entry.getValue() > timeout) {
                        playersInPortal.remove(entry.getKey());
                    }
                }
            }
        }.runTaskTimer(Plugin.getInstance(), 20, 20);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (playersInPortal.containsKey(event.getPlayer().getName())) {
            return;
        }
        LobbyPlayer player = Lobby.getInstance().getPlayers().get(event.getPlayer().getName());
        if (Lobby.getInstance().getActiveLobbies().get(player.getPlayer().getName()).isInPortal()) {
            playersInPortal.put(event.getPlayer().getName(), System.currentTimeMillis());
            playerEnteredPortal(player);
        }
    }

    private void playerEnteredPortal(LobbyPlayer player) {
        BungeeUtils.redirectPlayer(player.getPlayer(), "minigame");
    }
}
