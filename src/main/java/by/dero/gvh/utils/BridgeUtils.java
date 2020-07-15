package by.dero.gvh.utils;

import by.dero.gvh.Plugin;
import by.dero.gvh.model.ServerInfo;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import ru.cristalix.core.realm.IRealmService;
import ru.cristalix.core.realm.RealmId;
import ru.cristalix.core.realm.RealmStatus;
import ru.cristalix.core.transfer.ITransferService;

public class BridgeUtils {
    public static void toLobby(Player player) {
        toLobby(player, null);
    }

    public static void toLobby(Player player, String message) {
        ServerInfo lobbyServer = Plugin.getInstance().getServerData().getSavedLobbyServer();
        if (lobbyServer != null) {
            if (message != null) {
                player.sendMessage(message);
            }
            BridgeUtils.redirectPlayer(player, lobbyServer.getName());
        } else {
            player.kickPlayer(message == null ? "" : message);
        }
    }

    public static void redirectPlayer(Player player, String serverName) {
        ServerInfo lobbyServer = Plugin.getInstance().getServerData().getSavedLobbyServer();
        if (lobbyServer.getName().equals(serverName)) {
            if (lobbyServer.getOnline() >= lobbyServer.getMaxOnline()) {
                return;
            }
            lobbyServer.setOnline(lobbyServer.getOnline() + 1);
        } else
        for (ServerInfo info : Plugin.getInstance().getServerData().getSavedGameServers()) {
            if (info.getName().equals(serverName)) {
                if (info.getOnline() >= info.getMaxOnline()) {
                    return;
                }
                info.setOnline(info.getOnline() + 1);
            }
        }
        if (Plugin.getInstance().getSettings().isCristalix()) {
            if (IRealmService.get().getRealmById(RealmId.of(serverName)).getStatus().equals(RealmStatus.WAITING_FOR_PLAYERS)) {
                ITransferService.get().transfer(player.getUniqueId(), RealmId.of(serverName));
            }
        } else {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Connect");
            out.writeUTF(serverName);
            player.sendPluginMessage(Plugin.getInstance(), "BungeeCord", out.toByteArray());
        }
    }
}
