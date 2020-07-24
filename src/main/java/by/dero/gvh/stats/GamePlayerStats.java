package by.dero.gvh.stats;

import lombok.Getter;
import lombok.Setter;

public class GamePlayerStats {
    @Getter private final String name;

    @Getter @Setter private String className;

    public GamePlayerStats(String name, String className, int team) {
        this.name = name;
        this.className = className;
        this.team = team;
    }

    @Getter @Setter private int kills = 0;

    @Getter @Setter private int deaths = 0;

    @Getter @Setter private int assists = 0;

    @Getter @Setter private double damageTaken = 0;

    @Getter @Setter private double damageDealt = 0;

    @Getter @Setter private int expGained = 0;

    @Getter @Setter private int capturePoints = 0;

    @Getter @Setter private int team = 0;

    @Getter @Setter private int playTimeSec = 0;
    
    @Getter @Setter private int teamHeal = 0;
}
