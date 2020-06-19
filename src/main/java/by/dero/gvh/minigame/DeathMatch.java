package by.dero.gvh.minigame;

import by.dero.gvh.GamePlayer;
import by.dero.gvh.Plugin;
import by.dero.gvh.utils.Board;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

import static by.dero.gvh.utils.MessagingUtils.getNormal;

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
        board = new Board("Lives Left", currentLivesCount.length);
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
            player.setDisplayName(getNormal("§" + ((char)('a' + gp.getTeam())) + player.getDisplayName()));
        }
    }

    public DeathMatchInfo getDeathMatchInfo() {
        return deathMatchInfo;
    }

    private void checkForGameEnd() {
        for (int index = 0; index < getInfo().getTeamCount(); ++index) {
            if (currentLivesCount[index] == 0) {
                finish(index);
                return;
            }
        }
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
}
