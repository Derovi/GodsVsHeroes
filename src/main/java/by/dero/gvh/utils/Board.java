package by.dero.gvh.utils;

import by.dero.gvh.GamePlayer;
import net.minecraft.server.v1_15_R1.IChatBaseComponent;
import net.minecraft.server.v1_15_R1.PacketPlayOutTitle;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Collection;

public class Board {
    private final Team[] teams;

    private static Scoreboard scoreboard = null;
    private static int counter = 123;
    public Board(final String name, final int size) {
        scoreboard = getScoreboard();
        for (final Objective obj : scoreboard.getObjectives()) {
            obj.unregister();
        }

        final Objective obj = scoreboard.registerNewObjective("ServerName", "dummy", name);
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        teams = new Team[size];
        for (int i = 0; i < size; i++) {
            teams[i] = scoreboard.registerNewTeam("" + counter++);
            final String x = "§" + (char)('a' + i);
            teams[i].addEntry(x);
            obj.getScore(x).setScore(size-i);
        }
    }

    public void update(final String[] strings) {
        for (int i = 0; i < strings.length; i++) {
            teams[i].setPrefix(strings[i]);
        }
    }

    public static Scoreboard getScoreboard() {
        if (scoreboard == null) {
            scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        }
        return scoreboard;
    }

    public void clear() {
        for (final Team team : getScoreboard().getTeams()) {
            team.unregister();
        }
        for (final Objective obj : getScoreboard().getObjectives()) {
            obj.unregister();
        }
    }
}
