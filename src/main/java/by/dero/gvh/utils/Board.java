package by.dero.gvh.utils;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_12_R1.scoreboard.CraftTeam;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class Board {
    private final Team[] teams;

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    private static int counter = 123;
    public static int getCounter() {
        return counter++;
    }
    private final Scoreboard scoreboard;

    public Board(final String name, final int size) {
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        for (final Objective obj : scoreboard.getObjectives()) {
            obj.unregister();
        }

        final Objective obj = scoreboard.registerNewObjective(name, "dummy");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        teams = new Team[size];
        for (int i = 0; i < size; i++) {
            teams[i] = scoreboard.registerNewTeam("" + counter++);
            final String x = "§" + (char)('a' + i);
            teams[i].addEntry(x);
            obj.getScore(x).setScore(size-i);
        }
    }

    public static void setText(final Team team, final String str) {
        ((CraftTeam) team).team.setSuffix(" ");
        if (str.length() > 16) {
            int idx = -1;
            for (int i = 0; i < 16; i++) {
                if (str.charAt(i) == '§') {
                    idx = i;
                }
            }
            if (idx == -1) {
                ((CraftTeam) team).team.setPrefix(str.substring(0, 16));
                ((CraftTeam) team).team.setSuffix(str.substring(16));
            } else
            if (idx < 15) {
                ((CraftTeam) team).team.setPrefix(str.substring(0, 16));
                ((CraftTeam) team).team.setSuffix("§" + str.charAt(idx+1) + str.substring(16));
            } else {
                ((CraftTeam) team).team.setPrefix(str.substring(0, idx));
                ((CraftTeam) team).team.setSuffix(str.substring(idx));
            }
        } else {
            ((CraftTeam) team).team.setPrefix(str);
        }
    }

    public void update(final String[] strings) {
        for (int i = 0; i < strings.length; i++) {
            setText(teams[i], strings[i]);
        }
    }

    public void clear() {
        for (final Team team : scoreboard.getTeams()) {
            team.unregister();
        }
        for (final Objective obj : scoreboard.getObjectives()) {
            obj.unregister();
        }
    }
}
