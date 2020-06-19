package by.dero.gvh.utils;

import org.bukkit.entity.Player;

public class MessagingUtils {
    public static void sendCooldownMessage(final Player player, final String itemName, final long time) {
        player.sendMessage(("§6Перезагрузка. Подождите §6%time%§c секунд, чтобы использовать §6%itemname%§c!").
                replace("%itemname%", itemName).replace("%time%", String.valueOf(time)));
    }
}
