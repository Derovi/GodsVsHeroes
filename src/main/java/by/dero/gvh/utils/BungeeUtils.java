package by.dero.gvh.utils;

import by.dero.gvh.Plugin;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import ru.cristalix.core.realm.RealmId;
import ru.cristalix.core.transfer.ITransferService;

public class BungeeUtils {
    public static void redirectPlayer(Player player, String serverName) {
        if (Plugin.getInstance().getSettings().isCristalix()) {
            ITransferService.get().transfer(player.getUniqueId(), RealmId.of(serverName));
        } else {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Connect");
            out.writeUTF(serverName);
            player.sendPluginMessage(Plugin.getInstance(), "BungeeCord", out.toByteArray());
        }
    }
}
