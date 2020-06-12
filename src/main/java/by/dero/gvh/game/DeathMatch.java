package by.dero.gvh.game;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

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
        if (getState() != State.GAME) {
            return;
        }
        --currentLivesCount[getPlayers().get(event.getEntity().getName()).getTeam()];
        checkForGameEnd();
    }
}
