package by.dero.gvh.stats;

import by.dero.gvh.GamePlayer;
import by.dero.gvh.utils.GameUtils;
import lombok.Getter;
import lombok.Setter;

public class GamePlayerStats {
    @Getter
    private final String name;

    @Getter
    @Setter
    private String className;

    public GamePlayerStats(String name) {
        this.name = name;
        GamePlayer gp = GameUtils.getPlayer(name);
        className = gp.getClassName();
        team = gp.getTeam();
    }

    @Getter
    @Setter
    private int kills = 0;

    @Getter
    @Setter
    private int deaths = 0;

    @Getter
    @Setter
    private int assists = 0;

    @Getter
    @Setter
    private double damageTaken = 0;

    @Getter
    @Setter
    private double damageDealt = 0;

    @Getter
    @Setter
    private int expGained = 0;

    @Getter
    @Setter
    private int capturePoints = 0;

    @Getter
    @Setter
    private int team = 0;

    @Getter
    @Setter
    private int playTimeSec;
}
