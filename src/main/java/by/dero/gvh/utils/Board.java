package by.dero.gvh.utils;

import by.dero.gvh.GamePlayer;
import net.minecraft.server.v1_15_R1.IChatBaseComponent;
import net.minecraft.server.v1_15_R1.PacketPlayOutTitle;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Collection;

public class Board {
    private final Scoreboard scoreboard;
    private final Team[] teams;

    public Board(String name, int size) {
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective obj = scoreboard.registerNewObjective("ServerName", "dummy", name);
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        teams = new Team[size];
        for (int i = 0; i < size; i++) {
            teams[i] = scoreboard.registerNewTeam("" + i);
            String x = getNormal("§" + (char)('a' + i));
            teams[i].addEntry(x);
            obj.getScore(x).setScore(size-i);
        }
    }

    public static String getNormal(String msg) {
        int pos = msg.indexOf("§");
        if (pos != -1) {
            msg = msg.replace(msg.substring(pos, pos+1), "");
        }
        return msg;
    }

    public void update(String[] strings, int[] vars) {
        int pref = 0;
        for (int i = 0; i < strings.length; i++) {
            int len = StringUtils.countMatches(strings[i], "%");
            Object[] cur = new Object[len];
            for (int j = pref; j < pref + len; j++) {
                cur[j-pref] = vars[j];
            }
            teams[i].setPrefix(getNormal(String.format(strings[i], cur)));
            pref += len;
        }
    }

    public static void sendTitle(String text, Collection<GamePlayer> players) {
        PacketPlayOutTitle title = new PacketPlayOutTitle(
                PacketPlayOutTitle.EnumTitleAction.TITLE,
                IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + text + "\"}"),
                0, 20, 0);
        for (GamePlayer player : players) {
            ((CraftPlayer) player.getPlayer()).getHandle().playerConnection.sendPacket(title);
        }
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }
}
