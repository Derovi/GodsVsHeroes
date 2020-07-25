package by.dero.gvh.stats;

import by.dero.gvh.GamePlayer;
import by.dero.gvh.minigame.Game;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

public class GameStats {
    @SerializedName("_id")
    @Getter private int id;

    @Getter
    private final HashMap<String, GamePlayerStats> players = new HashMap<>();

    @Getter @Setter
    private int gameDurationSec;

    @Getter @Setter
    private int wonTeam;
    
    @Getter
    private String date;
    
    public GameStats() {
        GameStatsUtils.gameStats = this;
        GameStatsUtils.startTime = System.currentTimeMillis() / 1000;
        for (GamePlayer gp : Game.getInstance().getPlayers().values()) {
            players.put(gp.getPlayer().getName(),
                    new GamePlayerStats(gp.getPlayer().getName(), gp.getClassName(), gp.getTeam()));
        }
    }
}
