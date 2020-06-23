package by.dero.gvh.utils;

import org.bukkit.Bukkit;
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

    public void update(final String[] strings) {
        for (int i = 0; i < strings.length; i++) {
            teams[i].setPrefix(strings[i]);
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
