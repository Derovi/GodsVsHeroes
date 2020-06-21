package by.dero.gvh.minigame;

import by.dero.gvh.GamePlayer;
import by.dero.gvh.Plugin;
import by.dero.gvh.model.Lang;
import by.dero.gvh.utils.Board;
import by.dero.gvh.utils.HealthBar;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class DeathMatch extends Game {
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


    private HealthBar healthBar;
    @Override
    public void start() {
        super.start();
        board = new Board(Lang.get("game.livesLeft"), currentLivesCount.length);
        Bukkit.getServer().getScheduler().runTaskTimer(Plugin.getInstance(), ()->{
            String[] str = new String[currentLivesCount.length];
            for (int i = 0; i < currentLivesCount.length; i++) {
                final String col = String.valueOf((char)('1' + i));
                str[i] = Lang.get("commands.stat").replace("%col%", col)
                        .replace("%com%", Lang.get("commands." + col))
                        .replace("%pts%", String.valueOf(currentLivesCount[i]));
            }
            board.update(str);
        }, 0, 10);
        healthBar = new HealthBar(currentLivesCount.length);

        for (final GamePlayer gp : getPlayers().values()) {
            healthBar.addPlayer(gp.getPlayer());
        }
    }

    public DeathMatchInfo getDeathMatchInfo() {
        return deathMatchInfo;
    }

    @Override
    public void finish(int winnerTeam) {
        board.clear();
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
