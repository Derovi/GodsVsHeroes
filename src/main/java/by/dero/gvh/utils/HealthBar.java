package by.dero.gvh.utils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import static by.dero.gvh.utils.DataUtils.getPlayer;

public class HealthBar {
    private final Scoreboard board;

    public HealthBar(final int teams) {
        board = Board.getScoreboard();
        for (int team = 0; team < teams; team++) {
            final String t = team + "hp";
            if (board.getTeam(t) != null) {
                board.getTeam(t).unregister();
            }
            board.registerNewTeam(t).setColor(ChatColor.getByChar((char)('a' + team)));
        }
        if (board.getObjective("health") != null) {
            board.getObjective("health").unregister();
        }
        final Objective obj = board.registerNewObjective("health", "health", "health");
        obj.setDisplayName("§c❤");
        obj.setDisplaySlot(DisplaySlot.BELOW_NAME);
    }

    public void addPlayer(Player player) {
        final String name = player.getName();
        player.setScoreboard(board);
        board.getTeam(getPlayer(name).getTeam() + "hp").addEntry(name);
    }
}
