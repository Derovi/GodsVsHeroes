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

    public Board(final String name, final int size) {
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        final Objective obj = scoreboard.registerNewObjective("ServerName", "dummy", name);
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        teams = new Team[size];
        for (int i = 0; i < size; i++) {
            teams[i] = scoreboard.registerNewTeam("" + i);
            final String x = MessagingUtils.getNormal("§" + (char)('a' + i));
            teams[i].addEntry(x);
            obj.getScore(x).setScore(size-i);
        }
    }

    public void update(final String[] strings, final int[] vars) {
        int pref = 0;
        for (int i = 0; i < strings.length; i++) {
            final int len = StringUtils.countMatches(strings[i], "%");
            Object[] cur = new Object[len];
            for (int j = pref; j < pref + len; j++) {
                cur[j-pref] = vars[j];
            }
            teams[i].setPrefix(MessagingUtils.getNormal(String.format(strings[i], cur)));
            pref += len;
        }
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

    public Scoreboard getScoreboard() {
        return scoreboard;
    }
}
