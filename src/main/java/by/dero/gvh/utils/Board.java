package by.dero.gvh.utils;

import by.dero.gvh.lobby.Lobby;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_12_R1.scoreboard.CraftTeam;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class Board {
    @Getter private final CraftTeam[] teams;

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

        teams = new CraftTeam[size];
        for (int i = 0; i < size; i++) {
            teams[i] = (CraftTeam) scoreboard.registerNewTeam("" + counter++);
            final String x = "§" + (char)('a' + i);
            teams[i].addEntry(x);
            obj.getScore(x).setScore(size-i);
        }
    }
    
    public static void setText(final CraftTeam team, final String str) {
        team.team.setPrefix(str);
    }
    
    public void setAt(int idx, String str) {
        setText(teams[idx], str);
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
