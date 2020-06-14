package by.dero.gvh.utils;

import org.bukkit.entity.Player;

public class MessagingUtils {
    public static String getNormal(String msg) {
        int pos = msg.indexOf("§");
        if (pos != -1) {
            msg = msg.replace(msg.substring(pos, pos+1), "");
        }
        return msg;
    }

    public static void sendCooldownMessage(Player player, String itemName, int time) {
        player.sendMessage(getNormal(("§cПерезарядка. Чтобы использовать предмет §6%itemname%§c подождите еще " +
                "§6%time%§c секунд.").
                replace("itemname", itemName).replace("%time%", String.valueOf(time))));
    }
}
