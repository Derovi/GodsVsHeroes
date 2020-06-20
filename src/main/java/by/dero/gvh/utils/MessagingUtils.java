package by.dero.gvh.utils;

import by.dero.gvh.model.Lang;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

public class MessagingUtils {
    public static void sendCooldownMessage(final Player player, final String itemName, final long time) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Lang.get("game.itemReloadingActionBar").
                        replace("%itemname%", itemName).replace("%time%", getTimeString((int) time))));
    }

    public static String getTimeString(int sec) {
        if (sec > 10 && sec < 20) {
            return String.valueOf(sec) + ' ' + Lang.get("time.sec0");
        }
        if (sec % 10 == 2 || sec % 10 == 3 || sec % 10 == 4) {
            return String.valueOf(sec) + ' ' + Lang.get("time.sec1");
        }
        if (sec % 10 == 1) {
            return String.valueOf(sec) + ' ' + Lang.get("time.sec2");
        }
        return String.valueOf(sec) + ' ' + Lang.get("time.sec0");
    }
}
