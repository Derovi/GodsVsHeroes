package by.dero.gvh.minigame;

import by.dero.gvh.GamePlayer;
import by.dero.gvh.Plugin;
import by.dero.gvh.model.Lang;
import by.dero.gvh.utils.Board;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

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
        board = new Board(Lang.get("game.livesLeft"), currentLivesCount.length);
        Bukkit.getServer().getScheduler().runTaskTimer(Plugin.getInstance(), ()->{
            String[] str = new String[currentLivesCount.length];
            for (int i = 0; i < currentLivesCount.length; i++) {
                str[i] = "§" + (char)('a' + i) + "Command " + (i + 1) + " : §f" + currentLivesCount[i];
            }
            board.update(str);
        }, 0, 10);
        for (final GamePlayer gp : getPlayers().values()) {
            final Player player = gp.getPlayer();
            player.setScoreboard(board.getScoreboard());
            player.setDisplayName("§" + (char)('a' + gp.getTeam()) + player.getName());
        }
    }

    public DeathMatchInfo getDeathMatchInfo() {
        return deathMatchInfo;
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
