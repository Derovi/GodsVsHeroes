package by.dero.gvh.lobby;

import by.dero.gvh.Plugin;
import by.dero.gvh.minigame.Game;
import by.dero.gvh.model.Lang;
import by.dero.gvh.model.ServerInfo;
import by.dero.gvh.model.ServerType;
import by.dero.gvh.utils.BridgeUtils;
import by.dero.gvh.utils.DirectedPosition;
import by.dero.gvh.utils.Pair;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class PortalManager implements Listener {
    private final int timeout = 1500;
    private final HashMap<String, Long> playersInPortal = new HashMap<>();

    public PortalManager() {
        new BukkitRunnable() {
            @Override
            public void run() {
                playersInPortal.entrySet().removeIf(e -> System.currentTimeMillis() - e.getValue() > timeout);
            }
        }.runTaskTimer(Plugin.getInstance(), 20, 20);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (playersInPortal.containsKey(event.getPlayer().getName())) {
            return;
        }
        LobbyPlayer player = Lobby.getInstance().getPlayers().get(event.getPlayer().getName());
        
        PlayerLobby playerLobby = Lobby.getInstance().getActiveLobbies().get(player.getPlayer().getName());
        Pair<String, DirectedPosition> portal = playerLobby.getPortal();
        if (portal != null) {
            playersInPortal.put(event.getPlayer().getName(), System.currentTimeMillis());
            playerEnteredPortal(player, portal.getKey());
        }
    }

    private void playerEnteredPortal(LobbyPlayer player, String mode) {
        String serverName = null;
        for (ServerInfo info : Plugin.getInstance().getServerData().getSavedGameServers()) {
            if (info.getType() == ServerType.GAME &&
                    info.getMode().equals(mode) &&
                    info.getStatus().equals(Game.State.WAITING.toString()) &&
                    info.getMaxOnline() > info.getOnline()) {
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
