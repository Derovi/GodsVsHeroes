package by.dero.gvh.minigame;

import by.dero.gvh.GamePlayer;
import by.dero.gvh.model.Lang;
import by.dero.gvh.model.interfaces.DisplayInteractInterface;
import by.dero.gvh.utils.Board;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;

import static by.dero.gvh.utils.DataUtils.getPlayer;

public class DeathMatch extends Game implements DisplayInteractInterface {
    private final DeathMatchInfo deathMatchInfo;

    int[] currentLivesCount;

    public DeathMatch(GameInfo info, DeathMatchInfo deathMatchInfo) {
        super(info);
        this.deathMatchInfo = deathMatchInfo;
    }

    @Override
    void load() {
        currentLivesCount = new int[getInfo().getTeamCount()];
        for (int index = 0; index < getInfo().getTeamCount(); ++index) {
            currentLivesCount[index] = this.deathMatchInfo.getLivesCount();
        }
    }

    @Override
    public void setDisplays() {
        for (final GamePlayer gp : getPlayers().values()) {
            final Board board = new Board(Lang.get("game.livesLeft"),currentLivesCount.length + 1);
            gp.setBoard(board);
            final Scoreboard sb = board.getScoreboard();
            for (int team = 0; team < currentLivesCount.length; team++) {
                final String t = team + "hp";
                if (sb.getTeam(t) != null) {
                    sb.getTeam(t).unregister();
                }
                sb.registerNewTeam(t).setColor(
                        ChatColor.getByChar(Lang.get("commands." + (char)('1' + team)).charAt(1)));
            }
            if (sb.getObjective("health") != null) {
                sb.getObjective("health").unregister();
            }
            final Objective obj = sb.registerNewObjective("health", "health", "health");
            obj.setDisplayName("§c❤");
            obj.setDisplaySlot(DisplaySlot.BELOW_NAME);

            for (final GamePlayer othergp : getPlayers().values()) {
                final String name = othergp.getPlayer().getName();
                sb.getTeam(othergp.getTeam() + "hp").addEntry(name);
            }
        }
    }

    @Override
    public void updateDisplays() {
        ArrayList<Integer> idxs = new ArrayList<>();
        for (int i = 0; i < currentLivesCount.length; i++) {
            idxs.add(i);
        }
        idxs.sort((a, b) -> currentLivesCount[b] - currentLivesCount[a]);
        String[] str = new String[currentLivesCount.length + 1];
        for (int zxc = 0; zxc < currentLivesCount.length; zxc++) {
            final int i = idxs.get(zxc);
            final String com = Lang.get("commands." + (char)('1' + i));
            str[i] = Lang.get("commands.stat").replace("%col%", String.valueOf(com.charAt(1)))
                    .replace("%com%", com)
                    .replace("%pts%", String.valueOf(currentLivesCount[i]));
        }
        for (final GamePlayer gp : getPlayers().values()) {
            str[currentLivesCount.length] = Lang.get("commands.playingFor").
                    replace("%com%", Lang.get("commands." + (char)('1' + gp.getTeam())));
            gp.getBoard().update(str);
        }
    }

    @Override
    public void start() {
        super.start();

        setDisplays();
        updateDisplays();
    }

    public DeathMatchInfo getDeathMatchInfo() {
        return deathMatchInfo;
    }

    @Override
    public void finish(int winnerTeam) {
        for (final GamePlayer gp : getPlayers().values()) {
            gp.getBoard().clear();
        }
        super.finish(winnerTeam);
    }

    private void checkForGameEnd() {
        int winner = -1;
        for (int index = 0; index < getInfo().getTeamCount(); ++index) {
            if (currentLivesCount[index] != 0) {
                if (winner != -1) {
                    return;
                } else {
                    winner = index;
                }
            }
        }
        for (final Player player : Bukkit.getServer().getOnlinePlayers()) {
            player.setCustomNameVisible(false);
        }
        finish(winner);
    }

    @EventHandler
    public void onPlayerDie(PlayerDeathEvent event) {
        updateDisplays();
        System.out.println("Player die!");
        if (getState() != State.GAME) {
            return;
        }
        --currentLivesCount[getPlayers().get(event.getEntity().getName()).getTeam()];
        checkForGameEnd();
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (getState() != State.GAME) {
            event.setCancelled(true);
        }
    }
}
