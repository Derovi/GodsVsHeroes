package by.dero.gvh.utils;

import org.bukkit.entity.Player;

public class MessagingUtils {
    public static String getNormal(String msg) {
        final int pos = msg.indexOf('§');
        if (pos != -1) {
            msg = msg.replace(msg.substring(pos, pos+1), "");
        }
        return msg;
    }

    public static void sendCooldownMessage(final Player player, final String itemName, final long time) {
        player.sendMessage(getNormal(("§6Перезагрузка. Подождите §6%time%§c секунд, чтобы использовать §6%itemname%§c!").
                replace("%itemname%", itemName).replace("%time%", String.valueOf(time))));
    }
}
