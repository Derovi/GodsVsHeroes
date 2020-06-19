package by.dero.gvh.utils;

import by.dero.gvh.model.Lang;
import org.bukkit.entity.Player;

public class MessagingUtils {
    public static void sendCooldownMessage(final Player player, final String itemName, final long time) {
        player.sendMessage(Lang.get("itemReloading").
                replace("%itemname%", itemName).replace("%time%", String.valueOf(time)));
    }
}
