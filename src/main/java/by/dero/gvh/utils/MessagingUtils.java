package by.dero.gvh.utils;

import by.dero.gvh.GamePlayer;
import by.dero.gvh.model.Lang;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_12_R1.IChatBaseComponent;
import net.minecraft.server.v1_12_R1.PacketPlayOutTitle;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Collection;

public class MessagingUtils {
//    public static void sendStunned(Player player, int duration) {
//        sendSubtitle(Lang.get("game.stunMessage"), player, 0, duration, 0);
//    }
    
    public static void sendCooldownMessage(final Player player, final String itemName, final long time) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Lang.get("game.itemReloadingActionBar").
                        replace("%itemname%", itemName).replace("%time%", getTimeString((int) time, true))));
    }

    public static void sendTitle(final String text, final Player player, int c, int d, int e) {
        final PacketPlayOutTitle title = new PacketPlayOutTitle(
                PacketPlayOutTitle.EnumTitleAction.TITLE,
                IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + text + "\"}"),
                c, d, e);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(title);
    }

    public static void sendTitle(final String title, String subtitle, final Player player, int c, int d, int e) {
        final PacketPlayOutTitle titlePacket = new PacketPlayOutTitle(
                PacketPlayOutTitle.EnumTitleAction.TITLE,
                IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + title + "\"}"),
                c, d, e);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(titlePacket);
        final PacketPlayOutTitle subtitlePacket = new PacketPlayOutTitle(
                PacketPlayOutTitle.EnumTitleAction.SUBTITLE,
                IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + subtitle + "\"}"),
                c, d, e);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(subtitlePacket);
    }

    public static void sendSubtitle(final String text, final Player player, int c, int d, int e) {
        final PacketPlayOutTitle title = new PacketPlayOutTitle(
                PacketPlayOutTitle.EnumTitleAction.TITLE,
                IChatBaseComponent.ChatSerializer.a("{\"text\":\"\"}"),
                c, d, e);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(title);
        final PacketPlayOutTitle subtitle = new PacketPlayOutTitle(
                PacketPlayOutTitle.EnumTitleAction.SUBTITLE,
                IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + text + "\"}"),
                c, d, e);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(subtitle);
    }

    public static void sendActionBar(final String text, final Player player) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(text));
    }

    public static void sendTitle(final String text, final Collection<GamePlayer> players) {
        final PacketPlayOutTitle title = new PacketPlayOutTitle(
                PacketPlayOutTitle.EnumTitleAction.TITLE,
                IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + text + "\"}"),
                0, 20, 0);
        for (final GamePlayer player : players) {
            ((CraftPlayer) player.getPlayer()).getHandle().playerConnection.sendPacket(title);
        }
    }

    public static String getTimeString(int sec, boolean mode) {
        if (sec > 10 && sec < 20) {
            return String.valueOf(sec) + ' ' + Lang.get("time.sec0");
        }
        if (sec % 10 == 2 || sec % 10 == 3 || sec % 10 == 4) {
            return String.valueOf(sec) + ' ' + Lang.get("time.sec1");
        }
        if (sec % 10 == 1) {
            if (mode) {
                return String.valueOf(sec) + ' ' + Lang.get("time.sec2");
            } else {
                return String.valueOf(sec) + ' ' + Lang.get("time.sec3");
            }
        }
        return String.valueOf(sec) + ' ' + Lang.get("time.sec0");
    }
}
