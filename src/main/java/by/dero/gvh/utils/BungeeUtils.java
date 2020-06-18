package by.dero.gvh.utils;

import by.dero.gvh.Plugin;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;

public class BungeeUtils {
    public static void redirectPlayer(Player player, String serverName) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(serverName);
        player.sendPluginMessage(Plugin.getInstance(), "BungeeCord", out.toByteArray());
    }
}
