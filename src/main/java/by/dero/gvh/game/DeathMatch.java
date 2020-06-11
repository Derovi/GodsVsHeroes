package by.dero.gvh.game;

public class DeathMatch extends Game {
    private DeathMatchInfo deathMatchInfo;

    public DeathMatch(GameInfo info, DeathMatchInfo deathMatchInfo) {
        super(info);
        this.deathMatchInfo = deathMatchInfo;
    }
}
