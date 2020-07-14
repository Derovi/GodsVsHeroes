package by.dero.gvh.lobby;

import by.dero.gvh.Plugin;
import by.dero.gvh.minigame.Game;
import by.dero.gvh.model.Lang;
import by.dero.gvh.model.ServerInfo;
import by.dero.gvh.model.ServerType;
import by.dero.gvh.utils.BridgeUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class PortalManager implements Listener {
    private final int timeout = 1500;
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
        String serverName = null;
        for (ServerInfo info : Plugin.getInstance().getServerData().getGameServers()) {
            if (info.getType() == ServerType.GAME && info.getStatus().equals(Game.State.WAITING.toString())) {
                serverName = info.getName();
                break;
            }
        }
        if (serverName == null) {
            player.getPlayer().sendMessage(Lang.get("lobby.busy"));
        }
        BridgeUtils.redirectPlayer(player.getPlayer(), serverName);
    }
}
